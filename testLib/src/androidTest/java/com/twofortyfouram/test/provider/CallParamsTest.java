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

package com.twofortyfouram.test.provider;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class CallParamsTest {

    @Test
    @SmallTest
    public void getMethod() {
        @NonNull final String method = "method"; //$NON-NLS
        @NonNull final String arg = "arg"; //$NON-NLS
        @NonNull final Bundle bundle = Bundle.EMPTY;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getMethod(), is(method));
    }

    @Test
    @SmallTest
    public void getMethod_null() {
        @NonNull final String method = null;
        @NonNull final String arg = "arg"; //$NON-NLS
        @NonNull final Bundle bundle = Bundle.EMPTY;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getMethod(), nullValue());
    }

    @Test
    @SmallTest
    public void getArg() {
        @NonNull final String method = "method"; //$NON-NLS
        @NonNull final String arg = "arg"; //$NON-NLS
        @NonNull final Bundle bundle = Bundle.EMPTY;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getArg(), is(arg));
    }

    @Test
    @SmallTest
    public void getArg_null() {
        @NonNull final String method = "method"; //$NON-NLS
        @NonNull final String arg = null;
        @NonNull final Bundle bundle = Bundle.EMPTY;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getArg(), nullValue());
    }

    @Test
    @SmallTest
    public void getExtras_shallow_copy() {
        @NonNull final String method = "method"; //$NON-NLS
        @NonNull final String arg = "arg"; //$NON-NLS
        @NonNull final Bundle bundle = Bundle.EMPTY;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getExtras().size(), is(0));
        assertThat(params.getExtras(), not(sameInstance(bundle)));
    }

    @Test
    @SmallTest
    public void getExtras_null() {
        @NonNull final String method = "method"; //$NON-NLS
        @NonNull final String arg = "arg"; //$NON-NLS
        @Nullable final Bundle bundle = null;

        @NonNull final MockableContentProvider.CallParams params
                = new MockableContentProvider.CallParams(method, arg, bundle);

        assertThat(params.getExtras(), nullValue());
    }

}
