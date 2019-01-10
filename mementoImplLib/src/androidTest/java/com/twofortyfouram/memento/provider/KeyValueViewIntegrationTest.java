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
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import androidx.test.rule.provider.ProviderTestRule;
import com.twofortyfouram.memento.internal.ContentProviderClientCompat;
import com.twofortyfouram.memento.test.main_process.contract.KeyValueContract;
import com.twofortyfouram.memento.test.main_process.contract.LatestKeyValueContractView;
import com.twofortyfouram.memento.test.main_process.contract.TestKeyValueColumns;
import com.twofortyfouram.memento.test.main_process.model.TestKeyValueProviderParser;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderImpl;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/*
 * This test doesn't map directly to a single class but is intended to exercise {@link SqliteView
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
public final class KeyValueViewIntegrationTest {

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
    public void query_view_latest_value() {
        /*
         * Inserts a bunch of rows and ensures that the view returns only the latest one.  This isn't guaranteed to catch
         * a failure because an incorrect view might have inconsistent ordering.  But with the number of iterations and
         * different keys, it should provide good coverage.
         */

        @NonNull final ContentResolver resolver = mProviderRule.getResolver();
        final String baseKey = "key_";
        final String baseVal = "val_";
        final int maxKeysCount = 25;
        final int repeatCount = 3;

        {
            @NonNull final Uri contentUri = KeyValueContract.getContentUri(ApplicationProvider.getApplicationContext());
            for (int j = 0; j < repeatCount; j++) {
                //Write wrong values
                for (int i = 0; i < maxKeysCount; i++) {
                    resolver.insert(contentUri, TestKeyValueProviderParser.newContentValues(baseKey + i, "wrong"));
                }

                //Write correct values in reversed order
                for (int i = maxKeysCount - 1; i >= 0; i--) {
                    resolver.insert(contentUri, TestKeyValueProviderParser.newContentValues(baseKey + i, baseVal + i));
                }

                //Write correct values
                for (int i = 0; i < maxKeysCount; i++) {
                    resolver.insert(contentUri, TestKeyValueProviderParser.newContentValues(baseKey + i, baseVal + i));
                }
            }
        }

        for (int i = 0; i < maxKeysCount; i++) {
            try (@Nullable final Cursor cursor = resolver.query(LatestKeyValueContractView.getContentUri(ApplicationProvider.getApplicationContext()), null, String.format(Locale.US, "%s = ?", TestKeyValueColumns.COLUMN_STRING_KEY), new String[] {baseKey + i}, null)) {
                assertThat(cursor, notNullValue());
                assertThat(cursor.getCount(), is(1));

                cursor.moveToFirst();

                final int valueColumnIndex = cursor.getColumnIndexOrThrow(LatestKeyValueContractView.COLUMN_STRING_VALUE);
                assertThat(cursor.getString(valueColumnIndex), is(baseVal + i));
            }
        }
    }

}
