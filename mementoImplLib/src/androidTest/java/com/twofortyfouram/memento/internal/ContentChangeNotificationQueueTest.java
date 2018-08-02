/*
 * android-memento
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

package com.twofortyfouram.memento.internal;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.twofortyfouram.memento.test.main_process.TableOneContract;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;
import com.twofortyfouram.test.context.ReceiverContextWrapper;
import com.twofortyfouram.test.context.ReceiverContextWrapper.SentIntent;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class ContentChangeNotificationQueueTest {

    @SmallTest
    @Test
    public void non_batch_by_default() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        assertFalse(queue.isBatch());
    }

    @SmallTest
    @Test
    public void isBatch_true_after_start() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.startBatch();
        assertTrue(queue.isBatch());
    }

    @SmallTest
    @Test
    public void endBatch_notify() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.startBatch();
        queue.endBatch(true);
        assertFalse(queue.isBatch());
    }

    @SmallTest
    @Test
    public void endBatch_dont_notify() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.startBatch();
        queue.endBatch(false);
        assertFalse(queue.isBatch());
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void startBatch_second_call_throws() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.startBatch();
        queue.startBatch(); //Expected to throw
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void endBatch_throws_without_start_notify() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.endBatch(true); //Expected to throw
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void endBatch_throws_without_start_dont_notify() {
        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                InstrumentationRegistry.getContext(), false, null);

        queue.endBatch(false); //Expected to throw
    }

    @SmallTest
    @Test
    public void onContentChanged_no_batch() throws InterruptedException {
        final Uri uri = buildUri();

        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread(UUID.randomUUID().toString(), ThreadPriority.DEFAULT);
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final ContentObserver observer = new ContentObserver(new Handler(thread.getLooper())) {
                @Override
                public void onChange(final boolean selfChange) {
                    super.onChange(selfChange);

                    latch.countDown();
                }
            };
            InstrumentationRegistry.getContext().getContentResolver()
                    .registerContentObserver(uri, true, observer);

            final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                    InstrumentationRegistry.getContext(), false, null);
            queue.onContentChanged(uri);

            assertTrue(latch.await(1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS));
        } finally {
            thread.quit();
        }
    }

    @MediumTest
    @Test
    public void onContentChanged_batch_notify() throws InterruptedException {
        final Uri uri = buildUri();

        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread(UUID.randomUUID().toString(), ThreadPriority.DEFAULT);
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final ContentObserver observer = new ContentObserver(new Handler(thread.getLooper())) {
                @Override
                public void onChange(final boolean selfChange) {
                    super.onChange(selfChange);

                    latch.countDown();
                }
            };
            InstrumentationRegistry.getContext().getContentResolver()
                    .registerContentObserver(uri, true, observer);

            final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                    InstrumentationRegistry.getContext(), false, null);
            queue.startBatch();
            queue.onContentChanged(uri);

            assertFalse(latch.await(500, TimeUnit.MILLISECONDS));

            queue.endBatch(true);

            assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        } finally {
            thread.quit();
        }
    }

    @MediumTest
    @Test
    public void onContentChanged_batch_dont_notify() throws InterruptedException {
        final Uri uri = buildUri();

        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread(UUID.randomUUID().toString(), ThreadPriority.DEFAULT);
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final ContentObserver observer = new ContentObserver(new Handler(thread.getLooper())) {
                @Override
                public void onChange(final boolean selfChange) {
                    super.onChange(selfChange);

                    latch.countDown();
                }
            };
            InstrumentationRegistry.getContext().getContentResolver()
                    .registerContentObserver(uri, true, observer);

            final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(
                    InstrumentationRegistry.getContext(), false, null);
            queue.startBatch();
            queue.onContentChanged(uri);

            queue.endBatch(false);

            assertFalse(latch.await(500, TimeUnit.MILLISECONDS));
        } finally {
            thread.quit();
        }
    }

    @SmallTest
    @Test
    public void intent_no_batch_no_permission_exported() {
        assertIntentNotification(null, true, buildUri());
    }

    @SmallTest
    @Test
    public void intent_no_batch_permission_exported() {
        assertIntentNotification("com.twofortyfouram.memento.permission.TEST_PERMISSION", true,
                //$NON-NLS-1$
                buildUri());
    }

    @SmallTest
    @Test
    public void intent_no_batch_no_permission_not_exported() {
        assertIntentNotification(null, false, buildUri());
    }

    @MediumTest
    @Test
    public void intent_batch_notify() throws InterruptedException {
        assertIntentNotification(true, true, null, false, buildUri());
    }

    @MediumTest
    @Test
    public void intent_batch_dont_notify() throws InterruptedException {
        assertIntentNotification(true, false, null, false, buildUri());
    }

    @NonNull
    private static Uri buildUri() {
        final Uri.Builder builder = new Uri.Builder();

        builder.scheme(ContentResolver.SCHEME_CONTENT);
        builder.authority(TableOneContract.getContentUri(InstrumentationRegistry.getContext())
                .getAuthority());
        builder.path(Uri.encode(UUID.randomUUID().toString()));

        return builder.build();
    }

    /**
     * @param providerReadPermission Read permission for the Content Provider.
     * @param isProviderExported     True if the ContentProvider is exported.
     * @param changedUri             Uri that changed.
     */
    private void assertIntentNotification(@NonNull final String providerReadPermission,
            final boolean isProviderExported, @NonNull final Uri changedUri) {
        assertIntentNotification(false, false, providerReadPermission, isProviderExported,
                changedUri);
    }

    /**
     * @param isBatch                True if this is a batch operation.
     * @param isBatchSuccessful      True if the batch operation was successful.
     * @param providerReadPermission Optional read permission for the Content Provider.
     * @param isProviderExported     True if the ContentProvider is exported.
     * @param changedUri             Uri that changed.
     */
    private void assertIntentNotification(final boolean isBatch, final boolean isBatchSuccessful,
            @Nullable final String providerReadPermission, final boolean isProviderExported,
            @NonNull final Uri changedUri) {

        final ReceiverContextWrapper context = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        final ContentChangeNotificationQueue queue = new ContentChangeNotificationQueue(context,
                isProviderExported, providerReadPermission);

        if (isBatch) {
            queue.startBatch();
        }
        queue.onContentChanged(changedUri);

        if (isBatch) {
            queue.endBatch(isBatchSuccessful);
        }

        final Collection<SentIntent> intents = context.getAndClearSentIntents();

        if (!isBatch || (isBatch && isBatchSuccessful)) {
            assertThat(intents.size(), is(1));
        } else {
            assertThat(intents.size(), is(0));
        }

        for (final SentIntent sentIntent : intents) {
            assertFalse(sentIntent.getIsOrdered());
            assertFalse(sentIntent.getIsSticky());
            assertThat(sentIntent.getPermission(), is(providerReadPermission));

            final Intent intent = sentIntent.getIntent();
            assertThat(intent, notNullValue());
            assertThat(intent.getAction(), is(Intent.ACTION_PROVIDER_CHANGED));

            if (!isProviderExported) {
                assertThat(intent.getPackage(), is(context.getPackageName()));
            }

            assertThat(intent.getData(), is(changedUri));
        }
    }
}
