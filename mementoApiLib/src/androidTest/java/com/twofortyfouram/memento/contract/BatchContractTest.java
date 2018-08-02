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

package com.twofortyfouram.memento.contract;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.test.provider.MockableContentProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class BatchContractTest {

    private static final String MOCK_CONTENT_PROVIDER_AUTHORITY =
            "com.twofortyfouram.memento.test.provider"; //NON-NLS

    @SmallTest
    @Test
    public void newCallBundle() {
        @NonNull final ArrayList<ArrayList<ContentProviderOperation>> opsGroup
                = new ArrayList<>();

        @NonNull final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newAssertQuery(getContentAuthorityUri()).withExpectedCount(0).build());

        opsGroup.add(ops);

        @NonNull final Bundle bundle = BatchContract.newCallBundle(opsGroup);

        BundleAssertions.assertKeyCount(bundle, 1);
        BundleAssertions.assertHasKey(bundle,
                BatchContract.EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);
    }

    @Test
    @SmallTest
    public void applyBatchWithAlternatives() {
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        @NonNull final ArrayList<ArrayList<ContentProviderOperation>> ops = new ArrayList<>(1);
        {
            @NonNull final ArrayList<ContentProviderOperation> batch = new ArrayList<>();
            batch.add(ContentProviderOperation.newAssertQuery(
                    getContentAuthorityUri())
                    .withExpectedCount(0).build());

            ops.add(batch);
        }

        BatchContract
                .applyBatchWithAlternatives(mockableContentProvider.getContext(),
                        getContentAuthorityUri(),
                        ops);

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BatchContract.METHOD_BATCH_OPERATIONS));
        assertThat(callParams.getArg(), nullValue());

        @NonNull final Bundle callExtras = callParams.getExtras();
        assertThat(callExtras, notNullValue());

        final ArrayList<ArrayList<ContentProviderOperation>> operations;
        try {
            operations = (ArrayList<ArrayList<ContentProviderOperation>>) callExtras
                    .getSerializable(BatchContract.EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException(
                    "Extra is not ArrayList<ArrayList<ContentProviderOperation>>"); //$NON-NLS
        }

        assertThat(operations, is(ops));
    }

    @NonNull
    private MockableContentProvider getMockableContentProvider() {
        return MockableContentProvider.newMockProvider(getContext(),
                MOCK_CONTENT_PROVIDER_AUTHORITY);
    }

    @NonNull
    public static Uri getContentAuthorityUri() {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority
                (MOCK_CONTENT_PROVIDER_AUTHORITY).build();
    }

}
