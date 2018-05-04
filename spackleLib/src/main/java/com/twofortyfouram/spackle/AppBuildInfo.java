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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Determines information about the build of the app that is
 * running.
 */
@ThreadSafe
public final class AppBuildInfo {

    /**
     * Determines whether the application running is debuggable.  This is determined from the
     * application info object, as an alternative to {@code BuildInfo} which is useless for
     * libraries.
     *
     * @param context Application context.
     * @return True if the application is debuggable.
     */
    public static boolean isDebuggable(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS
        final PackageInfo packageInfo = getMyPackageInfo(context, 0);

        // Normally shouldn't be null, but could be under test.
        @Nullable final ApplicationInfo applicationInfo = packageInfo.applicationInfo;

        if (null != applicationInfo) {
            final boolean isDebuggable = (0 != (packageInfo.applicationInfo.flags
                    & ApplicationInfo.FLAG_DEBUGGABLE));

            return isDebuggable;
        }

        return false;
    }

    /**
     * Gets the "versionCode" in the AndroidManifest.
     *
     * @param context Application context.
     * @return versionCode of the app.
     * @see android.content.pm.PackageInfo#versionCode
     */
    public static int getVersionCode(@NonNull final Context context) {
        final int versionCode = getMyPackageInfo(context, 0).versionCode;

        Lumberjack.v("versionCode=%d", versionCode); //$NON-NLS-1$

        return versionCode;
    }

    /**
     * Gets the "versionName" in the AndroidManifest.
     *
     * @param context Application context.
     * @return versionName of the app.
     * @see android.content.pm.PackageInfo#versionName
     */
    @NonNull
    public static String getVersionName(@NonNull final Context context) {
        @Nullable String versionName = getMyPackageInfo(context, 0).versionName;

        if (null == versionName) {
            versionName = "";  //$NON-NLS-1$
        }

        Lumberjack.v("versionName=%s", versionName); //$NON-NLS-1$

        return versionName;
    }

    /**
     * Gets the name of the application or the package name if the application has no name.
     *
     * @param context Application context.
     * @return Label of the application from the Android Manifest or the package name if no label
     * was set.
     */
    @NonNull
    public static String getApplicationName(@NonNull final Context context) {
        assertNotNull(context, "context");

        final ApplicationInfo info = context.getApplicationInfo();

        CharSequence name = context.getPackageManager().getApplicationLabel(info);

        if (null == name) {
            name = context.getPackageName();
        }

        final String nameString = name.toString();

        return nameString;
    }

    /**
     * Note: this method is known to throw RuntimeException on some Android devices when the
     * Android Package Manager dies.  There's nothing we can do about that error.
     *
     * @param context Application context.
     * @param flags   Flags to pass to the package manager.
     * @return PackageInfo for the current package.
     */
    @NonNull
    /*package*/ static PackageInfo getMyPackageInfo(@NonNull final Context context,
            final int flags) {
        final PackageManager packageManager = context.getPackageManager();
        final String packageName = context.getPackageName();

        try {
            return packageManager.getPackageInfo(packageName, flags);
        } catch (final NameNotFoundException e) {
            // The app's own package must exist, so this should never occur.
            throw new AssertionError(e);
        }
    }

    /**
     * Gets the time in epoch milliseconds when the app was last updated.
     *
     * @param context Application context.
     * @return long representing the Epoch timestamp in milliseconds when the
     * app was last updated.
     */
    public static long getLastUpdateWallTimeMillis(@NonNull final Context context) {
        final long lastUpdateTimeMillis = getMyPackageInfo(context, 0).lastUpdateTime;

        Lumberjack.v("Last update time was %d [milliseconds]", lastUpdateTimeMillis); //$NON-NLS-1$

        return lastUpdateTimeMillis;
    }

    /**
     * Gets the time in epoch milliseconds when the app was installed.
     *
     * @param context Application context.
     * @return long representing the Epoch timestamp in milliseconds when the
     * app was installed.
     */
    public static long getInstallWallTimeMillis(@NonNull final Context context) {
        final long installTimeMillis = getMyPackageInfo(context, 0).firstInstallTime;

        Lumberjack.v("Install time was %d [milliseconds]", installTimeMillis); //$NON-NLS-1$

        return installTimeMillis;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be
     *                                       instantiated.
     */
    private AppBuildInfo() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
