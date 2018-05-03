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
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.format.DateUtils;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.ThreadUtil;
import com.twofortyfouram.spackle.ThreadUtil.ThreadPriority;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Interfaces with plug-in conditions, providing facilities to query them.
 */
/*
 * This class may be safely moved between threads without concern for safe publication.  The only
 * thread-safety concern is if destroy is called while waiting for a query to complete.
 */
@NotThreadSafe
public final class Condition {

    /**
     * Timeout in milliseconds when waiting for an ordered broadcast.
     */
    private static final int BROADCAST_TIMEOUT_MILLIS = 11 * (int) DateUtils.SECOND_IN_MILLIS;

    @NonNull
    private final Context mContext;

    @NonNull
    private final Plugin mPlugin;

    @NonNull
    private final HandlerThread mHandlerThread = ThreadUtil.newHandlerThread(
            Condition.class.getName(), ThreadPriority.BACKGROUND);

    /**
     * {@code Handler} for processing the ordered broadcasts.
     */
    @NonNull
    private final Handler mHandler = new Handler(mHandlerThread.getLooper());

    /**
     * Constructs a new plug-in setting.
     *
     * @param context Application context.
     * @param plugin  The plug-in details.
     */
    public Condition(@NonNull final Context context, @NonNull final Plugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$

        if (PluginType.CONDITION != plugin.getType()) {
            throw new IllegalArgumentException("plugin.getType() must be CONDITION"); //$NON-NLS-1$
        }

        mContext = ContextUtil.cleanContext(context);
        mPlugin = plugin;
    }

    /**
     * Performs a blocking query to the plug-in condition.
     *
     * @param data          The plug-in's instance data previously saved by the Edit Activity.
     * @param previousState The previous query result of the plug-in, to be set as the initial
     *                      result code
     *                      when querying the plug-in.  This must be one of {@link
     *                      com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_SATISFIED
     *                      RESULT_CONDITION_SATISFIED}
     *                      ,
     *                      {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNSATISFIED
     *                      RESULT_CONDITION_UNSATISFIED}
     *                      , or
     *                      {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     *                      Plug-in implementations might use this
     *                      previous result code for hysteresis.  If no previous state is
     *                      available,
     *                      pass {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     * @return One of the Locale plug-in query results:
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_SATISFIED
     * RESULT_CONDITION_SATISFIED}
     * ,
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNSATISFIED
     * RESULT_CONDITION_UNSATISFIED}
     * , or
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     * .
     */
    @Slow(Speed.SECONDS)
    @ConditionResult
    public int query(@NonNull final PluginInstanceData data, @ConditionResult final int
            previousState) {
        assertNotNull(data, "data"); //$NON-NLS-1$
        assertInRangeInclusive(previousState, com.twofortyfouram.locale.api.Intent.
                RESULT_CONDITION_SATISFIED, com.twofortyfouram.locale.api.Intent.
                RESULT_CONDITION_UNKNOWN, "previousState");

        final Bundle pluginBundle;
        try {
            /*
             * Referring to the full class name, rather than using an import, works around JavaDoc
             * warnings on BundleSerializer which is obfuscated out by the time the JavaDoc task
             * runs.
             */
            pluginBundle = com.twofortyfouram.locale.sdk.host.internal.BundleSerializer
                    .deserializeFromByteArray(data.getSerializedBundle());
        } catch (final Exception e) {
            /*
             * If the Bundle could be serialized, it should be possible to deserialize.  One
             * scenario where this could fail is if the Bundle was serialized on a newer version of
             * Android and contained a Serializable class not available to the current version of
             * Android.
             */
            Lumberjack.always("Error deserializing bundle", e);
            return com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN;
        }

        return query(pluginBundle, previousState);
    }

