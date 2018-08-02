/*
 * android-plugin-host-sdk-for-locale
 * https://github.com/twofortyfouram/android-monorepo
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

package com.twofortyfouram.locale.sdk.host.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.twofortyfouram.locale.sdk.host.api.PluginRegistry;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Call {@link #getInstance(Context)} to obtain the singleton instance of this class.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@ThreadSafe
public final class ThirdPartyPluginRegistry implements PluginRegistry {

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
    private static volatile ThirdPartyPluginRegistry sRegistry = null;

    /**
     * @param context Application Context.
     * @return the singleton instance of the registry.
     */
    @AnyThread
    @NonNull
    public static ThirdPartyPluginRegistry getInstance(@NonNull final Context context) {
        /*
         * Note: this method may be called from any thread.
         */

        final Context ctx = ContextUtil.cleanContext(context);

        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd
         * edition page 283.
         */
        @SuppressWarnings("FieldAccessNotGuarded") ThirdPartyPluginRegistry registry = sRegistry;
        if (null == registry) {
            //noinspection SynchronizationOnStaticField
            synchronized (LAZY_INITIALIZATION_INTRINSIC_LOCK) {
                registry = sRegistry;
                if (null == registry) {
                    final String changeAction = String.format(Locale.US, ACTION_REGISTRY_CHANGED,
                            android.os.Process.myPid());

                    sRegistry = registry = new ThirdPartyPluginRegistry(ctx, changeAction);
                    registry.init();
                }
            }
        }

        return registry;
    }

    /**
     * {@code Intent} action broadcast when the registry has changed, suffixed
     * with the current PID. The PID suffix allows for multiple
     * {@link ThirdPartyPluginRegistry} classes in different processes to avoid
     * interfering with each other.
     */
    @NonNull
    private static final String ACTION_REGISTRY_CHANGED
            = "com.twofortyfouram.locale.intent.action.PLUGIN_REGISTRY_CHANGED:%d"; //$NON-NLS-1$

    /**
     * Background thread used by the registry.
     */
    @NonNull
    private final HandlerThread mHandlerThread;

    /**
     * Handler to queue and process changes to installed plug-ins.
     */
    @NonNull
    private final PluginRegistryHandlerCallback mHandlerCallback;

    /**
     * Handler running {@link #mHandlerCallback}.
     */
    @NonNull
    private final Handler mHandler;

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
    @VisibleForTesting
    /* package */ThirdPartyPluginRegistry(@NonNull final Context context,
            @NonNull final String notificationAction) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(notificationAction, "notificationAction"); //$NON-NLS-1$

        final Context ctx = ContextUtil.cleanContext(context);

        mRegistryChangeAction = notificationAction;
        mRegistryChangePermission = getInternalPermission(ctx);

        mHandlerCallback = new PluginRegistryHandlerCallback(ctx,
                mRegistryChangeAction, mRegistryChangePermission);

        mHandlerThread = HandlerThreadFactory.newHandlerThread(
                PluginRegistryHandlerCallback.class.getName(),
                ThreadPriority.BACKGROUND);

        mHandler = new Handler(mHandlerThread.getLooper(), mHandlerCallback);
    }

    /**
     * After construction, this method is called to begin initializing the
     * registry on a background thread.
     */
    @VisibleForTesting
    /* package */void init() {
        final boolean isSuccessful = mHandler.sendMessage(mHandler.obtainMessage(
                PluginRegistryHandlerCallback.MESSAGE_INIT,
                new PluginRegistryHandlerCallback.InitObj(mHandler, mLoadLatch)));

        if (!isSuccessful) {
            throw new AssertionError();
        }
    }

    /**
     * Blocks until the registry is initially loaded.
     */
    @VisibleForTesting
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
    @RestrictTo(RestrictTo.Scope.TESTS)
    /* package */ void destroy() {
        // OK to suppress inspection; if this reference is the singleton, then sRegistry is
        // guaranteed to have been assigned.
        //noinspection FieldAccessNotGuarded,ObjectEquality
        if (this == sRegistry) {
            throw new UnsupportedOperationException();
        }

        //noinspection SynchronizeOnThis
        synchronized (this) {
            final boolean isSuccessful = mHandler
                    .sendEmptyMessage(PluginRegistryHandlerCallback.MESSAGE_DESTROY);
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

    @NonNull
    @Override
    public String getChangeIntentAction() {
        return mRegistryChangeAction;
    }

    @NonNull
    @Override
    public String getChangeIntentPermission() {
        return mRegistryChangePermission;
    }

    @NonNull
    @Override
    public Map<String, IPlugin> getPluginMap(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$

        StrictMode.noteSlowCall(
                "Call getPluginMap(PluginType).  Use peekPluginMap(PluginType) for performance critical sections of code"); //$NON-NLS-1$

        blockUntilLoaded();

        // Guaranteed to not be null, because blockUntilLoaded() ensures the register is loaded.
        //noinspection ConstantConditions
        return peekPluginMap(type);
    }

    @Nullable
    @Override
    public Map<String, IPlugin> peekPluginMap(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$

        final Map<String, IPlugin> result;
        switch (type) {
            case CONDITION: {
                result = mHandlerCallback.getConditions();
                break;
            }
            case SETTING: {
                result = mHandlerCallback.getSettings();
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
