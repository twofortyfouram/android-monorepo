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

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.spackle.AppBuildInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public final class PluginPackageScannerTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(PluginPackageScanner.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void findActivities_conditions() {
        final Collection<ResolveInfo> infos = PluginPackageScanner.findActivities(
                InstrumentationRegistry.getContext(),
                PluginType.CONDITION, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findActivities_settings() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findActivities(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findActivities_debug_condition() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findActivities(InstrumentationRegistry.getContext(),
                        PluginType.CONDITION,
                        InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }

    @SmallTest
    @Test
    public void findActivities_debug_setting() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findActivities(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }


    @SmallTest
    @Test
    public void findProviders_conditions() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findProviders(InstrumentationRegistry.getContext(),
                        PluginType.CONDITION, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findProviders_settings() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findProviders(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findProviders_debug_condition() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findProviders(InstrumentationRegistry.getContext(),
                        PluginType.CONDITION,
                        InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }

    @SmallTest
    @Test
    public void findProviders_debug_setting() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findProviders(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }

    @SmallTest
    @Test
    public void findReceivers_conditions() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findReceivers(InstrumentationRegistry.getContext(),
                        PluginType.CONDITION, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findReceivers_settings() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findReceivers(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, null);
        assertThat(infos, notNullValue());
        assertThat(infos.size(), greaterThanOrEqualTo(1));
    }

    @SmallTest
    @Test
    public void findReceivers_debug_condition() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findReceivers(InstrumentationRegistry.getContext(),
                        PluginType.CONDITION,
                        InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }

    @SmallTest
    @Test
    public void findReceivers_debug_setting() {
        final Collection<ResolveInfo> infos = PluginPackageScanner
                .findReceivers(InstrumentationRegistry.getContext(),
                        PluginType.SETTING, InstrumentationRegistry.getContext().getPackageName());
        assertThat(infos, notNullValue());
        assertThat(infos.size(), is(1));
    }

    @SmallTest
    @Test
    public void getVersionCode_unknown() {
        final int actualVersionCode = PluginPackageScanner
                .getVersionCode(InstrumentationRegistry.getContext()
                        .getPackageManager(), "com.twofortyfouram.locale.bork"); //$NON-NLS-1$

        assertThat(actualVersionCode, is(-1));
    }

    @SmallTest
    @Test
    public void getVersionCode_known() {
        final int expectedVersionCode = AppBuildInfo
                .getVersionCode(InstrumentationRegistry.getContext());
        final int actualVersionCode = PluginPackageScanner
                .getVersionCode(InstrumentationRegistry.getContext()
                                .getPackageManager(),
                        InstrumentationRegistry.getContext().getPackageName());

        assertThat(actualVersionCode, is(expectedVersionCode));
    }

    @SmallTest
    @Test
    public void isTargetSdkCorrect_true() {
        assertThat(PluginPackageScanner
                .isTargetSdkCorrect(InstrumentationRegistry.getContext(),
                        getResolveInfoWithTargetSdkVersion(InstrumentationRegistry.getContext()
                                .getApplicationInfo().targetSdkVersion)), is(true));
    }

    @SmallTest
    @Test
    public void isTargetSdkCorrect_false() {
        assertThat(PluginPackageScanner
                .isTargetSdkCorrect(InstrumentationRegistry.getContext(),
                        getResolveInfoWithTargetSdkVersion(InstrumentationRegistry.getContext()
                                .getApplicationInfo().targetSdkVersion - 1)), is(false));
    }

    @SmallTest
    @Test
    public void isApplicationEnabled_true() {
        assertThat(PluginPackageScanner
                .isApplicationEnabled(getResolveInfoWithApplicationEnabled(true)), is(true));
    }

    @SmallTest
    @Test
    public void isApplicationEnabled_false() {
        assertThat(PluginPackageScanner
                .isApplicationEnabled(getResolveInfoWithApplicationEnabled(false)), is(false));
    }

    @SmallTest
    @Test
    public void isComponentEnabled_true() {
        assertThat(
                PluginPackageScanner.isComponentEnabled(getResolveInfoWithActivityEnabled(true)),
                is(true));
    }

    @SmallTest
    @Test
    public void isComponentEnabled_false() {
        assertThat(PluginPackageScanner
                .isComponentEnabled(getResolveInfoWithActivityEnabled(false)), is(false));
    }

    @SmallTest
    @Test
    public void isComponentExported_true() {
        assertThat(PluginPackageScanner
                .isComponentExported(getResolveInfoWithActivityExported(true)), is(true));
    }

    @SmallTest
    @Test
    public void isComponentExported_false() {
        assertThat(PluginPackageScanner
                .isComponentExported(getResolveInfoWithActivityExported(false)), is(false));
    }

    @SmallTest
    @Test
    public void isComponentPermissionGranted_true() {
        assertThat(PluginPackageScanner
                .isComponentPermissionGranted(InstrumentationRegistry.getContext(),
                        getResolveInfoWithPermission(null)), is(true));
    }

    @SmallTest
    @Test
    public void isComponentPermissionGranted_false() {
        assertThat(PluginPackageScanner
                .isComponentPermissionGranted(InstrumentationRegistry.getContext(),
                        getResolveInfoWithPermission(
                                "com.nefarious.app.permission.NO_SOUP_FOR_YOU")
                ), is(false)); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void isInstallLocationCorrect_internal() {
        assertThat(
                PluginPackageScanner.isInstallLocationCorrect(InstrumentationRegistry.getContext(),
                        getResolveInfoWithInstallLocation(false)), is(true));
    }

    @SmallTest
    @Test
    public void isInstallLocationCorrect_external() {
        assertThat(
                PluginPackageScanner.isInstallLocationCorrect(InstrumentationRegistry.getContext(),
                        getResolveInfoWithInstallLocation(true)), is(false));
    }

    @NonNull
    private static ResolveInfo getResolveInfoWithTargetSdkVersion(final int targetSdkVersion) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = new ApplicationInfo();
        info.activityInfo.applicationInfo.targetSdkVersion = targetSdkVersion;
        return info;
    }

    @NonNull
    private static ResolveInfo getResolveInfoWithApplicationEnabled(
            final boolean isApplicationEnabled) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = new ApplicationInfo();
        info.activityInfo.applicationInfo.enabled = isApplicationEnabled;
        return info;
    }

    @NonNull
    private static ResolveInfo getResolveInfoWithActivityEnabled(final boolean isActivityEnabled) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.enabled = isActivityEnabled;
        return info;
    }

    @NonNull
    private static ResolveInfo getResolveInfoWithActivityExported(
            final boolean isActivityExported) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.exported = isActivityExported;
        return info;
    }

    @NonNull
    private static ResolveInfo getResolveInfoWithPermission(final String permissionString) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.permission = permissionString;
        return info;
    }

    @NonNull
    private ResolveInfo getResolveInfoWithInstallLocation(final boolean isExternal) {
        final ResolveInfo info = new ResolveInfo();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = new ApplicationInfo();
        info.activityInfo.packageName = InstrumentationRegistry.getContext().getPackageName();
        info.activityInfo.applicationInfo.flags = isExternal ? ApplicationInfo.FLAG_EXTERNAL_STORAGE
                : 0;
        return info;
    }
}
