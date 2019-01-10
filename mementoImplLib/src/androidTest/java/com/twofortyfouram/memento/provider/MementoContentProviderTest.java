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
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.format.DateUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.*;
import androidx.test.rule.provider.ProviderTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.twofortyfouram.memento.contract.BaseColumnsContract;
import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.memento.internal.ContentProviderClientCompat;
import com.twofortyfouram.memento.test.main_process.contract.KeyValueContract;
import com.twofortyfouram.memento.test.main_process.contract.LatestKeyValueContractView;
import com.twofortyfouram.memento.test.main_process.contract.TestTableOneContract;
import com.twofortyfouram.memento.test.main_process.contract.TestYouCanHazNoContract;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderImpl;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderUtil;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.spackle.AndroidSdkVersion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests the {@link ContentProviderImpl} in isolation as a proxy for testing
 * {@link MementoContentProvider}.
 */
@RunWith(AndroidJUnit4.class)
public final class MementoContentProviderTest {

    @Rule
    public ProviderTestRule mProviderRule = null;

    @Before
    public void setup() {
        mProviderRule = new ProviderTestRule.Builder(ContentProviderImpl.class,
                ContentProviderUtil.getContentAuthorityString(
                        ApplicationProvider.getApplicationContext())).setPrefix(UUID.randomUUID().toString()).build();
    }

