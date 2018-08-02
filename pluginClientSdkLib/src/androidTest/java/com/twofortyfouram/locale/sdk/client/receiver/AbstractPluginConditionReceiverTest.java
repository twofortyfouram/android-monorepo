/*
 * android-plugin-client-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.client.receiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.filters.Suppress;
import androidx.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.client.test.condition.receiver.PluginConditionReceiver;
import com.twofortyfouram.locale.sdk.client.test.condition.receiver.PluginJsonValues;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@Suppress
public final class AbstractPluginConditionReceiverTest {
    /*
     * In order to test the abstract class, these tests rely on the concrete implementation of
     * PluginConditionReceiver.
     */

    @NonNull
    private static final String ALTERNATIVE_ACTION_FOR_TEST
            = "com.twofortyfouram.locale.sdk.client.test.action.TEST_ACTION"; //$NON-NLS-1$

    @SmallTest
    @Test
    public void receiver_no_ordered_broadcast() {
        final Intent intent = getDefaultIntent(
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);

        final PluginConditionReceiver receiver = new PluginConditionReceiver();

        /*
         * This verifies that no exception is thrown for a non-ordered
         * broadcast. Yes, this is different from all the other tests because it
         * is not ordered.
         */
        receiver.onReceive(getContext(), intent);
    }

    @SmallTest
    @Test
    public void no_bundle() {
        final Intent intent = getDefaultIntent(LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
        intent.removeExtra(LocalePluginIntent.EXTRA_BUNDLE);

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
    }

    @SmallTest
    @Test
    public void no_json() {
        final Intent intent = getDefaultIntent(LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
        intent.getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE).clear();

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED);
        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
    }

    @SmallTest
    @Test
    public void receiver_wrong_action() {
        final Intent intent = getDefaultIntent(
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
        intent.setAction(ALTERNATIVE_ACTION_FOR_TEST);

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
    }

    @SmallTest
    @Test
    public void result_satisfied() {
        final Intent intent = getDefaultIntent(
                LocalePluginIntent.RESULT_CONDITION_SATISFIED);

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED);
    }

    @SmallTest
    @Test
    public void result_unsatisfied() {
        final Intent intent = getDefaultIntent(
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN,
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
    }

    @SmallTest
    @Test
    public void result_unknown() {
        final Intent intent = getDefaultIntent(
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);

        assertOrderedBroadcastResultCode(intent,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertState() {

        AbstractPluginConditionReceiver
                .assertResult(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
        AbstractPluginConditionReceiver
                .assertResult(LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
        AbstractPluginConditionReceiver
                .assertResult(LocalePluginIntent.RESULT_CONDITION_SATISFIED);

        // throws
        AbstractPluginConditionReceiver.assertResult(Activity.RESULT_OK);
    }

    @NonNull
    @SuppressLint("InlinedApi")
    private Intent getDefaultIntent(final int state) {
        final Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES | Intent.FLAG_FROM_BACKGROUND);
        intent.setClassName(getContext(), PluginConditionReceiver.class.getName());
        intent.setAction(LocalePluginIntent.ACTION_QUERY_CONDITION);

        final JSONObject json = PluginJsonValues.generateJson(getContext(), state);
        final String jsonString = json.toString();

        final Bundle bundle = new Bundle();

        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, jsonString);
        intent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);

        return intent;
    }

    /**
     * @param intent             Intent to send to the receiver implementation under test.
     * @param initialResultCode  Normally this should be
     *                           com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN, but
     *                           can be overridden to verify the receiver under test.
     * @param expectedResultCode Result code to assert comes back.
     */
    private void assertOrderedBroadcastResultCode(final Intent intent,
                                                  final int initialResultCode, final int expectedResultCode) {

        final HandlerThread handlerThread = HandlerThreadFactory
                .newHandlerThread(UUID.randomUUID().toString(),
                        ThreadPriority.DEFAULT);

        try {
            final QueryResultReceiver resultReceiver = new QueryResultReceiver();
            InstrumentationRegistry.getContext().sendOrderedBroadcast(intent, null, resultReceiver,
                    new Handler(handlerThread.getLooper()), initialResultCode, null, null);

            try {
                assertTrue(resultReceiver.mLatch.await(6 * DateUtils.SECOND_IN_MILLIS,
                        TimeUnit.MILLISECONDS));
            } catch (final InterruptedException e) {
                throw new AssertionError(e);
            }

            assertEquals(expectedResultCode + "!=" + resultReceiver.mQueryResult.get(),
                    expectedResultCode, resultReceiver.mQueryResult.get());
        } finally {
            handlerThread.getLooper().quit();
        }

    }

    @ThreadSafe
    private static final class QueryResultReceiver extends BroadcastReceiver {

        @NonNull
        /* package */ final CountDownLatch mLatch = new CountDownLatch(1);

        @NonNull
        /* package */ final AtomicInteger mQueryResult = new AtomicInteger(
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (BundleScrubber.scrub(intent)) {
                throw new AssertionError();
            }

            Lumberjack.v("Received %s", intent); //$NON-NLS-1$

            switch (getResultCode()) {
                case LocalePluginIntent.RESULT_CONDITION_SATISFIED: {
                    Lumberjack.v("Got RESULT_CONDITION_SATISFIED"); //$NON-NLS-1$
                    mQueryResult
                            .set(LocalePluginIntent.RESULT_CONDITION_SATISFIED);
                    break;
                }
                case LocalePluginIntent.RESULT_CONDITION_UNSATISFIED: {
                    Lumberjack.v("Got RESULT_CONDITION_UNSATISFIED"); //$NON-NLS-1$
                    mQueryResult
                            .set(LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
                    break;
                }
                case LocalePluginIntent.RESULT_CONDITION_UNKNOWN: {
                    Lumberjack.v("Got RESULT_CONDITION_UNKNOWN"); //$NON-NLS-1$
                    mQueryResult.set(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
                    break;
                }
                default: {
                    /*
                     * Although this shouldn't happen, don't throw an exception
                     * because bad 3rd party apps could give bad result codes
                     */
                    Lumberjack.w("Got unrecognized result code: %d", getResultCode()); //$NON-NLS-1$
                    mQueryResult.set(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
                }
            }

            mLatch.countDown();
        }
    }
}
