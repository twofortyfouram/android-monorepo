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


import android.app.SearchManager;
import android.content.ContentResolver;
import android.net.Uri;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.contract.MementoContract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class QueryStringUtilTest {

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(QueryStringUtil.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void isSuppressNotification_true() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT).authority("com.authority")
                .appendPath("bork").appendQueryParameter(MementoContract
                        .QUERY_STRING_IS_SUPPRESS_NOTIFICATION, Boolean.TRUE.toString()).build();

        assertThat(QueryStringUtil.isSuppressNotification(uri), is(true));
    }

    @Test
    @SmallTest
    public void isSuppressNotification_false() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT).authority("com.authority")
                .appendPath("bork").appendQueryParameter(MementoContract
                        .QUERY_STRING_IS_SUPPRESS_NOTIFICATION, Boolean.FALSE.toString()).build();

        assertThat(QueryStringUtil.isSuppressNotification(uri), is(false));
    }

    @Test
    @SmallTest
    public void getLimit_missing() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority("com.authority") //$NON-NLS
                .appendPath("bork") //$NON-NLS
                .build();

        assertThat(QueryStringUtil.getLimit(uri), nullValue());
    }

    @Test
    @SmallTest
    public void getLimit_zero() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority("com.authority") //$NON-NLS
                .appendPath("bork") //$NON-NLS
                .appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, "0") //$NON-NLS
                .build();

        assertThat(QueryStringUtil.getLimit(uri), nullValue());
    }

    @Test
    @SmallTest
    public void getLimit_one() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority("com.authority")//$NON-NLS
                .appendPath("bork")//$NON-NLS
                .appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, "1") //$NON-NLS
                .build();

        assertThat(QueryStringUtil.getLimit(uri), is("1")); //$NON-NLS
    }

    @Test
    @SmallTest
    public void getLimit_non_integer() {
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority("com.authority")//$NON-NLS
                .appendPath("bork") //$NON-NLS
                .appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, "foo") //$NON-NLS
                .build();

        assertThat(QueryStringUtil.getLimit(uri), nullValue());
    }

}
