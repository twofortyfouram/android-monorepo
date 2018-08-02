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

package com.twofortyfouram.memento.util;

import android.content.ContentResolver;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class QueryBundleBuilderTest {

    @SmallTest
    @Test
    public void notInstantiable() {
        assertThat(QueryBundleBuilder.class, ClassNotInstantiableMatcher.notInstantiable());
    }

    @Test
    @SmallTest
    public void withSelection_not_null_return_value() {
        @NonNull final String selection = "foo"; //$NON-NLS
        @NonNull final String[] selectionArgs = {"bar"}; //$NON-NLS

        final Bundle result = QueryBundleBuilder.withSelection(new Bundle(), selection, selectionArgs);

        assertThat(result, notNullValue());
    }

    @Test
    @SmallTest
    public void withSelection_keys_added() {
        @NonNull final String selection = "foo"; //$NON-NLS
        @NonNull final String[] selectionArgs = {"bar"}; //$NON-NLS

        final Bundle result = QueryBundleBuilder.withSelection(new Bundle(), selection, selectionArgs);

        BundleAssertions.assertHasString(result, ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        BundleAssertions.assertHasStringArray(result, ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);
        BundleAssertions.assertKeyCount(result, 2);
    }

    @Test
    @SmallTest
    public void withSelection_builder() {
        @NonNull final Bundle bundle = new Bundle();
        @NonNull final String selection = "foo"; //$NON-NLS
        @NonNull final String[] selectionArgs = {"bar"}; //$NON-NLS

        final Bundle result = QueryBundleBuilder.withSelection(bundle, selection, selectionArgs);

        assertThat(result, sameInstance(bundle));
    }

    @Test
    @SmallTest
    public void withSelection_preserve_existing_extras() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putInt("int", 1); //$NON-NLS

        @NonNull final String selection = "foo"; //$NON-NLS
        @NonNull final String[] selectionArgs = {"bar"}; //$NON-NLS

        final Bundle result = QueryBundleBuilder.withSelection(bundle, selection, selectionArgs);

        BundleAssertions.assertHasString(result, ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        BundleAssertions.assertHasStringArray(result, ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);
        BundleAssertions.assertKeyCount(result, 3);
    }

    @Test
    @SmallTest
    public void withLimit_keys_added() {
        @NonNull final Bundle result = QueryBundleBuilder.withLimit(new Bundle(), 1);

        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_LIMIT, 1, 1);
        BundleAssertions.assertKeyCount(result, 1);
    }

    @Test
    @SmallTest
    public void withLimit_not_null_return_value() {
        final Bundle result = QueryBundleBuilder.withLimit(new Bundle(), 1);

        assertThat(result, notNullValue());
    }

    @Test
    @SmallTest
    public void withLimit_builder() {
        @NonNull final Bundle bundle = new Bundle();
        final Bundle result = QueryBundleBuilder.withLimit(bundle, 1);

        assertThat(result, sameInstance(bundle));
    }

    @Test
    @SmallTest
    public void withLimit_preserve_existing_extras() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putString("foo", "bar"); //$NON-NLS

        final Bundle result = QueryBundleBuilder.withLimit(bundle, 1);

        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_LIMIT, 1, 1);
        BundleAssertions.assertKeyCount(result, 2);
    }

    @Test
    @SmallTest
    public void withOffsetAndLimit_keys_added() {
        @NonNull final Bundle result = QueryBundleBuilder.withOffsetAndLimit(new Bundle(), 1, 2);

        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_OFFSET, 1, 1);
        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_LIMIT, 2, 2);
        BundleAssertions.assertKeyCount(result, 2);
    }

    @Test
    @SmallTest
    public void withOffsetAndLimit_not_null_return_value() {
        final Bundle result = QueryBundleBuilder.withOffsetAndLimit(new Bundle(), 1, 2);

        assertThat(result, notNullValue());
    }

    @Test
    @SmallTest
    public void withOffsetAndLimit_builder() {
        @NonNull final Bundle bundle = new Bundle();
        final Bundle result = QueryBundleBuilder.withOffsetAndLimit(bundle, 1, 2);

        assertThat(result, sameInstance(bundle));
    }

    @Test
    @SmallTest
    public void withOffsetAndLimit_preserve_existing_extras() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putString("foo", "bar"); //$NON-NLS

        final Bundle result = QueryBundleBuilder.withOffsetAndLimit(bundle, 1, 2);

        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_OFFSET, 1, 1);
        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_LIMIT, 2, 2);
        BundleAssertions.assertKeyCount(result, 3);
    }

    @Test
    @SmallTest
    public void withSortColumnsAndDirection_keys_added() {
        @NonNull final Bundle result = QueryBundleBuilder.withSortColumnsAndDirection(new Bundle(), new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);

        BundleAssertions.assertHasStringArray(result, ContentResolver.QUERY_ARG_SORT_COLUMNS);
        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);
        BundleAssertions.assertKeyCount(result, 2);
    }

    @Test
    @SmallTest
    public void withSortColumnsAndDirection_not_null_return_value() {
        final Bundle result = QueryBundleBuilder.withSortColumnsAndDirection(new Bundle(), new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);

        assertThat(result, notNullValue());
    }

    @Test
    @SmallTest
    public void withSortColumnsAndDirection_builder() {
        @NonNull final Bundle bundle = new Bundle();
        @NonNull final Bundle result = QueryBundleBuilder.withSortColumnsAndDirection(bundle, new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);

        assertThat(result, sameInstance(bundle));
    }

    @Test
    @SmallTest
    public void withSortColumnsAndDirection_preserve_existing_extras() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putString("foo", "bar"); //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortColumnsAndDirection(bundle, new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);

        BundleAssertions.assertHasStringArray(result, ContentResolver.QUERY_ARG_SORT_COLUMNS);
        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);
        BundleAssertions.assertKeyCount(result, 3);
    }

    @Test
    @SmallTest
    public void withSortColumnsAndDirection_delete_sql_sort() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "bar"); //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortColumnsAndDirection(bundle, new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);

        BundleAssertions.assertHasStringArray(result, ContentResolver.QUERY_ARG_SORT_COLUMNS);
        BundleAssertions.assertHasInt(result, ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);
        BundleAssertions.assertKeyCount(result, 2);
    }


    @Test
    @SmallTest
    public void withSortSql_key_added() {
        @NonNull final String sortSql = "foo"; //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortSql(new Bundle(), sortSql);

        BundleAssertions.assertHasString(result, ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
        BundleAssertions.assertKeyCount(result, 1);
    }

    @Test
    @SmallTest
    public void withSortSql_not_null_return_value() {
        @NonNull final String sortSql = "foo"; //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortSql(new Bundle(), sortSql);

        assertThat(result, notNullValue());
    }

    @Test
    @SmallTest
    public void withSortSql_builder() {
        @NonNull final Bundle bundle = new Bundle();
        @NonNull final String sortSql = "foo"; //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortSql(bundle, sortSql);

        assertThat(result, sameInstance(bundle));
    }

    @Test
    @SmallTest
    public void withSortSql_preserve_existing_extras() {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putString("foo", "bar"); //$NON-NLS

        @NonNull final String sortSql = "foo"; //$NON-NLS

        @NonNull final Bundle result = QueryBundleBuilder.withSortSql(bundle, sortSql);

        BundleAssertions.assertHasString(result, ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
        BundleAssertions.assertKeyCount(result, 2);
    }

    @Test
    @SmallTest
    public void withSortSql_delete_sql_sort() {
        @NonNull final Bundle bundle = QueryBundleBuilder.withSortColumnsAndDirection(new Bundle(), new String[]{"foo"}, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);
        bundle.putString(ContentResolver.QUERY_ARG_SORT_COLLATION, "collation"); //$NON-NLS

        @NonNull final String sortSql = "foo"; //$NON-NLS
        QueryBundleBuilder.withSortSql(bundle, sortSql); //$NON-NLS

        BundleAssertions.assertHasString(bundle, ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortSql);
        BundleAssertions.assertKeyCount(bundle, 1);
    }
}
