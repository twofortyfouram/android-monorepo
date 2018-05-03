/*
 * android-plugin-sdk-for-locale
 * https://github.com/twofortyfouram/android-plugin-sdk-for-locale
 * Copyright (C) 2008–2018 two forty four a.m. LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.twofortyfouram.locale.sdk.host.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.locale.sdk.host.internal.PluginRegistryHandler;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.StrictModeCompat;
import com.twofortyfouram.spackle.ThreadUtil;
import com.twofortyfouram.spackle.ThreadUtil.ThreadPriority;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Dynamically discovers all {@link Plugin}s currently installed. Call {@link
 * #getInstance(Context)}
 * to obtain the singleton
 * instance of this class. Initially loading the plug-ins can take a few
 * seconds, so there are both blocking {@link #getPluginMap(PluginType)} and
 * non-blocking {@link #peekPluginMap(PluginType)} calls to retrieve a snapshot
 * the registry.
 * <p>
 * The registry will automatically keep itself up to date, and clients of this
 * class can monitor for changes via the Intent action returned by
 * {@link #getChangeIntentAction()}. When the registry changes, the snapshots
 * previously retrieved from the registry will not change. To get the latest
 * changes, call {@link #getPluginMap(PluginType)} or
 * {@link #peekPluginMap(PluginType)} again.
 */
@ThreadSafe
public final class PluginRegistry {

    /**
     * Lock to synchronize initialization of {@link #sRegistry}.
     */
    @NonNull
    private static final Object LAZY_INITIALIZATION_INTRINSIC_LOCK = new Object();

    /**
     * The singleton registry instance.
     * <p/>
     * This field is lazily initialized by {@link #getInstance(Context)}.
     */
    @SuppressWarnings("StaticNonFinalField")
    @GuardedBy("LAZY_INITIALIZATION_INTRINSIC_LOCK")
    @Nullable
    private static volatile PluginRegistry sRegistry = null;

    /**
     * @param context Application Context.
     * @return the singleton instance of the registry.
     */
    @NonNull
    public static PluginRegistry getInstance(@NonNull final Context context) {
        /*
         * Note: this method may be called from any thread.
         */

        final Context ctx = ContextUtil.cleanContext(context);

        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd
         * edition page 283.
         */
        @SuppressWarnings("FieldAccessNotGuarded") PluginRegistry registry = sRegistry;
        if (null == registry) {
            synchronized (LAZY_INITIALIZATION_INTRINSIC_LOCK) {
                registry = sRegistry;
                if (null == registry) {
                    final String changeAction = String.format(Locale.US, ACTION_REGISTRY_CHANGED,
                            android.os.Process.myPid());

                    sRegistry = registry = new PluginRegistry(ctx, changeAction);
                    registry.init();
                }
            }
        }

        return registry;
    }

    /**
     * {@code Intent} action broadcast when the registry has changed, suffixed
     * with the current PID. The PID suffix allows for multiple
     * {@link PluginRegistry} classes in different processes to avoid
     * interfering with each other.
     */
    @NonNull
    private static final String ACTION_REGISTRY_CHANGED
            = "com.twofortyfouram.locale.intent.action.PLUGIN_REGISTRY_CHANGED:%d"; //$NON-NLS-1$

    /**
     * Background thread used by the registry.
     */
    @NonNull
    private final HandlerThread mHandlerThread = ThreadUtil.newHandlerThread(
            PluginRegistryHandler.class.getName(),
            ThreadPriority.BACKGROUND);

    /**
     * Handler to queue and process changes to installed plug-ins.
     */
    @NonNull
    private final com.twofortyfouram.locale.sdk.host.internal.PluginRegistryHandler mHandler;

    /**
     * Latch that is released when the registry is fully loaded.
     */
    @NonNull
    private final CountDownLatch mLoadLatch = new CountDownLatch(1);

    /**
     * Intent action broadcast when the registry changes.
     *
     * @see #getChangeIntentAction()
     */
    @NonNull
    private final String mRegistryChangeAction;

    /**
     * Permission for securing {@link #mRegistryChangeAction}.
     */
    @NonNull
    private final String mRegistryChangePermission;

    /**
     * Constructs a new Registry.
     *
     * @param context            Application context.
     * @param notificationAction Intent action to broadcast when the registry
     *                           changes.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    /* package */PluginRegistry(@NonNull final Context context,
            @NonNull final String notificationAction) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(notificationAction, "notificationAction"); //$NON-NLS-1$

        final Context ctx = ContextUtil.cleanContext(context);

        mRegistryChangeAction = notificationAction;
        mRegistryChangePermission = getInternalPermission(ctx);

