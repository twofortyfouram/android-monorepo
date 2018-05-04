/*
 * android-test
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

package com.twofortyfouram.test.context;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public final class ContentProviderMockContextTest {

    @SmallTest
    @Test
    public void getApplicationContext_does_not_break_out() {
        final ContentProviderMockContext fContext = new ContentProviderMockContext(
                InstrumentationRegistry.getContext(),
                Collections.singletonMap("authority",
                        new ContentProviderImpl()));

        assertThat(fContext.getApplicationContext(),
                Matchers.sameInstance(fContext));
    }

    @SmallTest
    @Test
    public void getContentResolver_query() {
        final ContentProviderMockContext fContext = new ContentProviderMockContext(
                InstrumentationRegistry.getContext(),
                Collections.singletonMap("authority",
                        new ContentProviderImpl()));

        final Cursor result = fContext.getContentResolver()
                .query(new Uri.Builder().authority("authority").build(), null, null, null, null);

        assertThat(result, nullValue());
    }

    private static final class ContentProviderImpl extends ContentProvider {

        @Override
        public boolean onCreate() {
            return false;
        }

        @Nullable
        @Override
        public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
            return null;
        }

        @Nullable
        @Override
        public String getType(Uri uri) {
            return null;
        }

        @Nullable
        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            return null;
        }

        @Override
        public int delete(Uri uri, String s, String[] strings) {
            return 0;
        }

        @Override
        public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
            return 0;
        }
    }
}
