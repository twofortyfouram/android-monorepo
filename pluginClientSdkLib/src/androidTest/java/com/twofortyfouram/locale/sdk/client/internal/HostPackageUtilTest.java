/*
 * android-plugin-client-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.client.internal;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.test.filters.SmallTest;

import net.jcip.annotations.ThreadSafe;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public final class HostPackageUtilTest {

    @SmallTest
    @Test
    public void testNonInstantiable() {
        assertThat(HostPackageUtil.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void getCompatiblePackage_hinted_positive() {
        assertThat(HostPackageUtil.getCompatiblePackage( //$NON-NLS-1$
                new HostPackageManager("com.twofortyfouram.locale"),
                "com.twofortyfouram.locale"),
                is("com.twofortyfouram.locale")); //$NON-NLS-1$//$NON-NLS-2$
    }

    @SmallTest
    @Test
    public void getCompatiblePackage_hinted_negative() {
        assertThat(
                HostPackageUtil.getCompatiblePackage( //$NON-NLS-1$
                        new HostPackageManager("com.twofortyfouram.locale"),
                        "com.foo.bar"), is("com.twofortyfouram.locale")); //$NON-NLS-1$//$NON-NLS-2$
    }

    @SmallTest
    @Test
    public void testGetCompatiblePackage_none() {
        assertThat(HostPackageUtil.getCompatiblePackage(new HostPackageManager(), null),
                nullValue());
    }

    @SuppressWarnings("deprecation")
    @ThreadSafe
    public static class HostPackageManager extends android.test.mock.MockPackageManager {

        @NonNull
        private final List<PackageInfo> mInstalledPackages = new LinkedList<>();

        public HostPackageManager(@NonNull final String... packages) {
            for (String pkg : packages) {
                final PackageInfo pi = new PackageInfo();
                pi.packageName = pkg;
                mInstalledPackages.add(pi);
            }
        }

        @Override
        public List<PackageInfo> getInstalledPackages(int flags) {
            return mInstalledPackages;
        }
    }
}
