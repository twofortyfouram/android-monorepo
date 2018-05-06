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

import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class InstallLocationTest {

    @SmallTest
    @Test
    public void getInstallLocation_auto() {
        assertThat(InstallLocation
                        .getInstallLocation(InstallLocation.MANIFEST_INSTALL_LOCATION_AUTO),
                is(InstallLocation.auto));
    }

    @SmallTest
    @Test
    public void getInstallLocation_internal() {
        assertThat(InstallLocation
                .getInstallLocation(
                        InstallLocation.MANIFEST_INSTALL_LOCATION_INTERNAL_ONLY), is(
                InstallLocation.internalOnly
        ));
    }

    @SmallTest
    @Test
    public void getInstallLocation_external() {
        assertThat(InstallLocation
                        .getInstallLocation(
                                InstallLocation.MANIFEST_INSTALL_LOCATION_PREFER_EXTERNAL),
                is(InstallLocation.preferExternal));
    }

    @SmallTest
    @Test
    public void getInstallLocation_unknown() {
        assertThat(InstallLocation.getInstallLocation(Integer.MIN_VALUE),
                is(InstallLocation.UNKNOWN));
    }

    @SmallTest
    @Test
    public void testGetManifestInstallLocation() throws IOException, XmlPullParserException,
            PackageManager.NameNotFoundException {
        final InstallLocation thisTestApkInstallLocation = InstallLocation
                .getManifestInstallLocation(
                        InstrumentationRegistry.getContext(),
                        InstrumentationRegistry.getContext().getPackageName());

        assertThat(thisTestApkInstallLocation,
                is(InstallLocation.internalOnly));
    }
}
