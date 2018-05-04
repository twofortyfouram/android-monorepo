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

package com.twofortyfouram.locale.sdk.host.api;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.format.DateUtils;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import com.twofortyfouram.locale.api.v1.annotation.ConditionResultIntDef;
import com.twofortyfouram.locale.api.v2.PluginConditionContract;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
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
    private final Clock mClock;

    @NonNull
    private final Plugin mPlugin;

    @NonNull
    private final HandlerThread mHandlerThread = HandlerThreadFactory.newHandlerThread(
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
     * @param clock   Generic clock interface.
     * @param plugin  The plug-in details.
     */
    public Condition(@NonNull final Context context, @NonNull final Clock clock,
                     @NonNull final Plugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(clock, "clock"); //$NON-NLS-1$
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$

        if (PluginType.CONDITION != plugin.getType()) {
            throw new IllegalArgumentException("plugin.getType() must be CONDITION"); //$NON-NLS-1$
        }

        mContext = ContextUtil.cleanContext(context);
        mClock = clock;
        mPlugin = plugin;
    }

    /**
     * Performs a blocking query to the plug-in condition.
     *
     * @param data          The plug-in's instance data previously saved by the Edit Activity.
     * @param previousState The previous query result of the plug-in, to be set as the initial
     *                      result code
     *                      when querying the plug-in.  This must be one of {@link
     *                      LocalePluginIntentV1#RESULT_CONDITION_SATISFIED
     *                      RESULT_CONDITION_SATISFIED}
     *                      ,
     *                      {@link LocalePluginIntentV1#RESULT_CONDITION_UNSATISFIED
     *                      RESULT_CONDITION_UNSATISFIED}
     *                      , or
     *                      {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     *                      Plug-in implementations might use this
     *                      previous result code for hysteresis.  If no previous state is
     *                      available,
     *                      pass {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     * @return One of the Locale plug-in query results:
     * {@link LocalePluginIntentV1#RESULT_CONDITION_SATISFIED
     * RESULT_CONDITION_SATISFIED}
     * ,
     * {@link LocalePluginIntentV1#RESULT_CONDITION_UNSATISFIED
     * RESULT_CONDITION_UNSATISFIED}
     * , or
     * {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     * .
     */
    @Slow(Speed.SECONDS)
    @ConditionResultIntDef
    public int query(@NonNull final PluginInstanceData data, @ConditionResultIntDef final int
            previousState) {
        assertNotNull(data, "data"); //$NON-NLS-1$
        assertInRangeInclusive(previousState, LocalePluginIntentV1.
                RESULT_CONDITION_SATISFIED, LocalePluginIntentV1.
                RESULT_CONDITION_UNKNOWN, "previousState");

        final Bundle pluginBundle = data.getBundle();


        return query(pluginBundle, previousState);
    }

    /**
     * Performs a blocking query to the plug-in condition.
     * <p>
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
     *                      LocalePluginIntentV1#RESULT_CONDITION_SATISFIED
     *                      RESULT_CONDITION_SATISFIED}
     *                      ,
     *                      {@link LocalePluginIntentV1#RESULT_CONDITION_UNSATISFIED
     *                      RESULT_CONDITION_UNSATISFIED}
     *                      , or
     *                      {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     *                      Plug-in implementations might use this
     *                      previous result code for hysteresis.  If no previous state is
     *                      available,
     *                      pass {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     *                      RESULT_CONDITION_UNKNOWN}.
     * @return One of the Locale plug-in query results:
     * {@link LocalePluginIntentV1#RESULT_CONDITION_SATISFIED
     * RESULT_CONDITION_SATISFIED}
     * ,
     * {@link LocalePluginIntentV1#RESULT_CONDITION_UNSATISFIED
     * RESULT_CONDITION_UNSATISFIED}
     * , or
     * {@link LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     * .
     */
    @Slow(Speed.SECONDS)
    @ConditionResultIntDef
    public int query(@NonNull final Bundle pluginBundle, @ConditionResultIntDef final int
            previousState) {
        switch (mPlugin.getComponentType()) {
            case CONTENT_PROVIDER: {
                // API 2.0
                return queryProvider(pluginBundle, previousState);
            }
            case BROADCAST_RECEIVER: {
                // API 1.0
                return queryReceiver(pluginBundle, previousState);
            }
            case NONE:{
                throw new UnsupportedOperationException();
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Slow(Speed.SECONDS)
    private int queryProvider(@NonNull final Bundle pluginBundle, @ConditionResultIntDef final int previousState) {
        assertNotNull(pluginBundle, "pluginBundle"); //$NON-NLS-1$

        /*
         * Keep this log statement here for the benefit of 3rd party developers
         */
        Lumberjack.always("Querying plug-in condition %s", mPlugin.getRegistryName()); //$NON-NLS-1$

        // TODO: figure out a way to avoid building this each time
        @NonNull final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(mPlugin.getComponentIdentifier()).build();

        /*
         * There is no system imposed limit on the duration of a ContentProvider call.  A malicious plug-in could
         * theoretically keep the host waiting forever.  This should
         */
        // FIXME: Wrap this in a thread that we can kill, so that an infinitely long running
        // provider call won't hose us.
        final long startRealtimeMillis = mClock.getRealTimeMillis();
        try {
            @NonNull final Bundle result = mContext.getContentResolver().call(uri, PluginConditionContract.METHOD_QUERY_CONDITION, null,
                    pluginBundle);

            Lumberjack.v("Query completed after %d [milliseconds]",
                    mClock.getRealTimeMillis() - startRealtimeMillis);

            if (BundleScrubber.scrub(result)) {
                Lumberjack.always("Plug-in returned a Bundle containing a non-system Parcelable object"); //$NON-NLS
                return PluginConditionContract.RESULT_CONDITION_UNKNOWN;
            }

            Lumberjack.v("result=%s", result); //$NON-NLS

            return pluginBundle.getInt(PluginConditionContract.EXTRA_INT_RESULT, PluginConditionContract.RESULT_CONDITION_UNKNOWN);
        } catch (final Exception e) {
            // Anything can happen so catchall is necessary.
        }

        return PluginConditionContract.RESULT_CONDITION_UNKNOWN;
    }

    @Slow(Speed.SECONDS)
    private int queryReceiver(@NonNull final Bundle pluginBundle, @ConditionResultIntDef final int previousState) {
        assertNotNull(pluginBundle, "pluginBundle"); //$NON-NLS-1$
        assertInRangeInclusive(previousState, LocalePluginIntentV1.
                RESULT_CONDITION_SATISFIED, LocalePluginIntentV1.
                RESULT_CONDITION_UNKNOWN, "previousState");

        /*
         * Keep this log statement here for the benefit of 3rd party developers.
         */
        Lumberjack.always("Querying plug-in condition %s", mPlugin.getRegistryName()); //$NON-NLS-1$

        final Intent intent = newQueryIntent(mPlugin, pluginBundle);

        final QueryResultReceiver resultReceiver = new QueryResultReceiver();
        final long startRealtimeMillis = mClock.getRealTimeMillis();
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
                        mClock.getRealTimeMillis() - startRealtimeMillis); //$NON-NLS-1$
            }
        } catch (final InterruptedException e) {
            Lumberjack.e("Error waiting on plug-in%s", e); //$NON-NLS-1$
        }

        @ConditionResultIntDef final int conditionResult = resultReceiver.mQueryResult.get();

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
    @VisibleForTesting
    /*package*/ static Intent newQueryIntent(@NonNull final Plugin plugin,
                                             @NonNull final Bundle extraBundle) {
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$
        assertNotNull(extraBundle, "extraBundle"); //$NON-NLS-1$

        final Intent intent = new Intent();
        intent.setAction(LocalePluginIntentV1.ACTION_QUERY_CONDITION);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB_MR1)) {
            addFlagsHoneycombMr1(intent);
        }
        /*
         * Setting class name explicitly ensures the Intent goes only to its
         * intended recipient.
         */
        intent.setClassName(plugin.getPackageName(), plugin.getComponentIdentifier());
        intent.putExtra(LocalePluginIntentV1.EXTRA_BUNDLE, extraBundle);

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
                LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN);

        @Override
        public void onReceive(final Context context, final Intent intent) {
            try {
                if (BundleScrubber.scrub(intent)) {
                    // TODO: In the future, errors should be signaled to the user of the SDK.
                    mQueryResult
                            .set(LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN);
                    return;
                }

                Lumberjack.v("Received %s", intent); //$NON-NLS-1$

                switch (getResultCode()) {
                    case LocalePluginIntentV1.RESULT_CONDITION_SATISFIED: {
                        Lumberjack.always("Got RESULT_CONDITION_SATISFIED"); //$NON-NLS-1$
                        mQueryResult
                                .set(LocalePluginIntentV1.RESULT_CONDITION_SATISFIED);
                        break;
                    }
                    case LocalePluginIntentV1.RESULT_CONDITION_UNSATISFIED: {
                        Lumberjack.always("Got RESULT_CONDITION_UNSATISFIED"); //$NON-NLS-1$
                        mQueryResult
                                .set(LocalePluginIntentV1.RESULT_CONDITION_UNSATISFIED);
                        break;
                    }
                    case LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN: {
                        Lumberjack.always("Got RESULT_CONDITION_UNKNOWN"); //$NON-NLS-1$
                        mQueryResult
                                .set(LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN);
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
                                .set(LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN);
                    }
                }
            } finally {
                mLatch.countDown();
            }
        }
    }
}
