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

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.context.FeatureContextWrapper;
import com.twofortyfouram.test.util.FirebaseTestLabUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class PermissionCompatTest {

    @SmallTest
    @Test
    public void testNonInstantiable() {
        assertThat(PermissionCompat.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void getPermissionStatus_granted() {
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.M)) {
            final String[] permissionArray = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            };

            final Context testContext = new FeatureContextWrapper(getContext(), permissionArray,
                    permissionArray, null);

            assertThat(PermissionCompat.getPermissionStatus(testContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION),
                    is(PermissionCompat.PermissionStatus.GRANTED));
        }
    }

    @SmallTest
    @Test
    public void getPermissionStatus_not_granted_via_manifest() {
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.M)) {
            final Context testContext = new FeatureContextWrapper(getContext(), null, null, null);

            assertThat(
                    PermissionCompat.getPermissionStatus(testContext,
                            android.Manifest.permission.ACCESS_FINE_LOCATION),
                    is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_MANIFEST));
        }
    }

    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_not_granted_by_user() {

        /*
         * Note: this test is dependent on the Manifest having the permission, but the user not
         * granting the permission to the test app.  These conditions make the test slightly brittle,
         * but it should be relatively reliable in a CI environment.
         */

        // Fails on test lab, probably because all permissions are granted during install time
        if (!FirebaseTestLabUtil.isFirebaseTestLab(InstrumentationRegistry.getContext())) {
            assertThat(PermissionCompat.getPermissionStatus(getContext(),
                    Manifest.permission.CALL_PHONE),
                    is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_USER));
        }
    }

    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_write_settings_granted_with_feature_context_wrapper() {
        final String[] permissionArray = new String[]{
                Manifest.permission.WRITE_SETTINGS
        };

        final Context testContext = new FeatureContextWrapper(getContext(), permissionArray,
                permissionArray, null);

        assertThat(PermissionCompat.getPermissionStatus(testContext,
                Manifest.permission.WRITE_SETTINGS), is(PermissionCompat.PermissionStatus.GRANTED));
    }

    @SmallTest
    @TargetApi(Build.VERSION_CODES.M)
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_request_ignore_battery_optimizations_granted_with_feature_context_wrapper() {
        final String[] permissionArray = new String[]{
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        };

        final Context testContext = new FeatureContextWrapper(getContext(), permissionArray,
                permissionArray, null);

        assertThat(PermissionCompat.getPermissionStatus(testContext,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
                is(PermissionCompat.PermissionStatus.GRANTED));
    }

    @SmallTest
    @TargetApi(Build.VERSION_CODES.M)
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_notification_access_granted_with_feature_context_wrapper() {
        final String[] permissionArray = new String[]{
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        };

        final Context testContext = new FeatureContextWrapper(getContext(), permissionArray,
                permissionArray, null);

        assertThat(PermissionCompat.getPermissionStatus(testContext,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                is(PermissionCompat.PermissionStatus.GRANTED));
    }

    /*
     * Handle the write settings special case.
     */
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_write_settings_not_granted() {
        /*
         * Note: this test is dependent on the Manifest NOT having the permission.
         */

        assertThat(PermissionCompat.getPermissionStatus(getContext(),
                Manifest.permission.WRITE_SETTINGS),
                is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_MANIFEST));
    }

    /*
     * Handle the ignore battery optimizations special case.
     */
    @SmallTest
    @TargetApi(Build.VERSION_CODES.M)
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_request_ignore_battery_optimizations_not_granted() {
        /*
         * Note: this test is dependent on the Manifest NOT having the permission.
         */

        assertThat(
                PermissionCompat.getPermissionStatus(getContext(),
                        Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
                is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_MANIFEST));
    }

    @SmallTest
    @TargetApi(Build.VERSION_CODES.M)
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_notification_access_not_granted() {
        /*
         * Note: this test is dependent on the Manifest NOT having the permission.
         */

        assertThat(
                PermissionCompat.getPermissionStatus(getContext(),
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_MANIFEST));
    }

    @SmallTest
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void getPermissionStatus_not_granted_by_manifest() {

        /*
         * Note: this test is dependent on the Manifest NOT having the permission.
         */

        assertThat(
                PermissionCompat.getPermissionStatus(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION),
                is(PermissionCompat.PermissionStatus.NOT_GRANTED_BY_MANIFEST));
    }

}
