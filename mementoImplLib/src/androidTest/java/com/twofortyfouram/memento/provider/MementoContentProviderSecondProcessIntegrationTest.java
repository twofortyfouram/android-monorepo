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

package com.twofortyfouram.memento.provider;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.filters.Suppress;
import androidx.test.runner.AndroidJUnit4;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.memento.contract.*;
import com.twofortyfouram.memento.test.second_process.SecondProcessContentProviderImpl;
import com.twofortyfouram.memento.test.second_process.TableOneContractSecondProcess;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link SecondProcessContentProviderImpl}, a minimal implementation of the abstract class, to
 * test {@link MementoContentProvider}.
 */
/*
 * This test focuses on just a few scenarios across processes that are more likely to fail.  Multiprocess testing
 * requires Android O and associated entries in the manifest to enable it.
 */
/*
 * TODO [Case 17271]
 * These tests are flaky, probably due to a bug in the beta version of Espresso multiprocess that was available when
 * this test was first written.  Suppressed for now but should be re-evaluated in the future.
 */
@Suppress
@RunWith(AndroidJUnit4.class)
public final class MementoContentProviderSecondProcessIntegrationTest {

    @SmallTest
    @Test
    public void preventCiFromFail() {
        assertThat(true, is(true));
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void runInTransaction_content_notification_success() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clean up from other tests
        resolver.delete(MementoContract
                .addSuppressNotification(TableOneContractSecondProcess.getContentUri(getContext()).buildUpon())
                .build(), null, null);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContractSecondProcess.getContentUri(getContext()), 1);