        mHandler = new com.twofortyfouram.locale.sdk.host.internal.PluginRegistryHandler(
                mHandlerThread.getLooper(), ctx,
                mRegistryChangeAction, mRegistryChangePermission);
    }

    /**
     * After construction, this method is called to begin initializing the
     * registry on a background thread.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    /* package */void init() {
        final boolean isSuccessful = mHandler.sendMessage(mHandler.obtainMessage(
                PluginRegistryHandler.MESSAGE_INIT, mLoadLatch));

        if (!isSuccessful) {
            throw new AssertionError();
        }
    }

    /**
     * Blocks until the registry is initially loaded.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    /* package */void blockUntilLoaded() {
        try {
            mLoadLatch.await();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down this instance of the registry. This method is only for special
     * circumstances, such as when creating a temporary registry in unit tests.
     * Normally, the registry is a long-lived singleton object obtained through
     * {@link #getInstance(Context)}. This method cannot be used on that
     * singleton instance.
     *
     * @throws UnsupportedOperationException If this instance of the registry is
     *                                       the same instance returned by {@link #getInstance(Context)}.
     */
    @SuppressLint("NewApi")
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    /* package */ void destroy() {
        // OK to suppress inspection; if this reference is the singleton, then sRegistry is
        // guaranteed to have been assigned.
        //noinspection FieldAccessNotGuarded,ObjectEquality
        if (this == sRegistry) {
            throw new UnsupportedOperationException();
        }

        synchronized (this) {
            final boolean isSuccessful = mHandler
                    .sendEmptyMessage(PluginRegistryHandler.MESSAGE_DESTROY);
            if (!isSuccessful) {
                throw new AssertionError();
            }

            if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                mHandlerThread.quitSafely();
            } else {
                mHandlerThread.quit();
                mLoadLatch.countDown();
            }
        }
    }

    /**
     * The registry broadcasts this Intent action when the registry changes,
     * allowing clients to implement an observer design pattern.
     *
     * @return the Intent action broadcast when the registry changes.
     * @see #getChangeIntentPermission()
     */
    @NonNull
    public String getChangeIntentAction() {
        return mRegistryChangeAction;
    }

    /**
     * @return Permission string protecting {@link #getChangeIntentAction()}.
     */
    @NonNull
    public String getChangeIntentPermission() {
        return mRegistryChangePermission;
    }

    /**
     * Retrieves a snapshot of the registry's latest state, blocking until the
     * registry is loaded. Once the registry is initially loaded, this method
     * will no longer block.
     * <p>
     * Return values of this method are snapshots and will not change. If the
     * Intent from {@link #getChangeIntentAction()} is broadcast, call
     * {@code getPluginMap(PluginType)} again to get an updated snapshot.
     *
     * @param type The type of registry information to return.
     * @return A snapshot of the current registry state, which is a map of
     * {@link Plugin#getRegistryName()} keys to {@link Plugin}
     * instances. Clients MUST NOT attempt to modify the map.
     */
    @NonNull
    public Map<String, Plugin> getPluginMap(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$

        StrictModeCompat.noteSlowCall(
                "Call getPluginMap(PluginType).  Use peekPluginMap(PluginType) for performance critical sections of code"); //$NON-NLS-1$

        blockUntilLoaded();

        // Guaranteed to not be null, because blockUntilLoaded() ensures the register is loaded.
        //noinspection ConstantConditions
        return peekPluginMap(type);
    }

    /**
     * Retrieves a snapshot of the registry's latest state or {@code null} if
     * the registry isn't loaded yet. Once the registry is initially loaded,
     * this method will no longer return {@code null}.
     * <p>
     * Return values of this method are snapshots and will not change. If the
     * Intent from {@link #getChangeIntentAction()} is broadcast, call
     * {@code peekPluginMap(PluginType)} again to get an updated snapshot.
     *
     * @param type The type of registry information to return.
     * @return A snapshot of the current registry state, which is a map of
     * {@link Plugin#getRegistryName()} keys to {@link Plugin}
     * instances. This may return null if
     * the registry isn't loaded yet. Clients MUST NOT attempt to modify the map.
     */
    @Nullable
    public Map<String, Plugin> peekPluginMap(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$

        final Map<String, Plugin> result;
        switch (type) {
            case CONDITION: {
                result = mHandler.getConditions();
                break;
            }
            case SETTING: {
                result = mHandler.getSettings();
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return result;
    }

    /**
     * @param context Application context.
     * @return An internal permission used by the SDK.
     */
    @NonNull
    private static String getInternalPermission(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        return context.getPackageName()
                + ".com.twofortyfouram.locale.sdk.host.permission.internal"; //$NON-NLS-1$
    }

}
