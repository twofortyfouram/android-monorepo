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

package com.twofortyfouram.memento.internal;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.filters.SmallTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class ImmutableUriMatcherTest {

    private static final String AUTHORITY = "com.twofortyfouram.locale.core.test.provider";
    //$NON-NLS-1$

    private static final int MATCH_TABLE_A = 1;

    private static final int MATCH_TABLE_A_ID = 2;

    private static final int MATCH_TABLE_B = 3;

    private static final int MATCH_TABLE_B_ID = 4;

    @NonNull
    private static UriMatcher getUriMatcher() {
        final UriMatcher result = new UriMatcher(UriMatcher.NO_MATCH);

        result.addURI(AUTHORITY, "table_a", MATCH_TABLE_A); //$NON-NLS-1$
        result.addURI(AUTHORITY, "table_a/#", MATCH_TABLE_A_ID); //$NON-NLS-1$
        result.addURI(AUTHORITY, "table_b", MATCH_TABLE_B); //$NON-NLS-1$
        result.addURI(AUTHORITY, "table_b/#", MATCH_TABLE_B_ID); //$NON-NLS-1$

        return result;
    }

    @NonNull
    public static Uri buildUri(@NonNull final String tableName, @Nullable final Long id) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(ContentResolver.SCHEME_CONTENT);
        builder.authority(AUTHORITY);
        builder.path(tableName);

        Uri contentUri = builder.build();
        if (null != id) {
            contentUri = ContentUris.withAppendedId(contentUri, id);
        }

        return contentUri;

    }

    @SmallTest
    @Test
    public void match_table_a() {
        final UriMatcher matcher = getUriMatcher();
        final ImmutableUriMatcher immutableMatcher = new ImmutableUriMatcher(matcher);

        final Uri uri = buildUri("table_a", null); //$NON-NLS-1$

        assertThat(immutableMatcher.match(uri), is(matcher.match(uri)));
    }

    @SmallTest
    @Test
    public void match_table_a_id() {
        final UriMatcher matcher = getUriMatcher();
        final ImmutableUriMatcher immutableMatcher = new ImmutableUriMatcher(matcher);

        final Uri uri = buildUri("table_a", 1234L); //$NON-NLS-1$

        assertThat(immutableMatcher.match(uri), is(matcher.match(uri)));
    }

    @SmallTest
    @Test
    public void match_table_b() {
        final UriMatcher matcher = getUriMatcher();
        final ImmutableUriMatcher immutableMatcher = new ImmutableUriMatcher(matcher);

        final Uri uri = buildUri("table_b", null); //$NON-NLS-1$

        assertThat(immutableMatcher.match(uri), is(matcher.match(uri)));
    }

    @SmallTest
    @Test
    public void match_table_b_id() {
        final UriMatcher matcher = getUriMatcher();
        final ImmutableUriMatcher immutableMatcher = new ImmutableUriMatcher(matcher);

        final Uri uri = buildUri("table_b", 4321L); //$NON-NLS-1$

        assertThat(immutableMatcher.match(uri), is(matcher.match(uri)));
    }
}
