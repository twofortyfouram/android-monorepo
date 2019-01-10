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

package com.twofortyfouram.memento.cleanup;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import androidx.test.rule.provider.ProviderTestRule;
import com.twofortyfouram.memento.contract.BaseColumnsContract;
import com.twofortyfouram.memento.test.main_process.contract.KeyValueContract;
import com.twofortyfouram.memento.test.main_process.contract.LatestKeyValueContractView;
import com.twofortyfouram.memento.test.main_process.contract.TestKeyValueColumns;
import com.twofortyfouram.memento.test.main_process.model.TestKeyValueProviderParser;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderImpl;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderUtil;
import com.twofortyfouram.test.context.MyMockContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests {@link CleanupUtil} using the table implementation of {@link KeyValueContract} and {@link LatestKeyValueContractView}.
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
public final class KeyValueCleanupIntegrationTest {

    @Rule
    public ProviderTestRule mProviderRule = newProviderTestRule();

    @Test
    @SmallTest
    public void deleteOldValuesForKey_delete_multiple_entries() throws RemoteException, OperationApplicationException {
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()),
                TestKeyValueProviderParser.newContentValues("foo", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("foo", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("foo", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("baz", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("bat", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("bat", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("bat", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("bop", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("bop", "bar"));

        @NonNull final ArrayList<ContentProviderOperation> ops = CleanupUtil.getDeleteOps(new ResolverMockContext(mProviderRule.getResolver()), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), LatestKeyValueContractView.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueColumns.COLUMN_STRING_KEY, LatestKeyValueContractView._ID, 2);
        assertThat(ops, not(empty()));

        mProviderRule.getResolver().applyBatch(ContentProviderUtil.getContentAuthorityString
                (ApplicationProvider.getApplicationContext()), ops);

        @NonNull final String selection = String.format(Locale.US, "%s = ?", KeyValueContract.COLUMN_STRING_KEY); //$NON-NLS
        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), selection, new String[]{"foo"}), is(2));
        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), selection, new String[]{"baz"}), is(1));
        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), selection, new String[]{"bat"}), is(2));
        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), selection, new String[]{"bop"}), is(2));
    }

    @Test
    @SmallTest
    public void deleteOldValuesForKey_delete_one_entry() throws RemoteException, OperationApplicationException {
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("foo", "bar"));
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("foo", "bar"));

        @NonNull final ArrayList<ContentProviderOperation> ops = CleanupUtil.getDeleteOps(new ResolverMockContext(mProviderRule.getResolver()), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), LatestKeyValueContractView.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueColumns.COLUMN_STRING_KEY, LatestKeyValueContractView._ID, 1);
        assertThat(ops, not(empty()));

        mProviderRule.getResolver().applyBatch(ContentProviderUtil.getContentAuthorityString(ApplicationProvider.getApplicationContext()), ops);

        assertThat(BaseColumnsContract.getCountForUri(mProviderRule.getResolver(), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext())), is(1));
    }

    @Test
    @SmallTest
    public void deleteOldValuesForKey_dont_delete_empty() {
        mProviderRule.getResolver().insert(KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueProviderParser.newContentValues("foo", "bar"));

        @NonNull final ArrayList<ContentProviderOperation> ops = CleanupUtil.getDeleteOps(new ResolverMockContext(mProviderRule.getResolver()), KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext()), LatestKeyValueContractView.getContentUri(ApplicationProvider.getApplicationContext()), TestKeyValueColumns.COLUMN_STRING_KEY, LatestKeyValueContractView._ID, 1);
        assertThat(ops, empty());
    }

    @NonNull
    private ProviderTestRule newProviderTestRule() {
        @NonNull final String prefix = UUID.randomUUID().toString();

        return new ProviderTestRule.Builder(ContentProviderImpl.class,
                ContentProviderUtil.getContentAuthorityString(
                        ApplicationProvider.getApplicationContext())).setPrefix(prefix).build();
    }

    private static class ResolverMockContext extends MyMockContext {
        private final ContentResolver mResolver;

        public ResolverMockContext(@NonNull final ContentResolver resolver) {
            assertNotNull(resolver, "resolver"); //$NON-NLS

            mResolver = resolver;
        }

        @Override
        public Context getApplicationContext() {
            return this;
        }

        @Override
        public ContentResolver getContentResolver() {
            return mResolver;
        }
    }

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(CleanupUtil.class, notInstantiable());
    }
}
