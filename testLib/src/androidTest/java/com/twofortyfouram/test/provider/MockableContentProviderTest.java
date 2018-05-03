/*
 * android-test https://github.com/twofortyfouram/android-test
 * Copyright (C) 2014–2017 two forty four a.m. LLC
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

package com.twofortyfouram.test.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public final class MockableContentProviderTest {

    @NonNull
    private static final String TEST_AUTHORITY = "authoritah"; //$NON-NLS

    @NonNull
    private static final Uri TEST_URI = new Uri.Builder().authority(TEST_AUTHORITY).build();

    @NonNull
    private static final String[] TEST_PROJECTION = new String[]{"foo"}; //$NON-NLS

    @NonNull
    private static final String TEST_SELECTION = "_id = ?"; //$NON-NLS

    @NonNull
    private static final String[] TEST_SELECTION_ARGS = new String[]{"123"}; //$NON-NLS

    @NonNull
    private static final String TEST_ORDER_BY = "_id DESC"; //$NON-NLS

    @NonNull
    private static final String TEST_METHOD = "test_method"; //$NON-NLS

    @NonNull
    private static final String TEST_ARG = "test_arg"; //$NON-NLS

    @NonNull
    private static final Bundle TEST_BUNDLE = Bundle.EMPTY;

    @NonNull
    private static final ContentValues TEST_CONTENT_VALUES = new ContentValues();

    static {
        TEST_CONTENT_VALUES.put("TEST", "TEST"); //$NON-NLS
    }

    @Test
    @SmallTest
    public void query_count_0() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getQueryCount(), is(0));
    }

    @Test
    @SmallTest
    public void insert_count_0() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getInsertCount(), is(0));
    }

    @Test
    @SmallTest
    public void update_count_0() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getUpdateCount(), is(0));
    }

    @Test
    @SmallTest
    public void delete_count_0() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getDeleteCount(), is(0));
    }

    @Test
    @SmallTest
    public void call_count_0() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getCallCount(), is(0));
    }

    @Test
    @SmallTest
    public void query_count_1() {
        final MockableContentProvider provider = newProvider();

        provider.query(TEST_URI, null, null, null, null, null);

        assertThat(provider.getQueryCount(), is(1));

        assertThat(provider.getInsertCount(), is(0));
        assertThat(provider.getUpdateCount(), is(0));
        assertThat(provider.getDeleteCount(), is(0));
        assertThat(provider.getCallCount(), is(0));
    }

    @Test
    @SmallTest
    public void insert_count_1() {
        final MockableContentProvider provider = newProvider();

        provider.insert(TEST_URI, new ContentValues());

        assertThat(provider.getInsertCount(), is(1));

        assertThat(provider.getQueryCount(), is(0));
        assertThat(provider.getUpdateCount(), is(0));
        assertThat(provider.getDeleteCount(), is(0));
        assertThat(provider.getCallCount(), is(0));
    }

    @Test
    @SmallTest
    public void update_count_1() {
        final MockableContentProvider provider = newProvider();

        provider.update(TEST_URI, new ContentValues(), null, null);

        assertThat(provider.getUpdateCount(), is(1));

        assertThat(provider.getQueryCount(), is(0));
        assertThat(provider.getInsertCount(), is(0));
        assertThat(provider.getDeleteCount(), is(0));
        assertThat(provider.getCallCount(), is(0));
    }

    @Test
    @SmallTest
    public void delete_count_1() {
        final MockableContentProvider provider = newProvider();

        provider.delete(TEST_URI, null, new String[]{});

        assertThat(provider.getDeleteCount(), is(1));

        assertThat(provider.getQueryCount(), is(0));
        assertThat(provider.getInsertCount(), is(0));
        assertThat(provider.getUpdateCount(), is(0));
        assertThat(provider.getCallCount(), is(0));
    }

    @Test
    @SmallTest
    public void call_count_1() {
        final MockableContentProvider provider = newProvider();

        provider.call("method", null, null);

        assertThat(provider.getCallCount(), is(1));

        assertThat(provider.getQueryCount(), is(0));
        assertThat(provider.getInsertCount(), is(0));
        assertThat(provider.getUpdateCount(), is(0));
        assertThat(provider.getDeleteCount(), is(0));
    }

    @Test
    @SmallTest
    public void query_params_null() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getQueryParams(), nullValue());
    }

    @Test
    @SmallTest
    public void insert_params_null() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getInsertParams(), nullValue());
    }

    @Test
    @SmallTest
    public void update_params_null() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getUpdateParams(), nullValue());
    }

    @Test
    @SmallTest
    public void delete_params_null() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getDeleteParams(), nullValue());
    }

    @Test
    @SmallTest
    public void call_params_null() {
        final MockableContentProvider provider = newProvider();

        assertThat(provider.getCallParams(), nullValue());
    }

    @Test
    @SmallTest
    public void query_params_non_null() {
        final MockableContentProvider provider = newProvider();

        provider.query(TEST_URI, TEST_PROJECTION, TEST_SELECTION, TEST_SELECTION_ARGS,
                TEST_ORDER_BY);

        final MockableContentProvider.QueryParams params = provider.getQueryParams();
        assertThat(params, notNullValue());

        assertThat(params.getUri(), is(TEST_URI));
        assertThat(params.getProjection(), is(TEST_PROJECTION));
        assertThat(params.getSelection(), is(TEST_SELECTION));
        assertThat(params.getSelectionArgs(), is(TEST_SELECTION_ARGS));
        assertThat(params.getOrderBy(), is(TEST_ORDER_BY));

        assertThat(provider.getQueryParams(), nullValue());
    }

    @Test
    @SmallTest
    public void insert_params_non_null() {
        final MockableContentProvider provider = newProvider();

        provider.insert(TEST_URI, TEST_CONTENT_VALUES);

        final MockableContentProvider.InsertParams params = provider.getInsertParams();
        assertThat(params, notNullValue());

        assertThat(params.getUri(), is(TEST_URI));
        assertThat(params.getContentValues(), is(TEST_CONTENT_VALUES));

        assertThat(provider.getInsertParams(), nullValue());
    }

    @Test
    @SmallTest
    public void update_params_non_null() {
        final MockableContentProvider provider = newProvider();

        provider.update(TEST_URI, TEST_CONTENT_VALUES, TEST_SELECTION, TEST_SELECTION_ARGS);

        final MockableContentProvider.UpdateParams params = provider.getUpdateParams();
        assertThat(params, notNullValue());

        assertThat(params.getUri(), is(TEST_URI));
        assertThat(params.getContentValues(), is(TEST_CONTENT_VALUES));
        assertThat(params.getSelection(), is(TEST_SELECTION));
        assertThat(params.getSelectionArgs(), is(TEST_SELECTION_ARGS));

        assertThat(provider.getUpdateParams(), nullValue());
    }

    @Test
    @SmallTest
    public void delete_params_non_null() {
        final MockableContentProvider provider = newProvider();

        provider.delete(TEST_URI, TEST_SELECTION, TEST_SELECTION_ARGS);

        final MockableContentProvider.DeleteParams params = provider.getDeleteParams();
        assertThat(params, notNullValue());

        assertThat(params.getUri(), is(TEST_URI));
        assertThat(params.getSelection(), is(TEST_SELECTION));
        assertThat(params.getSelectionArgs(), is(TEST_SELECTION_ARGS));

        assertThat(provider.getDeleteParams(), nullValue());
    }

    @Test
    @SmallTest
    public void call_params_non_null() {
        final MockableContentProvider provider = newProvider();

        provider.call(TEST_METHOD, TEST_ARG, TEST_BUNDLE);

        final MockableContentProvider.CallParams params = provider.getCallParams();
        assertThat(params, notNullValue());

        assertThat(params.getMethod(), is(TEST_METHOD));
        assertThat(params.getArg(), is(TEST_ARG));
        assertThat(params.getExtras(), notNullValue());

        assertThat(provider.getCallParams(), nullValue());
    }

    @Test
    @SmallTest
    public void query_result() {
        final MockableContentProvider provider = newProvider();

        provider.addQueryResult(new MatrixCursor(new String[]{"foo"}));

        final Cursor result = provider
                .query(TEST_URI, TEST_PROJECTION, TEST_SELECTION, TEST_SELECTION_ARGS,
                        TEST_ORDER_BY);

        assertThat(result, notNullValue());

        assertThat(provider.query(TEST_URI, TEST_PROJECTION, TEST_SELECTION, TEST_SELECTION_ARGS,
                TEST_ORDER_BY), nullValue());
    }

    @Test
    @SmallTest
    public void insert_result() {
        final MockableContentProvider provider = newProvider();

        provider.addInsertResult(TEST_URI);

        final Uri result = provider.insert(TEST_URI, TEST_CONTENT_VALUES);

        assertThat(result, is(TEST_URI));

        assertThat(provider.insert(TEST_URI, TEST_CONTENT_VALUES), nullValue());
    }

    @Test
    @SmallTest
    public void update_result() {
        final MockableContentProvider provider = newProvider();

        provider.addUpdateResult(1);

        final int result = provider
                .update(TEST_URI, TEST_CONTENT_VALUES, TEST_SELECTION, TEST_SELECTION_ARGS);

        assertThat(result, is(1));

        assertThat(provider
                .update(TEST_URI, TEST_CONTENT_VALUES, TEST_SELECTION, TEST_SELECTION_ARGS), is(0));
    }

    @Test
    @SmallTest
    public void delete_result() {
        final MockableContentProvider provider = newProvider();

        provider.addDeleteResult(1);

        final int result = provider
                .delete(TEST_URI, TEST_SELECTION, TEST_SELECTION_ARGS);

        assertThat(result, is(1));

        assertThat(provider
                .delete(TEST_URI, TEST_SELECTION, TEST_SELECTION_ARGS), is(0));
    }

    @Test
    @SmallTest
    public void call_result() {
        final MockableContentProvider provider = newProvider();

        provider.addCallResult(TEST_BUNDLE);

        final Bundle result = provider.call(TEST_METHOD, TEST_ARG, TEST_BUNDLE);

        assertThat(result, notNullValue());

        assertThat(provider.call(TEST_METHOD, TEST_ARG, TEST_BUNDLE), nullValue());
    }


    @NonNull
    private static MockableContentProvider newProvider() {
        return MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), TEST_AUTHORITY);
    }

}
