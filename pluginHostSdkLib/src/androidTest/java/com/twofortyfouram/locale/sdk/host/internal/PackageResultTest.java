/*
 * android-plugin-host-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.internal;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class PackageResultTest {

    @Test
    @SmallTest
    public void get_conditions() {
        assertThat(PackageResult.get(true, false), is(PackageResult.CONDITIONS_CHANGED));
    }

    @Test
    @SmallTest
    public void get_settings() {
        assertThat(PackageResult.get(false, true), is(PackageResult.SETTINGS_CHANGED));
    }

    @Test
    @SmallTest
    public void get_conditions_and_settings() {
        assertThat(PackageResult.get(true, true),
                is(PackageResult.CONDITIONS_AND_SETTINGS_CHANGED));
    }

    @Test
    @SmallTest
    public void get_nothing() {
        assertThat(PackageResult.get(false, false), is(PackageResult.NOTHING_CHANGED));
    }
}
