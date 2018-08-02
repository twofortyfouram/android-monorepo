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

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.rule.provider.ProviderTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.memento.internal.ContentProviderClientCompat;
import com.twofortyfouram.memento.test.ContentProviderImpl;
import com.twofortyfouram.memento.test.TableOneContract;
import com.twofortyfouram.memento.test.YouCanHazNoContract;
import com.twofortyfouram.memento.util.MementoProviderUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
                ContentProviderImpl.getContentAuthority(
                        getContext())).setPrefix(UUID.randomUUID().toString()).build();
    }

    @After
    public void tearDown() {
        @Nullable ContentProviderClient client = null;
        try {
            client = mProviderRule.getResolver()
                    .acquireContentProviderClient(ContentProviderImpl.getContentAuthority(
                            getContext()));

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

        resolver.query(YouCanHazNoContract.getContentUri(getContext()), null, null, null, null);
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void insert_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(YouCanHazNoContract.getContentUri(getContext()), new ContentValues());
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void update_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.update(YouCanHazNoContract.getContentUri(getContext()), new ContentValues(),
                null,
                null);
    }


    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void delete_haz_no() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.delete(YouCanHazNoContract.getContentUri(getContext()),
                null,
                null);
    }

    @SmallTest
    @Test
    public void query_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
                        null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TableOneContract._ID,
                    TableOneContract.COLUMN_STRING_COLUMN_ONE));
        }
    }

    @SmallTest
    @Test
    public void query_projection_single() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), new String[]{
                        TableOneContract._ID
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnNames(), arrayContainingInAnyOrder(TableOneContract._ID));

            assertThat(cursor.getColumnIndex(TableOneContract._ID), is(0));
        }
    }

    @SmallTest
    @Test
    public void query_sort_order() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String sortBy = String.format(Locale.US,
                "%s COLLATE LOCALIZED ASC", //$NON-NLS-1$
                TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
                        sortBy)) {

            assertThat(cursor.getCount(), is(4));
            assertThat(cursor.getColumnCount(), is(2));

            final ArrayList<String> results = new ArrayList<>(4);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
            }

            assertThat(results, contains("a", "b", "c", "d")); //$NON-NLS
        }
    }

    @SmallTest
    @Test
    public void query_selection_with_args() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String selection = String.format(Locale.US,
                "%s != ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "a"}; //$NON-NLS-1$
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, selection,
                        selectionArgs, null)) {

            assertThat(cursor.getCount(), is(3));

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                assertThat(
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                        not("a")); //$NON-NLS
            }
        }
    }

    @SmallTest
    @Test
    public void query_id_in_path() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final Uri uri = resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_one")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_two")); //$NON-NLS-1$

        try (final Cursor cursor = resolver.query(uri, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
        }
    }

    @SmallTest
    @Test
    public void query_limit() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
                        null)) {
            // no limit returns 3 items
            assertThat(cursor.getCount(), is(3));
        }

        // limit to 1 item
        @SuppressWarnings("deprecation") final Uri uri = MementoContract.addLimit(TableOneContract
                .getContentUri(getContext())
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
                .query(TableOneContract.getContentUri(getContext()), new String[]{
                        TableOneContract._COUNT
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(TableOneContract._COUNT)), is(0));
        }
    }

    @SmallTest
    @Test
    public void query_count_non_empty() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value")); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), new String[]{
                        TableOneContract._COUNT
                }, null, null, null)) {
            assertThat(cursor.getCount(), is(1));
            assertTrue(cursor.moveToFirst());
            assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(TableOneContract._COUNT)), is(1));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TableOneContract._ID,
                    TableOneContract.COLUMN_STRING_COLUMN_ONE));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_null_projection_one() {
        final ContentResolver resolver = mProviderRule.getResolver();

        try (Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()),
                        new String[]{TableOneContract._ID}, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(1));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TableOneContract._ID));

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
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TableOneContract._ID,
                    TableOneContract.COLUMN_STRING_COLUMN_ONE));

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
                .query(TableOneContract.getContentUri(getContext()), null, Bundle.EMPTY, null)) {

            assertThat(cursor.getCount(), is(0));
            assertThat(cursor.getColumnCount(), is(2));

            final String[] actualColumns = cursor.getColumnNames();
            assertThat(actualColumns, arrayContainingInAnyOrder(TableOneContract._ID,
                    TableOneContract.COLUMN_STRING_COLUMN_ONE));

            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_selection_with_args() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("d")); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, String.format(Locale.US,
                "%s != ?", TableOneContract.COLUMN_STRING_COLUMN_ONE)); //$NON-NLS-1$
        bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, new String[]{
                "a"}); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null,
                        bundle, null)) {

            assertThat(cursor.getCount(), is(3));

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                assertThat(
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                        not("a")); //$NON-NLS
            }
        }
    }


    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    public void query_bundle_selection_with_args_bad() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("d")); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_SQL_SELECTION, 1); //$NON-NLS-1$
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, "foo"); //$NON-NLS-1$

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null,
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

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("c")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("a")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("b")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("d")); //$NON-NLS-1$

        final String sortBy = String.format(
                "%s COLLATE LOCALIZED ASC", //$NON-NLS-1$
                TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$

        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortBy);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null
                )) {

            assertThat(cursor.getCount(), is(4));
            assertThat(cursor.getColumnCount(), is(2));

            final ArrayList<String> results = new ArrayList<>(4);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(cursor
                        .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
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
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {

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

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        // No limit returns 3 items
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Limit to 1 item
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {
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

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        // Put a long instead of an int
        final Bundle bundle = new Bundle();
        bundle.putLong(ContentResolver.QUERY_ARG_LIMIT, 1);
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {

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

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        // Put a long instead of an int
        final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_LIMIT, "1"); //$NON-NLS
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {

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

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        @SuppressWarnings("deprecation") final Uri uri = MementoContract
                .addLimit(TableOneContract.getContentUri(getContext()).buildUpon(), 1).build();

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

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        @SuppressWarnings("deprecation") final Uri uri = MementoContract
                .addLimit(TableOneContract.getContentUri(getContext()).buildUpon(), 1).build();

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

        final ContentValues values1 = TableOneContract
                .getContentValues("test_value1"); //$NON-NLS-1$

        final String test_value2 = "test_value2"; //$NON-NLS-1$
        final ContentValues values2 = TableOneContract.getContentValues(test_value2); //$NON-NLS-1$

        final String test_value3 = "test_value3"; //$NON-NLS-1$
        final ContentValues values3 = TableOneContract.getContentValues(test_value3);

        resolver.insert(TableOneContract.getContentUri(getContext()), values1);
        resolver.insert(TableOneContract.getContentUri(getContext()), values2);
        resolver.insert(TableOneContract.getContentUri(getContext()), values3);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Limit to 2 items with offset 1
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, 1);
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 2);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(2));

            int columnIndex = cursor
                    .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE);
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

        final ContentValues values = TableOneContract.getContentValues("test_value1"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Missing limit for offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET,
                1); //will be ignored as limit is not specified

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {
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
        final ContentValues values1 = TableOneContract.getContentValues(test_value1); //$NON-NLS-1$
        final ContentValues values2 = TableOneContract.getContentValues(test_value2); //$NON-NLS-1$
        final ContentValues values3 = TableOneContract
                .getContentValues("test_value3"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values1);
        resolver.insert(TableOneContract.getContentUri(getContext()), values2);
        resolver.insert(TableOneContract.getContentUri(getContext()), values3);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Negative offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 2);
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, -1); //will be ignored

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(2));

            int columnIndex = cursor
                    .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE);
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

        final ContentValues values = TableOneContract.getContentValues("test_value1"); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        // No limit returns 5 items
        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null)) {
            assertThat(cursor.getCount(), is(3));
            assertThat(cursor.getExtras(), notNullValue());
            assertThat(cursor.getExtras().size(), is(0));
        }

        // Negative offset
        final Bundle bundle = new Bundle();
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, 4);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, bundle, null)) {
            assertThat(cursor.getCount(), is(0));
        }
    }

    @SmallTest
    @Test
    public void insert_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri resultUri = resolver.insert(TableOneContract.getContentUri(getContext()), values);
        assertThat(resultUri, notNullValue());
    }

    @SmallTest
    @Test
    public void insert_id_in_uri() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = ContentUris
                .withAppendedId(TableOneContract.getContentUri(getContext()), 1234);
        final Uri resultUri = resolver.insert(uri, values);

        assertThat(resultUri, notNullValue());
        assertThat(resultUri.getLastPathSegment(), is("1234")); //$NON-NLS

        // Ensure values wasn't mutated by the insert method
        assertThat(values, is(TableOneContract.getContentValues("test_value"))); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void update_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        assertThat(
                resolver.update(TableOneContract.getContentUri(getContext()), values, null, null),
                is(0));
    }

    @SmallTest
    @Test
    public void update_no_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValues = TableOneContract
                .getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = resolver
                .insert(TableOneContract.getContentUri(getContext()), initialValues);

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        assertThat(resolver.update(TableOneContract.getContentUri(getContext()),
                updatedValues, null, null), is(1));

        try (final Cursor cursor = resolver.query(uri, null, null, null, null);) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TableOneContract.getContentUri(getContext()),
                initialValuesOne);

        final ContentValues initialValuesTwo = TableOneContract
                .getContentValues("test_value_two"); //$NON-NLS-1$
        final Uri uriTwo = resolver.insert(TableOneContract.getContentUri(getContext()),
                initialValuesTwo);

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        final String selection = String.format(Locale.US,
                "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "test_value_one"}; //$NON-NLS-1$
        assertThat(resolver.update(TableOneContract.getContentUri(getContext()),
                updatedValues, selection, selectionArgs), is(1));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }

        try (final Cursor cursor = resolver.query(uriTwo, null, null, null, null)) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesTwo.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_selection_no_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TableOneContract.getContentUri(getContext()),
                initialValuesOne);

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        final String selection = String.format(Locale.US,
                "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "bork_bork_bork"}; //$NON-NLS-1$
        assertThat(resolver.update(TableOneContract.getContentUri(getContext()),
                updatedValues, selection, selectionArgs), is(0));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesOne.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void update_with_id_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues initialValuesOne = TableOneContract
                .getContentValues("test_value_one"); //$NON-NLS-1$
        final Uri uriOne = resolver.insert(TableOneContract.getContentUri(getContext()),
                initialValuesOne);

        final ContentValues initialValuesTwo = TableOneContract
                .getContentValues("test_value_two"); //$NON-NLS-1$
        final Uri uriTwo = resolver.insert(TableOneContract.getContentUri(getContext()),
                initialValuesTwo);

        final ContentValues updatedValues = TableOneContract
                .getContentValues("test_value_updated"); //$NON-NLS-1$
        assertThat(resolver.update(uriOne, updatedValues, null, null), is(1));

        try (final Cursor cursor = resolver.query(uriOne, null, null, null, null)) {
            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(updatedValues.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }

        // Note: This also tests that the ID of the record is unchanged
        // during the update
        try (final Cursor cursor = resolver.query(uriTwo, null, null, null, null);) {

            assertThat(cursor.getCount(), is(1));

            cursor.moveToFirst();

            assertThat(cursor.getString(cursor
                            .getColumnIndexOrThrow(TableOneContract.COLUMN_STRING_COLUMN_ONE)),
                    is(initialValuesTwo.getAsString(TableOneContract.COLUMN_STRING_COLUMN_ONE)));
        }
    }

    @SmallTest
    @Test
    public void delete_init() {
        final ContentResolver resolver = mProviderRule.getResolver();

        assertThat(resolver.delete(TableOneContract.getContentUri(getContext()), null, null),
                is(0));
    }

    @SmallTest
    @Test
    public void delete_no_selection_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value")); //$NON-NLS-1$

        assertThat(resolver.delete(TableOneContract.getContentUri(getContext()), null, null),
                is(1));

        assertCount(0);
    }

    @SmallTest
    @Test
    public void delete_with_id_in_path() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues values = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$
        final Uri uri = resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);
        resolver.insert(TableOneContract.getContentUri(getContext()), values);

        assertThat(resolver.delete(uri, null, null), is(1));

        assertCount(2);
    }

    @SmallTest
    @Test
    public void delete_with_selection() {
        final ContentResolver resolver = mProviderRule.getResolver();

        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_one")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_two")); //$NON-NLS-1$
        resolver.insert(TableOneContract.getContentUri(getContext()),
                TableOneContract.getContentValues("test_value_three")); //$NON-NLS-1$

        final String selection = String.format(Locale.US,
                "%s = ?", TableOneContract.COLUMN_STRING_COLUMN_ONE); //$NON-NLS-1$
        final String[] selectionArgs = {
                "test_value_two"}; //$NON-NLS-1$
        assertThat(resolver.delete(TableOneContract.getContentUri(getContext()), selection,
                selectionArgs), is(1));

        assertCount(2);
    }

    @SmallTest
    @Test
    public void applyBatch_init() throws Exception {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation
                .newAssertQuery(TableOneContract.getContentUri(getContext())).withExpectedCount(0)
                .build());

        resolver.applyBatch(ContentProviderImpl.getContentAuthority(getContext()), ops);
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
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$

        // This query will fail, aborting the entire transaction
        ops.add(ContentProviderOperation
                .newAssertQuery(TableOneContract.getContentUri(getContext())).withExpectedCount(5)
                .build());

        try {
            resolver.applyBatch(ContentProviderImpl.getContentAuthority(getContext()), ops);
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
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newInsert(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name")).build()); //$NON-NLS-1$
        ops.add(ContentProviderOperation.newUpdate(TableOneContract.getContentUri(getContext()))
                .withValues(TableOneContract.getContentValues("table_name_updated"))
                .build()); //$NON-NLS-1$

        resolver.applyBatch(ContentProviderImpl.getContentAuthority(getContext()), ops);

        try (final Cursor cursor = resolver
                .query(TableOneContract.getContentUri(getContext()), null, null, null,
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
        values[0] = TableOneContract.getContentValues("test_value"); //$NON-NLS-1$

        assertThat(resolver.bulkInsert(TableOneContract.getContentUri(getContext()), values),
                is(1));
    }

    @SmallTest
    @Test
    public void bulkInsert_success() {
        final ContentResolver resolver = mProviderRule.getResolver();

        final ContentValues[] contentValues = {
                TableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                TableOneContract.getContentValues("test_value_two")}; //$NON-NLS-1$

        assertThat(resolver.bulkInsert(TableOneContract.getContentUri(getContext()), contentValues),
                is(2));
        assertCount(2);
    }

    @SmallTest
    @Test
    public void bulkInsert_abort() {
        final ContentResolver resolver = mProviderRule.getResolver();

        // Clear any data left over?
        resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

        /*
         * Null violates constraints, so this will throw an exception.
         */
        final ContentValues cv = new ContentValues();
        cv.put(TableOneContract.COLUMN_STRING_COLUMN_ONE, (String) null);

        final ContentValues[] contentValues = {
                TableOneContract.getContentValues("test_value_one"), //$NON-NLS-1$
                cv
        };

        try {
            resolver.bulkInsert(TableOneContract.getContentUri(getContext()), contentValues);
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
                    .acquireContentProviderClient(ContentProviderImpl
                            .getContentAuthority(getContext()));
            final ContentProviderImpl provider = (ContentProviderImpl) client
                    .getLocalContentProvider();

            provider.runInTransaction(() -> {

                resolver.delete(TableOneContract.getContentUri(getContext()), null, null);

                return null;
            });
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
                    .acquireContentProviderClient(ContentProviderImpl
                            .getContentAuthority(getContext()));

            final ContentProviderImpl provider = (ContentProviderImpl) client
                    .getLocalContentProvider();

            provider.runInTransaction(() -> {
                final ArrayList<ContentProviderOperation> ops
                        = new ArrayList<>(
                        1);
                ops.add(ContentProviderOperation.newDelete(
                        TableOneContract.getContentUri(getContext())).build());

                try {
                    resolver.applyBatch(
                            ContentProviderImpl.getContentAuthority(getContext()), ops);
                } catch (final OperationApplicationException | RemoteException e) {
                    throw new AssertionError(e);
                }

                return null;
            });
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
            }
        }
    }

    /**
     * Asserts that {@link TableOneContract} has {@code count} rows.
     *
     * @param count Number of rows to assert exist in the table.
     */
    private void assertCount(final int count) {
        assertThat(MementoProviderUtil.getCountForUri(mProviderRule.getResolver(),
                TableOneContract.getContentUri(getContext())), is(count));
    }
}
