/*
 * android-spackle
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

package com.twofortyfouram.spackle;

import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public final class AbstractProcessNameContentProviderUnitTest {

    @Test
    @SmallTest
    public void getProcessName_from_application() {
        final String expectedApplicationProcessName = "beep"; //$NON-NLS

        final ContextWrapper ctx = new ContextWrapper(InstrumentationRegistry.getContext()) {
            @Override
            public ApplicationInfo getApplicationInfo() {
                final ApplicationInfo info = new ApplicationInfo();
                info.processName = expectedApplicationProcessName;

                return info;
            }
        };

        final String actualProcessName = AbstractProcessNameContentProvider.getProcessName(
                ctx, new ProviderInfo());

        assertThat(actualProcessName, is(expectedApplicationProcessName));
    }

    @Test
    @SmallTest
    public void getProcessName_from_provider() {
        final String expectedProviderProcessName = "beep"; //$NON-NLS

        final ProviderInfo info = new ProviderInfo();
        info.processName = expectedProviderProcessName;

        final String actualProcessName = AbstractProcessNameContentProvider.getProcessName(
                InstrumentationRegistry.getContext(), info);

        assertThat(actualProcessName, is(expectedProviderProcessName));
    }

    @Test
    @SmallTest
    public void getProcessName_from_package() {

        final String packageName = "beep"; //$NON-NLS

        final ContextWrapper ctx = new ContextWrapper(InstrumentationRegistry.getContext()) {
            @Override
            public ApplicationInfo getApplicationInfo() {
                return new ApplicationInfo();
            }

            @Override
            public String getPackageName() {
                return packageName;
            }
        };

        final String actualProcessName = AbstractProcessNameContentProvider.getProcessName(
                ctx, new ProviderInfo());

        assertThat(actualProcessName, is(packageName));

    }
}
