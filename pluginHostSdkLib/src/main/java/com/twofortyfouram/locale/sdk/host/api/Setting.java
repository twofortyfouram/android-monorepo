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

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.Clock;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Interfaces with plug-in settings, providing facilities to fire them.
 */
/*
 * This class may be safely moved between threads without concern for safe publication.  The only
 * thread-safety concern is if destroy is called while waiting for fire() to complete.
 */
@NotThreadSafe
public final class Setting {

    /**
     * Timeout in milliseconds when waiting for an ordered broadcast.
     */
    private static final int BROADCAST_TIMEOUT_MILLIS = 11 * (int) DateUtils.SECOND_IN_MILLIS;

    @NonNull
    private final Context mContext;

    @NonNull
    private final Clock mClock;

    @NonNull
    private final IPlugin mPlugin;

    @NonNull
    private final HandlerThread mHandlerThread = HandlerThreadFactory.newHandlerThread(
            Setting.class.getName(),
            ThreadPriority.BACKGROUND);

    /**
     * {@code Handler} for processing the ordered broadcasts.
     */
    @NonNull
    private final Handler mHandler = new Handler(mHandlerThread.getLooper());

    /**
     * Constructs a new plug-in setting.
     *
     * @param context Application context.
     * @param clock   Generic clock interface.
     * @param plugin  The plug-in details.
     */
    public Setting(@NonNull final Context context, @NonNull final Clock clock,
            @NonNull final IPlugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(clock, "clock"); //$NON-NLS-1$
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$

        if (PluginType.SETTING != plugin.getType()) {
            throw new IllegalArgumentException("plugin.getType() must be SETTING"); //$NON-NLS-1$
        }

        mContext = ContextUtil.cleanContext(context);
        mClock = clock;
        mPlugin = plugin;
    }

    /**
     * Performs a blocking fire of the plug-in's setting action.
     *
     * @param data The plug-in's instance data previously saved by the Edit Activity.
     */
    @Slow(Speed.SECONDS)
    public void fire(@NonNull final PluginInstanceData data) {
        assertNotNull(data, "data"); //$NON-NLS-1$

        final Bundle pluginBundle = data.getBundle();

        fire(pluginBundle);
    }

    /**
     * Performs a blocking fire of the plug-in's setting action.
     *
     * @param pluginBundle The plug-in's instance data previously saved by the Edit Activity,
     *                     already deserialized back into a Bundle.
     */
    @Slow(Speed.SECONDS)
    public void fire(@NonNull final Bundle pluginBundle) {
        assertNotNull(pluginBundle, "pluginBundle"); //$NON-NLS-1$
        /*
         * Keep this log statement here for the benefit of 3rd party developers
         */
        Lumberjack.always("Firing plug-in setting %s", mPlugin.getRegistryName()); //$NON-NLS-1$

        final Intent intent = new Intent();
        intent.setAction(LocalePluginIntent.ACTION_FIRE_SETTING);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB_MR1)) {
            addFlagsHoneycombMr1(intent);
        }
        /*
         * Setting class name explicitly ensures the intent goes only to its
         * intended recipient.
         */
        intent.setClassName(mPlugin.getPackageName(), mPlugin.getReceiverClassName());

        intent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, pluginBundle);

        if (mPlugin.getConfiguration().isBackwardsCompatibilityEnabled()) {
            intent.putExtras(pluginBundle);
        }

        /*
         * Use an ordered broadcast to block until firing the plug-in is
         * complete. This allows plug-in hosts to fire plug-ins serially,
         * avoiding launching a large number of plug-in processes at once.
         */
        try {
            final FireResultReceiver resultReceiver = new FireResultReceiver();
            final long startRealtimeMillis = mClock.getRealTimeMillis();
            mContext.sendOrderedBroadcast(intent, null, resultReceiver, mHandler,
                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN, null, null);

            try {
                final boolean isReceived = resultReceiver.mLatch.await(BROADCAST_TIMEOUT_MILLIS,
                        TimeUnit.MILLISECONDS);

                if (!isReceived) {
                    // TODO: In the future, errors should be signaled to the user of the SDK.
                    Lumberjack.e("Failed to receive ordered broadcast"); //$NON-NLS-1$
                } else {
                    Lumberjack.v("Fire completed after %d [milliseconds]",
                            mClock.getRealTimeMillis() - startRealtimeMillis);
                }
            } catch (final InterruptedException e) {
                Lumberjack.e("Error waiting on plug-in%s", e); //$NON-NLS-1$
            }
            mContext.sendBroadcast(intent);
        } catch (final Exception e) {
            /*
             * Catch every exception, as there are some weird corner-cases where
             * plug-ins can fail to launch due to TOCTTOU errors. For example, a
             * Permission could be enforced on the BroadcastReceiver at runtime.
             */
            Lumberjack.always("Could not fire plug-in setting%s", e); //$NON-NLS-1$
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private static void addFlagsHoneycombMr1(@NonNull final Intent intent) {
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    }

    /**
     * Destroys the plug-in setting, freeing up resources that were used to fire
     * the plug-in.
     */
    public void destroy() {
        mHandlerThread.getLooper().quit();
    }

    @ThreadSafe
    private static final class FireResultReceiver extends BroadcastReceiver {

        @NonNull
        /* package */ final CountDownLatch mLatch = new CountDownLatch(1);

        @Override
        public void onReceive(final Context context, final Intent intent) {
            try {
                if (BundleScrubber.scrub(intent)) {
                    // TODO: In the future, errors should be signaled to the user of the SDK.
                    return;
                }

                Lumberjack.v("Received %s", intent); //$NON-NLS-1$
            } finally {
                mLatch.countDown();
            }
        }
    }
}
