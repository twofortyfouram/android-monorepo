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


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.FlakyTest;
import androidx.test.filters.SmallTest;
import androidx.test.filters.Suppress;
import androidx.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.twofortyfouram.locale.api.LocalePluginIntent;
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
import java.util.concurrent.atomic.AtomicReference;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public final class AbstractPluginSettingReceiverTest {

    @NonNull
    private static final String ALTERNATIVE_ACTION_FOR_TEST
            = "com.twofortyfouram.locale.sdk.client.test.action.TEST_ACTION"; //$NON-NLS-1$

    @SmallTest
    @Test
    public void no_bundle() {
        final Intent intent = getDefaultIntent();
        intent.removeExtra(LocalePluginIntent.EXTRA_BUNDLE);

        final PluginSettingReceiverImpl receiver = new PluginSettingReceiverImpl(false);
        receiver.onReceive(getContext(), intent);

        assertThat(receiver.getFireJson().get(), nullValue());
        assertThat(receiver.getFireCount().get(), is(0));
    }

    @SmallTest
    @Test
    public void wrong_action() {
        final Intent intent = getDefaultIntent();
        intent.setAction(ALTERNATIVE_ACTION_FOR_TEST);

        final PluginSettingReceiverImpl receiver = new PluginSettingReceiverImpl(false);
        receiver.onReceive(getContext(), intent);

        assertThat(receiver.getFireJson().get(), nullValue());
        assertThat(receiver.getFireCount().get(), is(0));
    }

    @SmallTest
    @Test
    public void not_explicit() {
        final Intent intent = getDefaultIntent();
        intent.setPackage(null);

        final PluginSettingReceiverImpl receiver = new PluginSettingReceiverImpl(false);
        receiver.onReceive(getContext(), intent);

        assertThat(receiver.getFireJson().get(), nullValue());
        assertThat(receiver.getFireCount().get(), is(0));
    }

    @SmallTest
    @Test
    public void null_json() {
        final Intent intent = getDefaultIntent();
        intent.getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE).clear();

        final PluginSettingReceiverImpl receiver = new PluginSettingReceiverImpl(false);
        receiver.onReceive(getContext(), intent);

        assertThat(receiver.getFireJson().get(), nullValue());
        assertThat(receiver.getFireCount().get(), is(0));
    }

    @SmallTest
    @Test
    public void valid() {
        final Intent intent = getDefaultIntent();

        final PluginSettingReceiverImpl receiver = new PluginSettingReceiverImpl(false);
        receiver.onReceive(getContext(), intent);

        assertThat(receiver.getFireJson().get(), notNullValue());
        assertThat(receiver.getFireCount().get(), is(1));
    }

    @SmallTest
    @Test
    @Suppress
    @FlakyTest
    public void valid_async() {
        final Intent intent = getDefaultIntent();

        assertOrderedBroadcast(intent, Activity.RESULT_CANCELED, Activity.RESULT_OK, true, 1,
                intent.getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE));
    }

    @NonNull
    private Intent getDefaultIntent() {
        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, new JSONObject().toString());

        final Intent intent = new Intent();
        intent.setPackage(getContext().getPackageName());
        intent.setAction(LocalePluginIntent.ACTION_FIRE_SETTING);
        intent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);

        return intent;
    }

    /**
     * @param intent             Intent to send to the receiver implementation under test.
     * @param initialResultCode  Normally this should be
     *                           com.twofortyfouram.locale.api.Intent#RESULT_CONDITION_UNKNOWN, but
     *                           can be overridden to verify the receiver under test.
     * @param expectedResultCode The expected result code.
     * @param isAsync            True if the plug-in receiver should handle its work in a
     *                           background thread.
     */
    private void assertOrderedBroadcast(final Intent intent, final int initialResultCode,
                                        final int expectedResultCode, final boolean isAsync, final int expectedFireCount,
                                        final Bundle expectedBundle) {

        final HandlerThread handlerThread = HandlerThreadFactory
                .newHandlerThread(UUID.randomUUID().toString(),
                        ThreadPriority.DEFAULT);

        final PluginSettingReceiverImpl receiverImpl = new PluginSettingReceiverImpl(isAsync);

        final IntentFilter filter = new IntentFilter(
                LocalePluginIntent.ACTION_FIRE_SETTING);
        filter.addAction(ALTERNATIVE_ACTION_FOR_TEST);

        InstrumentationRegistry.getContext().registerReceiver(receiverImpl, filter);
        try {

            final QueryResultReceiver resultReceiver = new QueryResultReceiver();
            InstrumentationRegistry.getContext().sendOrderedBroadcast(intent, null, resultReceiver,
                    new Handler(handlerThread.getLooper()), initialResultCode, null, null);

            try {
                assertThat(resultReceiver.getLatch().await(5 * DateUtils.SECOND_IN_MILLIS,
                        TimeUnit.MILLISECONDS), is(true));
            } catch (final InterruptedException e) {
                throw new AssertionError(e);
            }

            // TODO: Compare JSON
            //assertThat(receiverImpl.getFireJson().toString(), is(expectedBundle))
            // assertTrue(BundleComparer.areBundlesEqual(expectedBundle,
            // receiverImpl.mFireBundle.get()));
            assertThat(receiverImpl.getFireCount().get(), is(expectedFireCount));
        } finally {
            handlerThread.getLooper().quit();
            InstrumentationRegistry.getContext().unregisterReceiver(receiverImpl);
        }

    }

    private static final class PluginSettingReceiverImpl extends AbstractPluginSettingReceiver {

        private final boolean mIsAsync;

        @NonNull
        private final AtomicInteger mIsValidCount = new AtomicInteger(0);

        @NonNull
        private final AtomicInteger mFireCount = new AtomicInteger(0);

        @NonNull
        private final AtomicReference<JSONObject> mFireJson = new AtomicReference<>(null);

        @NonNull
        public AtomicInteger getFireCount() {
            return mFireCount;
        }

        @NonNull
        public AtomicReference<JSONObject> getFireJson() {
            return mFireJson;
        }

        public PluginSettingReceiverImpl(final boolean isAsync) {
            mIsAsync = isAsync;
        }


        @Override
        protected boolean isJsonValid(@NonNull final JSONObject jsonObject) {
            mIsValidCount.incrementAndGet();
            return true;
        }

        @Override
        protected boolean isAsync() {
            return mIsAsync;
        }

        @Override
        protected void firePluginSetting(@NonNull final Context context,
                                         @NonNull final JSONObject json) {
            mFireCount.incrementAndGet();
            mFireJson.set(json);
        }
    }

    @ThreadSafe
    private static final class QueryResultReceiver extends BroadcastReceiver {

        @NonNull
        private final CountDownLatch mLatch = new CountDownLatch(1);


        @NonNull
        public CountDownLatch getLatch() {
            return mLatch;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (BundleScrubber.scrub(intent)) {
                throw new AssertionError();
            }

            Lumberjack.v("Received %s", intent); //$NON-NLS-1$

            mLatch.countDown();
        }
    }
}
