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

import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.memento.contract.*;
import com.twofortyfouram.memento.test.main_process.ContentProviderImpl;
import com.twofortyfouram.memento.test.main_process.TableOneContract;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.spackle.FileUtil;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Tests the {@link ContentProviderImpl}, a minimal implementation of the abstract class, to
 * test {@link MementoContentProvider}.
 */
@RunWith(AndroidJUnit4.class)
public final class MementoContentProviderIntegrationTest {

    @SmallTest
    @Test
    public void update_content_notification_success() {
        final ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

        // Insert a record and wait for the content notification to clear
        TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);
        try {
            final ContentValues initialValues = TableOneContract
                    .getContentValues("test_value"); //$NON-NLS-1$
            resolver.insert(TableOneContract.getContentUri(getContext()), initialValues);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        observer = getNewRegisteredContentObserver(TableOneContract.getContentUri(getContext()), 1);
        try {
            assertThat(resolver.update(TableOneContract.getContentUri(getContext()),
                    updatedValues, null, null), is(1));
            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }
    }

    @SmallTest
    @Test
    public void update_content_notification_suppressed() {
        final ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

        SystemClock.sleep(500);

        // Insert a record and wait for the content notification to clear
        TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);
        try {
            final ContentValues initialValues = TableOneContract
                    .getContentValues("test_value"); //$NON-NLS-1$
            resolver.insert(TableOneContract.getContentUri(getContext()),
                    initialValues);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()),
                0);
        try {
            assertThat(resolver.update(TableOneContract.getContentUri(getContext()).buildUpon()
                            .appendQueryParameter(
                                    MementoContract.QUERY_STRING_IS_SUPPRESS_NOTIFICATION,
                                    Boolean.TRUE
                                            .toString()).build(),
                    updatedValues, null, null), is(1));
            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }
    }

    @MediumTest
    @Test
    public void update_content_notification_no_success() {
        final ContentResolver resolver = getContext().getContentResolver();

        final ContentValues initialValues = TableOneContract
                .getContentValues("test_value"); //$NON-NLS-1$

        // Insert a record and wait for the content notification to clear
        TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);
        try {
            resolver.insert(TableOneContract.getContentUri(getContext()), initialValues);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        observer = getNewRegisteredContentObserver(TableOneContract.getContentUri(getContext()), 0);
        try {
            final String selection = String.format(Locale.US,
                    "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
            final String[] selectionArgs = {
                    "bork_bork_bork"}; //$NON-NLS-1$
            assertThat(resolver.update(TableOneContract.getContentUri(getContext()),
                    updatedValues, selection, selectionArgs), is(0));
            observer.assertExpectedHits();
        } finally {
            observer.destroy();
            observer = null;
        }
    }

    @SmallTest
    @Test
    public void delete_content_notification_success() {
        final ContentResolver resolver = getContext().getContentResolver();

        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        final ContentValues[] initialValues = {
                TableOneContract.getContentValues("test_value_one"),
                TableOneContract.getContentValues("test_value_two"), TableOneContract
                .getContentValues("test_value_three")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertThat(
                resolver.bulkInsert(TableOneContract.getContentUri(getContext()), initialValues),
                is(3));

        SystemClock.sleep(500);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);

        try {
            final String selection = String.format(Locale.US,
                    "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
            final String[] selectionArgs = {
                    "test_value_two"}; //$NON-NLS-1$
            assertThat(resolver.delete(TableOneContract.getContentUri(getContext()),
                    selection, selectionArgs), is(1));

            assertCount(resolver, 2);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @SmallTest
    @Test
    public void delete_content_notification_suppressed() {
        final ContentResolver resolver = getContext().getContentResolver();

        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        final ContentValues[] initialValues = {
                TableOneContract.getContentValues("test_value_one"),
                TableOneContract.getContentValues("test_value_two"), TableOneContract
                .getContentValues("test_value_three")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertThat(
                resolver.bulkInsert(TableOneContract.getContentUri(getContext()), initialValues),
                is(3));

        SystemClock.sleep(500);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 0);

        try {
            final String selection = String.format(Locale.US,
                    "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
            final String[] selectionArgs = {
                    "test_value_two"}; //$NON-NLS-1$
            assertThat(resolver.delete(TableOneContract.getContentUri(getContext())
                            .buildUpon()
                            .appendQueryParameter(
                                    MementoContract.QUERY_STRING_IS_SUPPRESS_NOTIFICATION,
                                    Boolean.TRUE
                                            .toString()).build(),
                    selection, selectionArgs), is(1));

            assertCount(resolver, 2);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @MediumTest
    @Test
    public void delete_content_notification_no_success() {
        final ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_one")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_two")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_three")); //$NON-NLS-1$

        SystemClock.sleep(500);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 0);
        try {
            final String selection = String.format(Locale.US,
                    "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
            final String[] selectionArgs = {
                    "test_value_four"}; //$NON-NLS-1$
            assertThat(resolver.delete(TableOneContract.getContentUri(getContext()),
                    selection, selectionArgs), is(0));

            assertCount(resolver, 3);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @MediumTest
    @Test
    public void applyBatch_content_notification_abort() throws RemoteException {
        final ContentResolver resolver = getContext().getContentResolver();

        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        SystemClock.sleep(500);

        /*
         * When an operation is aborted, none of the operations should be performed.
         *
         * In addition, no content notification should occur.
         */

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$

        // This query will fail, aborting the entire transaction
        ops.add(ContentProviderOperation
                .newAssertQuery(TableOneContract.getContentUri(getContext())).withExpectedCount(5)
                .build());

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 0);

        try {
            // First, the failed operation should throw an exception.
            try {
                resolver.applyBatch(ContentProviderImpl.getContentAuthority(getContext()),
                        ops);
                fail("Should have thrown an exception"); //$NON-NLS-1$
            } catch (final OperationApplicationException e) {
                // Expected exception
            }

            assertCount(resolver, 0);

            // Finally, the failed operation should not post a content change
            // notification.
            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @MediumTest
    @Test
    public void applyBatch_content_notification_success() throws RemoteException,
            OperationApplicationException {
        final ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        SystemClock.sleep(1 * DateUtils.SECOND_IN_MILLIS);

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newUpdate(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);

        try {
            getContext().getContentResolver().applyBatch(
                    ContentProviderImpl.getContentAuthority(getContext()), ops);

            assertCount(resolver, 3);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @SmallTest
    @Test
    public void bulkInsert_content_notification_success() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clean up from other tests
        resolver.delete(MementoContract
                .addSuppressNotification(TableOneContract.getContentUri(getContext()).buildUpon())
                .build(), null, null);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);

        try {
            final ContentValues[] contentValues = {
                    TableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                    TableOneContract.getContentValues("test_value_two")}; //$NON-NLS-1$

            assertThat(resolver.bulkInsert(TableOneContract.getContentUri(getContext()),
                    contentValues), is(2));

            assertCount(resolver, 2);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @MediumTest
    @Test
    public void bulkInsert_content_notification_abort() {
        final ContentResolver resolver = getContext().getContentResolver();

        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        SystemClock.sleep(500);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 0);

        try {
            try {
                /*
                 * Null violates constraints, so this will throw an exception.
                 */
                final ContentValues values = new ContentValues(1);
                values.putNull(TableOneContract.COLUMN_STRING_COLUMN_ONE);
                final ContentValues[] contentValues = {
                        TableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                        values
                };

                resolver.bulkInsert(TableOneContract.getContentUri(getContext()), contentValues);
                fail();
            } catch (final SQLiteException e) {
                // Expected exception
            }

            assertCount(resolver, 0);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    @SmallTest
    @Test
    public void runInTransaction_content_notification_success() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clean up from other tests
        resolver.delete(MementoContract
                .addSuppressNotification(TableOneContract.getContentUri(getContext()).buildUpon())
                .build(), null, null);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 1);

        try {
            TransactionContract.runInTransaction(getContext(),
                    ContentProviderImpl.getContentAuthorityUri(getContext()), new Transactable_runInTransaction_content_notification_success(), new Bundle());

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
            context.getContentResolver().insert(TableOneContract.getContentUri(getContext()),
                    TableOneContract
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

    @MediumTest
    @Test
    public void runInTransaction_content_notification_abort() {
        final ContentResolver resolver = getContext().getContentResolver();

        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);
        SystemClock.sleep(500);

        final TestContentObserver observer = getNewRegisteredContentObserver(
                TableOneContract.getContentUri(getContext()), 0);

        try {
            try {
                TransactionContract.runInTransaction(getContext(),
                        ContentProviderImpl.getContentAuthorityUri(getContext()),
                        new Transactable_runInTransaction_content_notification_abort(), new Bundle());

                fail();
            } catch (final RuntimeException e) {
                // Expected exception
            }

            assertCount(resolver, 0);

            observer.assertExpectedHits();
        } finally {
            observer.destroy();
        }
    }

    private static final class Transactable_runInTransaction_content_notification_abort implements Transactable {

        public static final Creator<Transactable_runInTransaction_content_notification_abort> CREATOR = new Creator<Transactable_runInTransaction_content_notification_abort>() {
            @Override
            public Transactable_runInTransaction_content_notification_abort createFromParcel(Parcel parcel) {
                return new Transactable_runInTransaction_content_notification_abort();
            }

            @Override
            public Transactable_runInTransaction_content_notification_abort[] newArray(int i) {
                return new Transactable_runInTransaction_content_notification_abort[0];
            }
        };

        @Nullable
        @Override
        public Bundle runInTransaction(@NonNull final Context context,
                                       @NonNull final Bundle bundle) {
            context.getContentResolver().insert(TableOneContract.getContentUri(getContext()),
                    TableOneContract
                            .getContentValues("test_value_one")); //$NON-NLS-1$

            // This exception should abort the
            // transaction
            throw new RuntimeException();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull final Parcel parcel, final int i) {

        }

    }

    @SmallTest
    @Test
    public void call_batch_one() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clear the database from prior tests
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newUpdate(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        final ArrayList<ArrayList<ContentProviderOperation>> opsGroup
                = new ArrayList<>();
        opsGroup.add(ops);

        resolver.call(TableOneContract.getContentUri(getContext()),
                BatchContract.METHOD_BATCH_OPERATIONS, null,
                BatchContractProxy.newCallBundle(opsGroup));

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
                        null)) {
            assertThat(cursor.getCount(), is(3));
        }
    }

    @SmallTest
    @Test
    public void call_batch_two_with_preceeding_failure() {
        final ContentResolver resolver = getContext().getContentResolver();

        // Clear the database from prior tests
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

        final ArrayList<ContentProviderOperation> ops1 = new ArrayList<>();
        ops1.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops1.add(ContentProviderOperation
                .newAssertQuery(TableOneContract.getContentUri(getContext())).withExpectedCount(10)
                .build());

        final ArrayList<ContentProviderOperation> ops2 = new ArrayList<>();
        ops2.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops2.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops2.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops2.add(ContentProviderOperation.newUpdate(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        final ArrayList<ArrayList<ContentProviderOperation>> opsGroup
                = new ArrayList<>();
        opsGroup.add(ops1);
        opsGroup.add(ops2);

        resolver.call(TableOneContract.getContentUri(getContext()),
                BatchContract.METHOD_BATCH_OPERATIONS, null,
                BatchContractProxy.newCallBundle(opsGroup));

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
                        null)) {
            assertThat(cursor.getCount(), is(3));
        }
    }

    @SmallTest
    @Test
    public void call_backup_method_invalid_path() {
        final ContentResolver resolver = getContext().getContentResolver();

        final Bundle result = resolver.call(ContentProviderImpl.getContentAuthorityUri(getContext()),
                BackupContract.METHOD_BACKUP, "1nV@l1d_#path", null); //NON-NLS

        assertThat(result, notNullValue());
        assertFalse(result.getBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false));
    }

    @SmallTest
    @Test
    public void call_backup_valid_path() {
        final File destDir = new File(getContext().getExternalFilesDir(null),
                "output_test_file"); //NON-NLS
        destDir.mkdirs();

        try {
            final ContentResolver resolver = getContext().getContentResolver();

            final Bundle result = resolver
                    .call(ContentProviderImpl.getContentAuthorityUri(getContext()),
                            BackupContract.METHOD_BACKUP, destDir.getAbsolutePath(),
                            null); //NON-NLS

            assertThat(result, notNullValue());
            assertTrue(result.getBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false));

            @NonNull final File backupFile = new File(destDir, BackupContract.FILE_NAME_BACKUP);
            assertThat(backupFile.exists(), is(true));
            assertThat(backupFile.isFile(), is(true));

            // Not testing for -wal or -journal, as those aren't guaranteed to exist
        } finally {
            //Cleanup
            if (destDir.exists()) {
                FileUtil.deleteRecursively(destDir);
            }
        }
    }

    @SmallTest
    @Test
    public void call_backup_method_missing_arg() {
        final ContentResolver resolver = getContext().getContentResolver();

        final Bundle result = resolver.call(ContentProviderImpl.getContentAuthorityUri(getContext()),
                BackupContract.METHOD_BACKUP, null, null);

        assertThat(result, notNullValue());
        assertFalse(result.getBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false));
    }

    /**
     * Asserts that {@link TableOneContract} has {@code count} rows.
     *
     * @param resolver ContentResolver to use.
     * @param count    Number of rows to assert exist in the table.
     */
    private void assertCount(@NonNull final ContentResolver resolver, final int count) {
        try (@Nullable final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
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
