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

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.provider.MockableContentProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class BackupContractTest {

    private static final String MOCK_CONTENT_PROVIDER_AUTHORITY =
            "com.twofortyfouram.memento.test.provider"; //NON-NLS

    @Test
    @SmallTest
    public void backup_true() {
        @NonNull final String filePath = "foo"; //NON-NLS
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        {
            final Bundle result = new Bundle();
            result.putBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, true);
            mockableContentProvider.addCallResult(result);
        }

        assertThat(BackupContract
                        .backup(mockableContentProvider.getContext(), getContentAuthorityUri(), filePath),
                is(true));

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BackupContract.METHOD_BACKUP));
        assertThat(callParams.getArg(), is(filePath));
        assertThat(callParams.getExtras(), nullValue());
    }

    @Test
    @SmallTest
    public void backup_false() {
        @NonNull final String filePath = "foo"; //NON-NLS
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        {
            final Bundle result = new Bundle();
            result.putBoolean(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false);
            mockableContentProvider.addCallResult(result);
        }

        assertThat(BackupContract
                        .backup(mockableContentProvider.getContext(), getContentAuthorityUri(), filePath),
                is(false));

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BackupContract.METHOD_BACKUP));
        assertThat(callParams.getArg(), is(filePath));
        assertThat(callParams.getExtras(), nullValue());
    }

    @Test
    @SmallTest
    public void backup_incompatible_provider_null_bundle() {
        @NonNull final String filePath = "foo"; //NON-NLS
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        assertThat(BackupContract
                        .backup(mockableContentProvider.getContext(), getContentAuthorityUri(), filePath),
                is(false));

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BackupContract.METHOD_BACKUP));
        assertThat(callParams.getArg(), is(filePath));
        assertThat(callParams.getExtras(), nullValue());
    }

    @Test
    @SmallTest
    public void backup_incompatible_provider_empty_bundle() {
        @NonNull final String filePath = "foo"; //NON-NLS
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        {
            final Bundle result = new Bundle();
            mockableContentProvider.addCallResult(result);
        }

        assertThat(BackupContract
                        .backup(mockableContentProvider.getContext(), getContentAuthorityUri(), filePath),
                is(false));

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BackupContract.METHOD_BACKUP));
        assertThat(callParams.getArg(), is(filePath));
        assertThat(callParams.getExtras(), nullValue());
    }

    @Test
    @SmallTest
    public void backup_incompatible_provider_wrong_type_in_bundle() {
        @NonNull final String filePath = "foo"; //NON-NLS
        @NonNull final MockableContentProvider mockableContentProvider = getMockableContentProvider();

        {
            final Bundle result = new Bundle();
            result.putString(BackupContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, "foo"); //$NON-NLS
            mockableContentProvider.addCallResult(result);
        }

        assertThat(BackupContract
                        .backup(mockableContentProvider.getContext(), getContentAuthorityUri(), filePath),
                is(false));

        assertThat(mockableContentProvider.getCallCount(), is(1));
        assertThat(mockableContentProvider.getQueryCount(), is(0));
        assertThat(mockableContentProvider.getInsertCount(), is(0));
        assertThat(mockableContentProvider.getUpdateCount(), is(0));
        assertThat(mockableContentProvider.getDeleteCount(), is(0));

        @Nullable final MockableContentProvider.CallParams callParams = mockableContentProvider
                .getCallParams();
        assertThat(callParams, notNullValue());
        assertThat(callParams.getMethod(), is(BackupContract.METHOD_BACKUP));
        assertThat(callParams.getArg(), is(filePath));
        assertThat(callParams.getExtras(), nullValue());
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
