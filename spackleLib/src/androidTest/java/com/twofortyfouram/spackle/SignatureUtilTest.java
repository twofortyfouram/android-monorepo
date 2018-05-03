/*
 * android-spackle https://github.com/twofortyfouram/android-spackle
 * Copyright (C) 2009–2017 two forty four a.m. LLC
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

package com.twofortyfouram.spackle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;


@RunWith(AndroidJUnit4.class)
public final class SignatureUtilTest {

    @Test
    @SmallTest
    public void getSignatures_app_new_instance() {
        final String[] sig1 = SignatureUtil.getApplicationSignatures(getContext());
        final String[] sig2 = SignatureUtil.getApplicationSignatures(getContext());
        assertThat(sig1, not(sameInstance(sig2)));
    }

    @Test
    @SmallTest
    public void getSignatures_package_new_instance() throws NameNotFoundException {
        final String[] sig1 =
                SignatureUtil.getApplicationSignatures(getContext(), getContext().getPackageName());
        final String[] sig2 =
                SignatureUtil.getApplicationSignatures(getContext(), getContext().getPackageName());

        assertThat(sig1, not(sameInstance(sig2)));
    }

    @Test
    @SmallTest
    @SuppressWarnings("unchecked")
    public void getSignatures_app_not_null() {
        final String[] signatures = SignatureUtil.getApplicationSignatures(getContext());

        assertThat(signatures, notNullValue());
        assertThat(signatures, not(arrayContaining(nullValue())));
    }

    @Test
    @SmallTest
    @SuppressWarnings("unchecked")
    public void getSignatures_package_not_null() throws NameNotFoundException {
        final String[] signatures =
                SignatureUtil.getApplicationSignatures(getContext(), getContext().getPackageName());

        assertThat(signatures, notNullValue());
        assertThat(signatures, not(arrayContaining(nullValue())));
    }

    @Test
    @SmallTest
    @SuppressWarnings("unchecked")
    public void getSignatures_app_not_empty() {
        final String[] signatures = SignatureUtil.getApplicationSignatures(getContext());

        assertThat(signatures, not(emptyArray()));
        assertThat(signatures, not(arrayContaining(isEmptyString())));
    }

    @Test
    @SmallTest
    @SuppressWarnings("unchecked")
    public void getSignatures_package_not_empty() throws NameNotFoundException {
        final String[] signatures =
                SignatureUtil.getApplicationSignatures(getContext(), getContext().getPackageName());

        assertThat(signatures, not(emptyArray()));
        assertThat(signatures, not(arrayContaining(isEmptyString())));
    }

    @Test
    @SmallTest
    @SuppressWarnings("unchecked")
    public void getSignatures_null_array() throws NameNotFoundException {
        final Context mockContext = new MockContext() {
            @Override
            public String getPackageName() {
                return InstrumentationRegistry.getContext().getPackageName();
            }

            @Override
            public Context getApplicationContext() {
                return this;
            }

            @Override
            public PackageManager getPackageManager() {
                return new MockPackageManager() {
                    @Override
                    public PackageInfo getPackageInfo(final String packageName, final int flags)
                            throws NameNotFoundException {
                        return new PackageInfo();
                    }
                };
            }
        };

        final String[] signatures =
                SignatureUtil.getApplicationSignatures(mockContext,
                        InstrumentationRegistry.getContext().getPackageName());

        assertThat(signatures, emptyArray());
    }
}