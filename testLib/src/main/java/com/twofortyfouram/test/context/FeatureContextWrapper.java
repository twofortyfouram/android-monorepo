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


import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.mock.MockPackageManager;

import net.jcip.annotations.Immutable;

import java.util.Arrays;

/**
 * Context for faking environment capabilities and permissions. This allows dynamic feature
 * scaling to be exercised in a test environment.
 *
 * Note: This class returns a mock {@code PackageManager} via {@link Context#getPackageManager()},
 * which may not fully implement the PackageManager APIs.
 */
@Immutable
public final class FeatureContextWrapper extends ContextWrapper {

    /**
     * Sorted set of permissions this application is allowed to access.
     */
    @Nullable
    private final String[] mAllowedPermissions;

    /**
     * Sorted set of system features that are available.
     */
    @Nullable
    private final String[] mAvailableFeatures;

    /**
     * Mock package manager for faking permission results.
     */
    @NonNull
    private final PackageManager mMockPackageManager;

    /**
     * Note: There is no checking that {@code requestedPermissions} and {@code allowedPermissions}
     * have sane values.  It is possible to construct an instance of this class that indicates
     * permission is allowed even if not requested.
     *
     * @param baseContext          Base context to wrap.
     * @param requestedPermissions Set of permissions requested (e.g. in the Android Manifest).
     * @param allowedPermissions   Set of allowed permissions.
     * @param availableFeatures    Set of available features.
     */
    public FeatureContextWrapper(@NonNull final Context baseContext,
            @Nullable final String[] requestedPermissions,
            @Nullable final String[] allowedPermissions,
            @Nullable final String[] availableFeatures) {
        super(baseContext);
        if (null != allowedPermissions) {
            mAllowedPermissions = copyArray(allowedPermissions);
            Arrays.sort(mAllowedPermissions);
        } else {
            mAllowedPermissions = null;
        }

        if (null != availableFeatures) {
            mAvailableFeatures = copyArray(availableFeatures);
            Arrays.sort(mAvailableFeatures);
        } else {
            mAvailableFeatures = null;
        }

        final String[] requestedPermissionsCopy;
        if (null != requestedPermissions) {
            requestedPermissionsCopy = copyArray(requestedPermissions);
            Arrays.sort(requestedPermissionsCopy);
        } else {
            requestedPermissionsCopy = null;
        }

        mMockPackageManager = new MyMockPackageManager(baseContext, requestedPermissionsCopy);
    }

    @Override
    public PackageManager getPackageManager() {
        return mMockPackageManager;
    }

    @Override
    public int checkCallingOrSelfPermission(final String permission) {
        return checkPermissionInternal(permission);
    }

    @Override
    public int checkSelfPermission(final String permission) {
        return checkPermissionInternal(permission);
    }

    private int checkPermissionInternal(final String permission) {
        boolean isPermissionGranted;
        if (null == mAllowedPermissions) {
            isPermissionGranted = false;
        } else {
            isPermissionGranted = 0 <= Arrays.binarySearch(mAllowedPermissions, permission);
        }

        return isPermissionGranted ? PackageManager.PERMISSION_GRANTED
                : PackageManager.PERMISSION_DENIED;
    }

    private boolean checkFeatureInternal(@NonNull final String feature) {
        boolean isFeatureAvailable;
        if (null == mAvailableFeatures) {
            isFeatureAvailable = false;
        } else {
            if (0 <= Arrays.binarySearch(mAvailableFeatures, feature)) {
                isFeatureAvailable = true;
            } else {
                isFeatureAvailable = false;
            }
        }

        return isFeatureAvailable;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @NonNull
    private static String[] copyArray(@NonNull final String[] toCopy) {
        final String[] dest = new String[toCopy.length];

        System.arraycopy(toCopy, 0, dest, 0, toCopy.length);

        return dest;
    }


    @SuppressWarnings("deprecation")
    private class MyMockPackageManager extends MockPackageManager {

        private final Context mBaseContext;

        private final String[] mRequestedPermissionsCopy;

        public MyMockPackageManager(final Context baseContext,
                final String[] requestedPermissionsCopy) {
            mBaseContext = baseContext;
            mRequestedPermissionsCopy = requestedPermissionsCopy;
        }

        @Override
        public int checkPermission(final String permName, final String pkgName) {
            return checkPermissionInternal(permName);
        }

        @Override
        public boolean hasSystemFeature(final String name) {
            return checkFeatureInternal(name);
        }

        @Override
        public void setComponentEnabledSetting(final ComponentName componentName,
                final int newState,
                final int flags) {
            mBaseContext.getPackageManager().setComponentEnabledSetting(componentName, newState,
                    flags);
        }

        @Override
        public int getComponentEnabledSetting(final ComponentName componentName) {
            return mBaseContext.getPackageManager().getComponentEnabledSetting(componentName);
        }

        @Override
        public PackageInfo getPackageInfo(final String packageName, final int flags)
                throws NameNotFoundException {
            final PackageInfo packageInfo = new PackageInfo();

            if (null != mRequestedPermissionsCopy) {
                packageInfo.requestedPermissions = copyArray(mRequestedPermissionsCopy);
            }

            return packageInfo;
        }

        @Override
        public ApplicationInfo getApplicationInfo(final String packageName, final int flags)
                throws NameNotFoundException {
            return mBaseContext.getPackageManager().getApplicationInfo(packageName, flags);
        }
    }
}