    @After
    public void tearDown() {
        @Nullable ContentProviderClient client = null;
        try {
            client = mProviderRule.getResolver()
                    .acquireContentProviderClient(ContentProviderUtil.getContentAuthorityString(
                            ApplicationProvider.getApplicationContext()));

            client.getLocalContentProvider().shutdown();
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
            }
        }
    }

    @SmallTest
    @Test
    public void isSlowDiskAccess() {
        assertFalse(MementoContentProvider.IS_SLOW_ACCESS_ENABLED);
    }

    @SmallTest
    @Test
    public void newAndIdSelection_non_null_selection() {
        final String newSelection = MementoContentProvider
                .newAndIdSelection("foo = bar"); //$NON-NLS-1$

        assertThat(newSelection, notNullValue());
        assertThat(newSelection, is("(_id = ?) AND (foo = bar)")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void newAndIdSelection_null_selection() {
        final String newSelection = MementoContentProvider.newAndIdSelection(null);

        assertThat(newSelection, notNullValue());
        assertThat(newSelection, is("_id = ?")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void newAndIdSelectionArgs_null_selection() {
        final String[] newSelectionArgs = MementoContentProvider
                .newAndIdSelectionArgs("1", //$NON-NLS-1$
                        null);

        assertThat(newSelectionArgs, notNullValue());
        assertThat(newSelectionArgs, arrayContaining("1"));//$NON-NLS
    }

    @SmallTest
    @Test
    public void newAndIdSelectionArgs_empty_selection() {
        final String[] newSelectionArgs = MementoContentProvider
                .newAndIdSelectionArgs("1", //$NON-NLS-1$
                        new String[0]);

        assertThat(newSelectionArgs, notNullValue());
        assertThat(newSelectionArgs, arrayContaining("1"));//$NON-NLS
    }

    @SmallTest
    @Test
    public void newAndIdSelectionArgs_normal_selection() {
        final String[] newSelectionArgs = MementoContentProvider
                .newAndIdSelectionArgs("1", //$NON-NLS-1$
                        new String[]{"foo"}); //$NON-NLS-1$

        assertThat(newSelectionArgs, notNullValue());
        assertThat(newSelectionArgs, arrayContaining("1", "foo"));//$NON-NLS
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void query_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.query(TestYouCanHazNoContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null, null);
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void insert_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestYouCanHazNoContract.getContentUri(ApplicationProvider.getApplicationContext()), new ContentValues());
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void update_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.update(TestYouCanHazNoContract.getContentUri(ApplicationProvider.getApplicationContext()), new ContentValues(),
                null,
                null);
    }


    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void delete_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.delete(TestYouCanHazNoContract.getContentUri(ApplicationProvider.getApplicationContext()),
                null,
                null);
    }

    @SmallTest
    @Test
    public void query_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null,
                        null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TestTableOneContract._ID,
                    TestTableOneContract.COLUMN_STRING_COLUMN_ONE));
        }
    }

    @SmallTest
    @Test
    public void query_projection_single() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), new String[]{
                        TestTableOneContract._ID
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnNames(), arrayContainingInAnyOrder(TestTableOneContract._ID));

            assertThat(cursor.getColumnIndex(TestTableOneContract._ID), is(0));
        }
    }

    @SmallTest
    @Test
    public void query_notify_uri_single() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null, null)) {

            assertThat(cursor.getNotificationUri(), is(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())));
        }
    }

    @SmallTest
    @Test
    public void query_notify_uri_single_stripped() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(ContentUris.withAppendedId(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), 1), null, null, null, null)) {

            assertThat(cursor.getNotificationUri(), is(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())));
        }
    }

    @SmallTest
    @Test
    public void query_notify_uri_multiple() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null, null)) {

            if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.Q)) {
                assertThat(cursor.getNotificationUris(), contains(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext())));
            }
            else {
                assertThat(cursor.getNotificationUri(), is(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext())));
            }
        }
    }

    @SmallTest
    @Test
    public void query_sort_order() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String sortBy = String.format(Locale.US,
                "%s COLLATE LOCALIZED ASC", //$NON-NLS-1$
                TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null,
                        sortBy)) {

            assertThat(cursor.getCount(), is(4));
            assertThat(cursor.getColumnCount(), is(2));

            final ArrayList<String> results = new ArrayList<>(4);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(cursor
                        .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
            }

            assertThat(results, contains("a", "b", "c", "d")); //$NON-NLS
        }
    }

    @SmallTest
    @Test
    public void query_selection_with_args() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String selection = String.format(Locale.US,
                "%s != ?", TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "a"}; //$NON-NLS-1$
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, selection,
                        selectionArgs, null)) {

            assertThat(cursor.getCount(), is(3));

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                assertThat(
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                        not("a")); //$NON-NLS
            }
        }
    }

    @SmallTest
    @Test
    public void query_id_in_path() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final Uri uri = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value_one")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value_two")); //$NON-NLS-1$

        try (final Cursor cursor = resolver.query(uri, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
        }
    }

    @SmallTest
    @Test
    public void query_limit() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null,
                        null)) {
            // no limit returns 3 items
            assertThat(cursor.getCount(), is(3));
        }

        // limit to 1 item
        @SuppressWarnings("deprecation") final Uri uri = MementoContract.addLimit(TestTableOneContract
                .getContentUri(ApplicationProvider.getApplicationContext())
                .buildUpon(), 1).build();
        try (final Cursor cursor = resolver.query(uri, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
        }
    }

    @SmallTest
    @Test
    public void query_count_empty() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), new String[]{
                        TestTableOneContract._COUNT
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(TestTableOneContract._COUNT)), is(0));
        }
    }

    @SmallTest
    @Test
    public void query_count_non_empty() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value")); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), new String[]{
                        TestTableOneContract._COUNT
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(TestTableOneContract._COUNT)), is(1));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TestTableOneContract._ID,
                    TestTableOneContract.COLUMN_STRING_COLUMN_ONE));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_null_projection_one() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                        new String[]{TestTableOneContract._ID}, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(1));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TestTableOneContract._ID));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_null() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TestTableOneContract._ID,
                    TestTableOneContract.COLUMN_STRING_COLUMN_ONE));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }


    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_empty() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, Bundle.EMPTY, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TestTableOneContract._ID,
                    TestTableOneContract.COLUMN_STRING_COLUMN_ONE));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_selection_with_args() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("d")); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, String.format(Locale.US,
                "%s != ?", TestTableOneContract.COLUMN_STRING_COLUMN_ONE)); //$NON-NLS-1$
        bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, new String[]{
                "a"}); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null,
                        bundle, null)) {

            assertThat(cursor.getCount(), is(3));

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                assertThat(
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                        not("a")); //$NON-NLS
            }
        }
    }


    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_selection_with_args_bad() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("d")); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_SQL_SELECTION, 1); //$NON-NLS-1$
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, "foo"); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null,
                        bundle, null)) {

            assertThat(cursor.getCount(), is(4));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_sort_order() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String sortBy = String.format(
                "%s COLLATE LOCALIZED ASC", //$NON-NLS-1$
                TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortBy);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null
                )) {

            assertThat(cursor.getCount(), is(4));
            assertThat(cursor.getColumnCount(), is(2));

            final ArrayList<String> results = new ArrayList<>(4);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(cursor
                        .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
            }

            assertThat(results, contains("a", "b", "c", "d")); //$NON-NLS

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(1));
            assertThat(cursor.getExtras().getStringArray(ContentResolver.EXTRA_HONORED_ARGS),
                    arrayContainingInAnyOrder(ContentResolver.QUERY_ARG_SQL_SORT_ORDER));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_sort_order_invalid_type() {
        final ContentResolver resolver = mProviderRule.getResolver();

        // Put a long instead of an int
        final Bundle bundle = new Bundle();
        bundle.putLong(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, 1);
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {

            assertThat(cursor.getCount(), is(0));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_limit() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        // No limit returns 3 items
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Limit to 1 item
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(1));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(1));
            assertThat(cursor.getExtras().getStringArray(ContentResolver.EXTRA_HONORED_ARGS),
                    arrayContaining(ContentResolver.QUERY_ARG_LIMIT));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_limit_invalid_long() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        // Put a long instead of an int
        final Bundle bundle = new Bundle();
        bundle.putLong(ContentResolver.QUERY_ARG_LIMIT, 1);
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {

            assertThat(cursor.getCount(), is(3));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_limit_invalid_string() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        // Put a long instead of an int
        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_LIMIT, "1"); //$NON-NLS
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {

            assertThat(cursor.getCount(), is(3));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_limit_both() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        @SuppressWarnings("deprecation") final Uri uri = MementoContract
                .addLimit(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()).buildUpon(), 1).build();

        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 2); //$NON-NLS
        try (final Cursor cursor = resolver
                .query(uri, null, bundle, null)) {

            assertThat(cursor.getCount(), is(2));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(1));
            assertThat(cursor.getExtras().getStringArray(ContentResolver.EXTRA_HONORED_ARGS),
                    arrayContaining(ContentResolver.QUERY_ARG_LIMIT));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_limit_both_invalid_bundle() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        @SuppressWarnings("deprecation") final Uri uri = MementoContract
                .addLimit(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()).buildUpon(), 1).build();

        final Bundle bundle = new Bundle();
        bundle.putLong(ContentResolver.QUERY_ARG_LIMIT, 2); //$NON-NLS
        try (final Cursor cursor = resolver
                .query(uri, null, bundle, null)) {

            assertThat(cursor.getCount(), is(3));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
            assertThat(cursor.getExtras().getStringArray(ContentResolver.EXTRA_HONORED_ARGS),
                    not(arrayContaining(ContentResolver.QUERY_ARG_LIMIT)));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_offset() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values1 = TestTableOneContract
                .getContentValues("test_value1"); //$NON-NLS-1$

        final String test_value2 = "test_value2"; //$NON-NLS-1$
        final ContentValues values2 = TestTableOneContract.getContentValues(test_value2); //$NON-NLS-1$

        final String test_value3 = "test_value3"; //$NON-NLS-1$
        final ContentValues values3 = TestTableOneContract.getContentValues(test_value3);

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values1);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values2);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values3);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Limit to 2 items with offset 1
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, 1);
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 2);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(2));

            int columnIndex = cursor
                    .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE);
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getString(columnIndex), is(test_value2));
            assertTrue(cursor.moveToNext());
            assertThat(cursor.getString(columnIndex), is(test_value3));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(1));
            assertThat(cursor.getExtras().getStringArray(ContentResolver.EXTRA_HONORED_ARGS),
                    arrayContaining(ContentResolver.QUERY_ARG_LIMIT,
                            ContentResolver.QUERY_ARG_OFFSET));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_offset_without_limit() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value1"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Missing limit for offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET,
                1); //will be ignored as limit is not specified

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
            assertThat(cursor.getCount(), is(3));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_offset_invalid() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final String test_value1 = "test_value1"; //NON-NLS
        final String test_value2 = "test_value2"; //NON-NLS
        final ContentValues values1 = TestTableOneContract.getContentValues(test_value1); //$NON-NLS-1$
        final ContentValues values2 = TestTableOneContract.getContentValues(test_value2); //$NON-NLS-1$
        final ContentValues values3 = TestTableOneContract
                .getContentValues("test_value3"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values1);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values2);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values3);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Negative offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 2);
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, -1); //will be ignored

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(2));

            int columnIndex = cursor
                    .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE);
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getString(columnIndex), is(test_value1));
            assertTrue(cursor.moveToNext());
            assertThat(cursor.getString(columnIndex), is(test_value2));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_offset_beyond_max_rows() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value1"); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        // No limit returns 5 items
        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Negative offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, 4);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(0));
        }
    }

    @SmallTest
    @Test
    public void insert_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri resultUri = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        assertThat(resultUri, notNullValue());
    }

    @SmallTest
    @Test
    public void insert_id_in_uri() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = ContentUris
                .withAppendedId(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), 1234);
        final Uri resultUri = resolver.insert(uri, values);

        assertThat(resultUri, notNullValue());
        assertThat(resultUri.getLastPathSegment(), is("1234")); //$NON-NLS

        // Ensure values wasn't mutated by the insert method
        assertThat(values, is(TestTableOneContract.getContentValues("test_value"))); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void update_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        assertThat(
                resolver.update(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values, null, null),
                is(0));
    }

    @SmallTest
    @Test
    public void update_no_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValues = TestTableOneContract
                .getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = resolver
                .insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), initialValues);

        final ContentValues updatedValues = TestTableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        assertThat(resolver.update(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                updatedValues, null, null), is(1));

        try (final Cursor cursor = resolver.query(uri, null, null, null, null);) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TestTableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                initialValuesOne);

        final ContentValues initialValuesTwo = TestTableOneContract
                .getContentValues("test_value_two"); //$NON-NLS-1$
        final Uri uriTwo = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                initialValuesTwo);

        final ContentValues updatedValues = TestTableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        final String selection = String.format(Locale.US,
                "%s = ?", TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "test_value_one"}; //$NON-NLS-1$
        assertThat(resolver.update(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                updatedValues, selection, selectionArgs), is(1));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }

        try (final Cursor cursor = resolver.query(uriTwo, null, null, null, null)) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesTwo.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_selection_no_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TestTableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                initialValuesOne);

        final ContentValues updatedValues = TestTableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        final String selection = String.format(Locale.US,
                "%s = ?", TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "bork_bork_bork"}; //$NON-NLS-1$
        assertThat(resolver.update(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                updatedValues, selection, selectionArgs), is(0));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesOne.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_id_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TestTableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                initialValuesOne);

        final ContentValues initialValuesTwo = TestTableOneContract
                .getContentValues("test_value_two"); //$NON-NLS-1$
        final Uri uriTwo = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                initialValuesTwo);

        final ContentValues updatedValues = TestTableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        assertThat(resolver.update(uriOne, updatedValues, null, null), is(1));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }

        // Note: This also tests that the ID of the record is unchanged
        // during the update
        try (final Cursor cursor = resolver.query(uriTwo, null, null, null, null);) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesTwo.getAsString(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void delete_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        assertThat(resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null),
                is(0));
    }

    @SmallTest
    @Test
    public void delete_no_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value")); //$NON-NLS-1$

        assertThat(resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null),
                is(1));

        assertCount(0);
    }

    @SmallTest
    @Test
    public void delete_with_id_in_path() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values);

        assertThat(resolver.delete(uri, null, null), is(1));

        assertCount(2);
    }

    @SmallTest
    @Test
    public void delete_with_selection() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value_one")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value_two")); //$NON-NLS-1$
        resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestTableOneContract.getContentValues("test_value_three")); //$NON-NLS-1$

        final String selection = String.format(Locale.US,
                "%s = ?", TestTableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "test_value_two"}; //$NON-NLS-1$
        assertThat(resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), selection,
                selectionArgs), is(1));

        assertCount(2);
    }

    @SmallTest
    @Test
    public void applyBatch_init() throws Exception {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation
                .newAssertQuery(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())).withExpectedCount(0)
                .build());

        resolver.applyBatch(ContentProviderUtil.getContentAuthorityString(ApplicationProvider.getApplicationContext()), ops);
    }

    @MediumTest
    @Test
    public void applyBatch_abort() throws RemoteException {
        final ContentResolver resolver = mProviderRule.getResolver();

        /*
         * When an operation is aborted, none of the operations should be performed.
         *
         * In addition, no content notification should occur.
         */

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()))
                .withValues(TestTableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$

        // This query will fail, aborting the entire transaction
        ops.add(ContentProviderOperation
                .newAssertQuery(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())).withExpectedCount(5)
                .build());

        try {
            resolver.applyBatch(ContentProviderUtil.getContentAuthorityString(ApplicationProvider.getApplicationContext()), ops);
            fail("Should have thrown an exception"); //$NON-NLS-1$
        } catch (final OperationApplicationException e) {
            // Expected exception
        }

        assertCount(0);
    }

    @SmallTest
    @Test
    public void applyBatch_success() throws RemoteException, OperationApplicationException {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()))
                .withValues(TestTableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()))
                .withValues(TestTableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()))
                .withValues(TestTableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newUpdate(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()))
                .withValues(TestTableOneContract.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        resolver.applyBatch(ContentProviderUtil.getContentAuthorityString(ApplicationProvider.getApplicationContext()), ops);

        try (final Cursor cursor = resolver
                .query(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null, null,
                        null)) {
            assertThat(cursor.getCount(), is(3));
        }
    }

    /*
     * This is glass-box test intended to verify the provider's lazy initialization with a bulk
     * insert as the first call.
     */
    @SmallTest
    @Test
    public void bulkInsert_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues[] values = new ContentValues[1];
        values[0] = TestTableOneContract.getContentValues("test_value"); //$NON-NLS-1$

        assertThat(resolver.bulkInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), values),
                is(1));
    }

    @SmallTest
    @Test
    public void bulkInsert_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues[] contentValues = {
                TestTableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                TestTableOneContract.getContentValues("test_value_two")}; //$NON-NLS-1$

        assertThat(resolver.bulkInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), contentValues),
                is(2));
        assertCount(2);
    }

    @SmallTest
    @Test
    public void bulkInsert_abort() {
        final ContentResolver resolver = mProviderRule.getResolver();

        // Clear any data left over?
        resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null);

        /*
         * Null violates constraints, so this will throw an exception.
         */
        final ContentValues cv = new ContentValues();
        cv.put(TestTableOneContract.COLUMN_STRING_COLUMN_ONE, (String) null);

        final ContentValues[] contentValues = {
                TestTableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                cv
        };

        try {
            resolver.bulkInsert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), contentValues);
            fail();
        } catch (final SQLiteException e) {
            // Expected exception
        }

        assertCount(0);
    }

    @SmallTest
    @Test
    public void runInTransaction_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        // Don't convert to try with resources.
        @Nullable ContentProviderClient client = null;
        try {
            client = resolver
                    .acquireContentProviderClient(ContentProviderUtil
                            .getContentAuthorityString(ApplicationProvider.getApplicationContext()));
            final ContentProviderImpl provider = (ContentProviderImpl) client
                    .getLocalContentProvider();

            provider.runInTransaction(new Transactable() {
                @Nullable
                @Override
                public Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle) {
                    resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null, null);

                    return null;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(@NonNull final Parcel parcel, final int i) {

                }
            }, new Bundle());
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
            }
        }
    }

    @SmallTest
    @Test
    public void runInTransaction_nested() {
        final ContentResolver resolver = mProviderRule.getResolver();

        // Don't convert to try with resources.
        @Nullable ContentProviderClient client = null;
        try {
            client = resolver
                    .acquireContentProviderClient(ContentProviderUtil
                            .getContentAuthorityString(ApplicationProvider.getApplicationContext()));

            final ContentProviderImpl provider = (ContentProviderImpl) client
                    .getLocalContentProvider();

            provider.runInTransaction(new Transactable() {
                @Nullable
                @Override
                public Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle) {
                    final ArrayList<ContentProviderOperation> ops
                            = new ArrayList<>(
                            1);
                    ops.add(ContentProviderOperation.newDelete(
                            TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())).build());

                    try {
                        resolver.applyBatch(
                                ContentProviderUtil.getContentAuthorityString(ApplicationProvider.getApplicationContext()), ops);
                    } catch (final OperationApplicationException | RemoteException e) {
                        throw new AssertionError(e);
                    }

                    return null;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(@NonNull final Parcel parcel, final int i) {

                }
            }, new Bundle());
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
            }
        }
    }

    @SmallTest
    @Test
    public void runInTransaction_nested_multiple_threads() {
        /*
         * This tests that transactions are mutually exclusive. The second transaction should be
         * blocked from execution until the first transaction completes.
         *
         * This test must be run here to bypass checks on the Transactable interface being static, as opposed to in the
         * Integration test.  This is the easiest way to hack this test in, as local access to the latches is needed.
         * We could do it with static fields in static classes, but that seems terribly gross and potentially easy to
         * break due to threading.
         */

        final ContentResolver resolver = mProviderRule.getResolver();

        final CountDownLatch transactionOneStartLatch = new CountDownLatch(1);
        final CountDownLatch keepTransactionAliveLatch = new CountDownLatch(1);

        // Don't convert to try with resources.
        @Nullable ContentProviderClient client = null;
        try {
            client = resolver
                    .acquireContentProviderClient(ContentProviderUtil
                            .getContentAuthorityString(ApplicationProvider.getApplicationContext()));

            final ContentProviderImpl provider = (ContentProviderImpl) client
                    .getLocalContentProvider();
            //noinspection Convert2Lambda
            new Thread(new Runnable() {
                @Override
                public void run() {
                    provider.runInTransaction(
                            new Transactable() {
                                @Nullable
                                @Override
                                public Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle) {
                                    transactionOneStartLatch.countDown();
                                    resolver.insert(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()),
                                            TestTableOneContract
                                                    .getContentValues("test_value")); //$NON-NLS-1$

                                    try {
                                        assertFalse(keepTransactionAliveLatch.await(
                                                1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS));
                                    } catch (final InterruptedException e) {
                                        throw new AssertionError(e);
                                    }

                                    return null;
                                }

                                @Override
                                public int describeContents() {
                                    return 0;
                                }

                                @Override
                                public void writeToParcel(@NonNull final Parcel parcel, final int i) {

                                }
                            }, new Bundle());
                }
            }).start();

            final CountDownLatch threadTwoLatch = new CountDownLatch(1);
            //noinspection Convert2Lambda
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        transactionOneStartLatch.await();
                    } catch (final InterruptedException e) {
                        throw new AssertionError(e);
                    }

                    provider.runInTransaction(
                            new Transactable() {
                                @Nullable
                                @Override
                                public Bundle runInTransaction(@NonNull final Context context,
                                                               @NonNull final Bundle bundle) {
                                    resolver.delete(TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext()), null,
                                            null);

                                    keepTransactionAliveLatch.countDown();

                                    return null;
                                }

                                @Override
                                public int describeContents() {
                                    return 0;
                                }

                                @Override
                                public void writeToParcel(@NonNull final Parcel parcel, final int i) {

                                }
                            }, new Bundle());

                    threadTwoLatch.countDown();
                }
            }).start();

            try {
                threadTwoLatch.await();
            } catch (final InterruptedException e) {
                throw new AssertionError(e);
            }

        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
                client = null;
            }
        }

        assertCount(0);
    }

    @LargeTest
    @FlakyTest
    @Test
    public void runInTransaction_stress() {
        /*
         * This is a non-deterministic test, in that it relies on multiple iterations to catch a problem.
         */
        for (int x = 0; x < 30; x++) {
            runInTransaction_nested_multiple_threads();
        }
    }

    /**
     * Asserts that {@link TestTableOneContract} has {@code count} rows.
     *
     * @param count Number of rows to assert exist in the table.
     */
    private void assertCount(final int count) {
        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(),
                TestTableOneContract.getContentUri(ApplicationProvider.getApplicationContext())), is(count));
    }
}