    /**
     * Performs a blocking query to the plug-in condition.
     *
     * Note: PluginInstanceData is immutable and provides a safe interface, however guaranteed
     * immutability requires a memory copy and deserialization, which are not efficient.
     * This method provides an alternative for clients to optimize performance by deserializing
     * the plug-in's Bundle once and reusing that Bundle.  When using this approach, clients must
     * not modify the Bundle.
     *
     * @param pluginBundle  The plug-in's instance data previously saved by the Edit Activity.
     * @param previousState The previous query result of the plug-in, to be set as the initial
     *                      result code
     *                      when querying the plug-in.  This must be one of {@link
     *                      com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_SATISFIED
     *                      RESULT_CONDITION_SATISFIED}
     *                      ,
     *                      {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNSATISFIED
     *                      RESULT_CONDITION_UNSATISFIED}
     *                      , or
     *                      {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     *                      Plug-in implementations might use this
     *                      previous result code for hysteresis.  If no previous state is
     *                      available,
     *                      pass {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     * @return One of the Locale plug-in query results:
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_SATISFIED
     * RESULT_CONDITION_SATISFIED}
     * ,
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNSATISFIED
     * RESULT_CONDITION_UNSATISFIED}
     * , or
     * {@link com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     * .
     */
    @Slow(Speed.SECONDS)
    @ConditionResult
    public int query(@NonNull final Bundle pluginBundle, @ConditionResult final int
            previousState) {
        assertNotNull(pluginBundle, "pluginBundle"); //$NON-NLS-1$
        assertInRangeInclusive(previousState, com.twofortyfouram.locale.api.Intent.
                RESULT_CONDITION_SATISFIED, com.twofortyfouram.locale.api.Intent.
                RESULT_CONDITION_UNKNOWN, "previousState");

        /*
         * Keep this log statement here for the benefit of 3rd party developers.
         */
        Lumberjack.always("Querying plug-in condition %s", mPlugin.getRegistryName()); //$NON-NLS-1$

        final Intent intent = newQueryIntent(mPlugin, pluginBundle);

        final QueryResultReceiver resultReceiver = new QueryResultReceiver();
        final long startRealtimeMillis = SystemClock.elapsedRealtime();
        mContext.sendOrderedBroadcast(intent, null, resultReceiver, mHandler,
                previousState, null, null);

        try {
            final boolean isReceived = resultReceiver.mLatch.await(BROADCAST_TIMEOUT_MILLIS,
                    TimeUnit.MILLISECONDS);

            if (!isReceived) {
                // TODO: In the future, errors should be signaled to the user of the SDK.
                Lumberjack.e("Failed to receive ordered broadcast"); //$NON-NLS-1$
            } else {
                Lumberjack.v("Query completed after %d [milliseconds]",
                        SystemClock.elapsedRealtime() - startRealtimeMillis);
            }
        } catch (final InterruptedException e) {
            Lumberjack.e("Error waiting on plug-in%s", e); //$NON-NLS-1$
        }

        @ConditionResult
        final int conditionResult = resultReceiver.mQueryResult.get();

        return conditionResult;
    }

    /**
     * Destroys the plug-in condition, freeing up resources that were used to
     * perform queries to the plug-in.  After being destroyed, {@link #query(PluginInstanceData,
     * int)} }
     * cannot be called.
     */
    public void destroy() {
        mHandlerThread.getLooper().quit();
    }

    @NonNull
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    /*package*/ static Intent newQueryIntent(@NonNull final Plugin plugin,
            @NonNull final Bundle extraBundle) {
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$
        assertNotNull(extraBundle, "extraBundle"); //$NON-NLS-1$

        final Intent intent = new Intent();
        intent.setAction(com.twofortyfouram.locale.api.Intent.ACTION_QUERY_CONDITION);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB_MR1)) {
            addFlagsHoneycombMr1(intent);
        }
        /*
         * Setting class name explicitly ensures the Intent goes only to its
         * intended recipient.
         */
        intent.setClassName(plugin.getPackageName(), plugin.getReceiverClassName());
        intent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, extraBundle);

        return intent;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private static void addFlagsHoneycombMr1(@NonNull final Intent intent) {
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    }

    @ThreadSafe
    private static final class QueryResultReceiver extends BroadcastReceiver {

        @NonNull
        /* package */ final CountDownLatch mLatch = new CountDownLatch(1);

        @NonNull
        /* package */ final AtomicInteger mQueryResult = new AtomicInteger(
                com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN);

        @Override
        public void onReceive(final Context context, final Intent intent) {
            try {
                if (BundleScrubber.scrub(intent)) {
                    // TODO: In the future, errors should be signaled to the user of the SDK.
                    mQueryResult
                            .set(com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN);
                    return;
                }

                Lumberjack.v("Received %s", intent); //$NON-NLS-1$

                switch (getResultCode()) {
                    case com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED: {
                        Lumberjack.always("Got RESULT_CONDITION_SATISFIED"); //$NON-NLS-1$
                        mQueryResult
                                .set(com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED);
                        break;
                    }
                    case com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNSATISFIED: {
                        Lumberjack.always("Got RESULT_CONDITION_UNSATISFIED"); //$NON-NLS-1$
                        mQueryResult
                                .set(com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNSATISFIED);
                        break;
                    }
                    case com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN: {
                        Lumberjack.always("Got RESULT_CONDITION_UNKNOWN"); //$NON-NLS-1$
                        mQueryResult
                                .set(com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN);
                        break;
                    }
                    default: {
                        /*
                         * Although this shouldn't happen, don't throw an exception
                         * because bad 3rd party apps could give bad result codes
                         */
                        // TODO: In the future, errors should be signaled to the user of the SDK.
                        Lumberjack.w("Got unrecognized result code: %d",
                                getResultCode()); //$NON-NLS-1$
                        mQueryResult
                                .set(com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_UNKNOWN);
                    }
                }
            } finally {
                mLatch.countDown();
            }
        }
    }

    @IntDef({com.twofortyfouram.locale.api.Intent.RESULT_CONDITION_SATISFIED, com.twofortyfouram
            .locale.api.Intent.RESULT_CONDITION_UNKNOWN, com.twofortyfouram.locale.api.Intent
            .RESULT_CONDITION_UNSATISFIED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConditionResult {

    }
}