        try {
            TransactionContract.runInTransaction(getContext(),
                    SecondProcessContentProviderImpl.getContentAuthorityUri(getContext()), new Transactable_runInTransaction_content_notification_success(), new Bundle());

            assertCount(resolver, 1);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    public static class Transactable_runInTransaction_content_notification_success implements Transactable {

        public static final Creator<Transactable_runInTransaction_content_notification_success> CREATOR = new Creator<Transactable_runInTransaction_content_notification_success>() {
            @Override
            public Transactable_runInTransaction_content_notification_success createFromParcel(Parcel parcel) {
                return new Transactable_runInTransaction_content_notification_success();
            }

            @Override
            public Transactable_runInTransaction_content_notification_success[] newArray(int i) {
                return new Transactable_runInTransaction_content_notification_success[0];
            }
        };

        @Nullable
        @Override
        public Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle) {
            context.getContentResolver().insert(TableOneContractSecondProcess.getContentUri(getContext()),
                    TableOneContractSecondProcess
                            .getContentValues("test_value_one")); //$NON-NLS-1$

            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void call_batch_one() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clear the database from prior tests
        resolver.delete(TableOneContractSecondProcess.getContentUri(getContext()), null, null);

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TableOneContractSecondProcess.getContentUri(getContext()))
                .withValues(TableOneContractSecondProcess.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContractSecondProcess.getContentUri(getContext()))
                .withValues(TableOneContractSecondProcess.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContractSecondProcess.getContentUri(getContext()))
                .withValues(TableOneContractSecondProcess.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newUpdate(TableOneContractSecondProcess.getContentUri(getContext()))
                .withValues(TableOneContractSecondProcess.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        final ArrayList<ArrayList<ContentProviderOperation>> opsGroup
                = new ArrayList<>();
        opsGroup.add(ops);

        resolver.call(TableOneContractSecondProcess.getContentUri(getContext()),
                BatchContract.METHOD_BATCH_OPERATIONS, null,
                BatchContractProxy.newCallBundle(opsGroup));

        try (final Cursor cursor = resolver
                .query(TableOneContractSecondProcess.getContentUri(getContext()), null, null, null,
                        null)) {
            assertThat(cursor.getCount(), is(3));
        }
    }


    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void call_backup_valid_path() {
        final File destFile = new File(getContext().getExternalFilesDir(null),
                "output_test_file"); //NON-NLS
        try {
            final ContentResolver resolver = getContext().getContentResolver();

            final Bundle result = resolver
                    .call(SecondProcessContentProviderImpl.getContentAuthorityUri(getContext()),
                            BackupContract.METHOD_BACKUP, destFile.getAbsolutePath(),
                            null); //NON-NLS

            assertThat(result, notNullValue());
            assertTrue(result.getBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false));
        } finally {
            //Cleanup
            if (destFile.exists()) {
                assertTrue(destFile.delete());
            }
        }
    }

    /**
     * Asserts that {@link TableOneContractSecondProcess} has {@code count} rows.
     *
     * @param resolver ContentResolver to use.
     * @param count    Number of rows to assert exist in the table.
     */
    private void assertCount(@NonNull final ContentResolver resolver, final int count) {
        try (@Nullable final Cursor cursor = resolver
                .query(TableOneContractSecondProcess.getContentUri(getContext()), null, null, null,
                        null)) {
            assertThat(cursor.getCount(), is(count));
        }
    }

    /**
     * Helper to register a ContentObserver to listen for content change notifications.
     *
     * @param uriToMonitor     Uri to monitor for hits.
     * @param expectedHitCount Expected number of hits.
     * @return A registered content observer monitoring for hits.
     */
    @NonNull
    public TestContentObserver getNewRegisteredContentObserver(@NonNull final Uri uriToMonitor,
            final int expectedHitCount) {
        final Handler handler = new Handler(HandlerThreadFactory.newHandlerThread(
                TestContentObserver.class.getName(), ThreadPriority.DEFAULT).getLooper());

        /*
         * The MockContentResolver won't deliver content change notifications, so a real
         * ContentResolver is required for this test.
         */
        final ContentResolver resolver = getContext().getContentResolver();

        final TestContentObserver observer = new TestContentObserver(handler, resolver,
                expectedHitCount);

        resolver.registerContentObserver(uriToMonitor, true, observer);

        return observer;
    }

    private static final class TestContentObserver extends ContentObserver {

        /**
         * Amount of time to wait for an asynchronous event to complete.
         */
        /*
         * This value is somewhat arbitrary. A number of tests need to wait for asynchronous events.
         * The longer the wait time, the more likely that the test has completed in the background.
         * On the flip side, setting this value higher also makes the test take significantly longer
         * to complete.
         */
        private static final int DEFAULT_LATCH_WAIT_MILLIS = 1500;

        /**
         * Background handler for receiving content change notifications.
         */
        @NonNull
        private final Handler mHandler;

        /**
         * ContentResolver on which this observer is registered.
         */
        @NonNull
        private final ContentResolver mResolver;

        /**
         * Latch to wait for hits.
         */
        @NonNull
        private final CountDownLatch mLatch;

        /**
         * Number of hits received.
         */
        @NonNull
        private final AtomicInteger mHitCount = new AtomicInteger(0);

        /**
         * Expected number of hits.
         */
        private final int mExpectedHitCount;

        public TestContentObserver(@NonNull final Handler handler,
                @NonNull final ContentResolver resolver, final int expectedHitCount) {
            super(handler);

            mHandler = handler;
            mResolver = resolver;
            mExpectedHitCount = expectedHitCount;
            mLatch = new CountDownLatch(expectedHitCount);
        }

        @Override
        public void onChange(final boolean selfChange) {
            super.onChange(selfChange);

            mHitCount.incrementAndGet();
            mLatch.countDown();
        }

        /**
         * Asserts the expected number of hits were received.
         */
        @Slow(Speed.SECONDS)
        public void assertExpectedHits() {
            if (0 == mExpectedHitCount) {
                SystemClock.sleep(DEFAULT_LATCH_WAIT_MILLIS);

                assertThat(mHitCount.get(), is(mExpectedHitCount));
            } else {
                try {
                    assertTrue(mLatch.await(DEFAULT_LATCH_WAIT_MILLIS, TimeUnit.MILLISECONDS));
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void destroy() {
            mResolver.unregisterContentObserver(this);
            mHandler.getLooper().quit();
        }
    }
}
