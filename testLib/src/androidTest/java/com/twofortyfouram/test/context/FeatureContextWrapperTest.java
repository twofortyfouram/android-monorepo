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


import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class FeatureContextWrapperTest {

    @SmallTest
    @Test
    public void breakOut() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(), null, null,
                null);

        assertThat(fContext.getApplicationContext(),
                sameInstance(fContext));
    }

    @SmallTest
    @Test
    public void allowedFeature_null() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(), null, null,
                null);

        assertThat(fContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER), is(false));
    }

    @SmallTest
    @Test
    public void allowedFeature_empty() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(), null, null,
                new String[0]);

        assertThat(fContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER), is(false));
    }

    @SmallTest
    @Test
    public void allowedFeature() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                null, null, new String[]{PackageManager.FEATURE_LIVE_WALLPAPER});

        assertThat(fContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER), is(true));
        assertThat(fContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_LOCATION), is(false));
    }

    @SmallTest
    @Test
    public void allowedPermission_null() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(), null, null,
                null);

        assertThat(fContext.getPackageManager()
                .checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        fContext.getPackageName()), is(PackageManager.PERMISSION_DENIED));
        assertThat(fContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                is(PackageManager.PERMISSION_DENIED));
    }

    @SmallTest
    @Test
    public void allowedPermission_empty() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                null, new String[0], null);

        assertThat(fContext.getPackageManager()
                .checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        fContext.getPackageName()), is(PackageManager.PERMISSION_DENIED));
        assertThat(
                fContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                is(PackageManager.PERMISSION_DENIED));
    }

    @SmallTest
    @Test
    public void allowedPermission() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, null);

        assertThat(fContext.getPackageManager()
                .checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        fContext.getPackageName()), is(PackageManager.PERMISSION_GRANTED));
        assertThat(fContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                is(PackageManager.PERMISSION_GRANTED));

        assertThat(fContext.getPackageManager()
                .checkPermission(Manifest.permission.ACCESS_NETWORK_STATE,
                        fContext.getPackageName()), is(PackageManager.PERMISSION_DENIED));
        assertThat(fContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE),
                is(PackageManager.PERMISSION_DENIED));
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    public void allowedPermission_marshmallow() {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, null);

        assertThat(fContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                is(PackageManager.PERMISSION_GRANTED));

        assertThat(fContext.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE),
                is(PackageManager.PERMISSION_DENIED));
    }

    @SmallTest
    @Test
    public void requestedPermission() throws PackageManager.NameNotFoundException {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, null, null);

        final PackageInfo info = fContext.getPackageManager()
                .getPackageInfo(fContext.getPackageName(), PackageManager.GET_PERMISSIONS);

        assertThat(info.requestedPermissions, notNullValue());
        assertThat(info.requestedPermissions, arrayWithSize(1));
        assertThat(info.requestedPermissions,
                arrayContaining(Manifest.permission.ACCESS_FINE_LOCATION));
    }


    @SmallTest
    public void requestedPermission_null() throws PackageManager.NameNotFoundException {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                null, null, null);

        final PackageInfo info = fContext.getPackageManager()
                .getPackageInfo(fContext.getPackageName(), PackageManager.GET_PERMISSIONS);

        assertThat(info.requestedPermissions, notNullValue());
    }


    @SmallTest
    @Test
    public void requestedPermission_empty() throws PackageManager.NameNotFoundException {
        final FeatureContextWrapper fContext = new FeatureContextWrapper(getContext(),
                new String[]{}, null, null);

        final PackageInfo info = fContext.getPackageManager()
                .getPackageInfo(fContext.getPackageName(), PackageManager.GET_PERMISSIONS);

        assertThat(info.requestedPermissions, notNullValue());
        assertThat(info.requestedPermissions, emptyArray());
    }

}
