/*
 * android-test
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

package com.twofortyfouram.test.context;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public final class ReceiverContextWrapperTest {

    // TODO: These tests are not particularly DRY

    @SmallTest
    @Test
    public void getApplicationContext_does_not_break_out() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        assertThat(fContext.getApplicationContext(),
                sameInstance(fContext));
    }

    @SmallTest
    @Test
    public void getAndClear_empty() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        final Collection<ReceiverContextWrapper.SentIntent> intents = fContext
                .getAndClearSentIntents();
        assertThat(intents, notNullValue());
        assertThat(intents, empty());
    }

    @MediumTest
    @Test
    public void sendBroadcast_without_permission() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        /*
         * Verifies the Intent is not broadcast to the rest of the system and the Intent is captured.
         */
        final String name = UUID.randomUUID().toString();
        final HandlerThread handlerThread = new HandlerThread(name,
                android.os.Process.THREAD_PRIORITY_DEFAULT);
        handlerThread.start();
        try {

            final CountDownLatch latch = new CountDownLatch(1);
            final BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    latch.countDown();
                }
            };

            final String intentAction = "com.twofortyfouram.test.intent.action." + name;
            final IntentFilter filter = new IntentFilter(intentAction);
            InstrumentationRegistry.getContext().registerReceiver(receiver, filter);

            final Intent intentToSend = new Intent(intentAction);
            fContext.sendBroadcast(intentToSend);

            try {
                assertThat(latch.await(DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS),
                        is(false));
            } catch (final InterruptedException e) {
                fail(e.getMessage());
            } finally {
                InstrumentationRegistry.getContext().unregisterReceiver(receiver);
            }

            final Collection<ReceiverContextWrapper.SentIntent> intents = fContext
                    .getAndClearSentIntents();
            assertThat(intents.size(), is(1));
            for (final ReceiverContextWrapper.SentIntent i : intents) {
                assertThat(intentToSend.filterEquals(i.getIntent()), is(true));
                assertThat(i.getIntent(), not(sameInstance(intentToSend)));

                assertThat(i.getPermission(), nullValue());
                assertThat(i.getIsSticky(), is(false));
                assertThat(i.getIsOrdered(), is(false));
            }

        } finally {
            handlerThread.getLooper().quit();
        }
    }

    @MediumTest
    @Test
    public void sendBroadcast_with_permission() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        /*
         * Verifies the Intent is not broadcast to the rest of the system and the Intent is captured.
         */
        final String name = UUID.randomUUID().toString();
        final HandlerThread handlerThread = new HandlerThread(name,
                android.os.Process.THREAD_PRIORITY_DEFAULT);
        handlerThread.start();
        try {

            final CountDownLatch latch = new CountDownLatch(1);
            final BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    latch.countDown();
                }
            };

            final String intentAction = "com.twofortyfouram.test.intent.action." + name;
            final String permissionString = "com.twofortyfouram.test.permission." + name;
            final IntentFilter filter = new IntentFilter(intentAction);
            InstrumentationRegistry.getContext()
                    .registerReceiver(receiver, filter, permissionString, null);

            final Intent intentToSend = new Intent(intentAction);
            fContext.sendBroadcast(intentToSend, permissionString);

            try {
                assertThat(latch.await(DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS),
                        is(false));
            } catch (final InterruptedException e) {
                fail(e.getMessage());
            } finally {
                InstrumentationRegistry.getContext().unregisterReceiver(receiver);
            }

            final Collection<ReceiverContextWrapper.SentIntent> intents = fContext
                    .getAndClearSentIntents();
            assertThat(intents.size(), is(1));
            for (final ReceiverContextWrapper.SentIntent i : intents) {
                assertThat(intentToSend.filterEquals(i.getIntent()), is(true));
                assertThat(i.getIntent(), not(sameInstance(intentToSend)));

                assertThat(i.getPermission(), is(permissionString));
                assertThat(i.getIsSticky(), is(false));
                assertThat(i.getIsOrdered(), is(false));
            }

        } finally {
            handlerThread.getLooper().quit();
        }
    }

    @MediumTest
    @Test
    public void sendOrderedBroadcast_without_result_receiver() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        /*
         * Verifies the Intent is not broadcast to the rest of the system and the Intent is captured.
         */
        final String name = UUID.randomUUID().toString();
        final HandlerThread handlerThread = new HandlerThread(name,
                android.os.Process.THREAD_PRIORITY_DEFAULT);
        handlerThread.start();
        try {

            final CountDownLatch latch = new CountDownLatch(1);
            final BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    latch.countDown();
                }
            };

            final String intentAction = "com.twofortyfouram.test.intent.action." + name;
            final IntentFilter filter = new IntentFilter(intentAction);
            InstrumentationRegistry.getContext().registerReceiver(receiver, filter);

            final Intent intentToSend = new Intent(intentAction);
            fContext.sendOrderedBroadcast(intentToSend, null);

            try {
                assertThat(latch.await(DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS),
                        is(false));
            } catch (final InterruptedException e) {
                fail(e.getMessage());
            } finally {
                InstrumentationRegistry.getContext().unregisterReceiver(receiver);
            }

            final Collection<ReceiverContextWrapper.SentIntent> intents = fContext
                    .getAndClearSentIntents();
            assertThat(intents.size(), is(1));
            for (final ReceiverContextWrapper.SentIntent i : intents) {
                assertThat(intentToSend.filterEquals(i.getIntent()), is(true));
                assertThat(i.getIntent(), not(sameInstance(intentToSend)));

                assertThat(i.getPermission(), nullValue());
                assertThat(i.getIsSticky(), is(false));
                assertThat(i.getIsOrdered(), is(true));
            }

        } finally {
            handlerThread.getLooper().quit();
        }
    }

    @MediumTest
    @Test
    public void sendOrderedBroadcast_with_result_receiver() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        /*
         * Verifies the Intent is not broadcast to the rest of the system and the Intent is captured.
         */
        final String name = UUID.randomUUID().toString();
        final HandlerThread handlerThread = new HandlerThread(name,
                android.os.Process.THREAD_PRIORITY_DEFAULT);
        handlerThread.start();
        try {

            final CountDownLatch systemLatch = new CountDownLatch(1);
            final BroadcastReceiver systemReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    systemLatch.countDown();
                }
            };

            final CountDownLatch fakeLatch = new CountDownLatch(1);
            final BroadcastReceiver fakeReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    assertThat(Looper.myLooper(), sameInstance(handlerThread.getLooper()));
                    fakeLatch.countDown();
                }
            };

            final String intentAction = "com.twofortyfouram.test.intent.action." + name;
            final IntentFilter filter = new IntentFilter(intentAction);
            InstrumentationRegistry.getContext().registerReceiver(systemReceiver, filter);

            final Intent intentToSend = new Intent(intentAction);
            fContext.sendOrderedBroadcast(intentToSend, null, fakeReceiver,
                    new Handler(handlerThread.getLooper()), 0, null, null);

            // Not sent to the entire system
            try {
                assertThat(systemLatch.await(DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS),
                        is(false));
            } catch (final InterruptedException e) {
                fail(e.getMessage());
            } finally {
                InstrumentationRegistry.getContext().unregisterReceiver(systemReceiver);
            }

            // Sent to the fake systemReceiver
            try {
                assertThat(fakeLatch.await(DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS),
                        is(true));
            } catch (final InterruptedException e) {
                fail(e.getMessage());
            }

            final Collection<ReceiverContextWrapper.SentIntent> intents = fContext
                    .getAndClearSentIntents();
            assertThat(intents.size(), is(1));
            for (final ReceiverContextWrapper.SentIntent i : intents) {
                assertThat(intentToSend.filterEquals(i.getIntent()), is(true));
                assertThat(i.getIntent(), not(sameInstance(intentToSend)));

                assertThat(i.getPermission(), nullValue());
                assertThat(i.getIsSticky(), is(false));
                assertThat(i.getIsOrdered(), is(true));
            }

        } finally {
            handlerThread.getLooper().quit();
        }
    }

    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    public void sendBroadcastAsUser_without_permission() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());
        fContext.sendBroadcastAsUser(new Intent(), null);
    }

    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    public void sendBroadcastAsUser_with_permission() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());
        fContext.sendBroadcastAsUser(new Intent(), null, null);
    }


    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    public void sendStickyBroadcastAsUser_throw() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());
        fContext.sendStickyBroadcastAsUser(new Intent(), null);
    }

    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test(expected = UnsupportedOperationException.class)
    public void sendStickyOrderedBroadcastAsUser_throw() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        fContext.sendStickyOrderedBroadcastAsUser(new Intent(), null, null, null, 0, null,
                null);
    }

    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void sendOrderedBroadcastAsUser_throw() {
        final ReceiverContextWrapper fContext = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());
        fContext.sendOrderedBroadcastAsUser(new Intent(), null, null, null, null, 0, null,
                null);
    }

    @SmallTest
    @Test
    public void startService() {
        final ReceiverContextWrapper context = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        context.startService(new Intent("foo")); //$NON-NLS

        final ReceiverContextWrapper.SentIntent polledIntent = context.pollIntent();
        assertThat(polledIntent, notNullValue());

        assertThat(polledIntent.getIntent().getAction(), is("foo")); //$NON-NLS

        assertThat(context.pollIntent(), nullValue());
    }

}
