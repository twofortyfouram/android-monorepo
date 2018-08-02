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
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.provider.MockableContentProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class MementoProviderUtilTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(MementoProviderUtil.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void mockableClassName() {
        assertThat(MockableContentProvider.class.getName(),
                is(MementoProviderUtil.MOCKABLE_CONTENT_PROVIDER_CLASS_NAME));
    }

    @Test
    @SmallTest
    public void getCountForUri_zero() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._COUNT}, 1);
        cursor.addRow(new Object[]{0});
        mockableContentProvider.addQueryResult(cursor);

        assertThat(MementoProviderUtil
                        .getCountForUri(mockableContentProvider.getContext().getContentResolver(), uri),
                is(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    @SmallTest
    public void getCountForUri_multiple_rows() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._COUNT}, 2);
        cursor.addRow(new Object[]{0});
        cursor.addRow(new Object[]{0});
        mockableContentProvider.addQueryResult(cursor);

        MementoProviderUtil
                .getCountForUri(mockableContentProvider.getContext().getContentResolver(),
                        uri);//throws
    }

    @Test(expected = NullPointerException.class)
    @SmallTest
    public void getCountForUri_null_cursor() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        mockableContentProvider.addQueryResult(null);

        MementoProviderUtil
                .getCountForUri(mockableContentProvider.getContext().getContentResolver(),
                        uri);//throws
    }

    @Test(expected = UnsupportedOperationException.class)
    @SmallTest
    public void getCountForUri_missing_count_column() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        try (final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID}, 1)) {
            cursor.addRow(new Object[]{0});
            mockableContentProvider.addQueryResult(cursor);

            MementoProviderUtil
                    .getCountForUri(mockableContentProvider.getContext().getContentResolver(),
                            uri); //throws
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    @SmallTest
    public void getCountForUri_type_mismatch_string() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        try (final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._COUNT}, 1)) {
            cursor.addRow(new Object[]{"foo"}); //$NON-NLS
            mockableContentProvider.addQueryResult(cursor);

            MementoProviderUtil
                    .getCountForUri(mockableContentProvider.getContext().getContentResolver(),
                            uri);//throws
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    @SmallTest
    public void getCountForUri_type_mismatch_null() {
        final String authority = "testauthority"; //$NON-NLS
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority).path("test").build(); //$NON-NLS
        final MockableContentProvider mockableContentProvider = MockableContentProvider
                .newMockProvider(InstrumentationRegistry.getContext(), authority);

        try (final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._COUNT}, 1)) {
            cursor.addRow(new Object[]{null});
            mockableContentProvider.addQueryResult(cursor);

            MementoProviderUtil
                    .getCountForUri(mockableContentProvider.getContext().getContentResolver(),
                            uri);//throws
        }
    }

}
