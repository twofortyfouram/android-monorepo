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
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.internal.Constants;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
public final class PermissionCompat {

    /*
     * Since test contexts are usually intended to be available in unobfuscated
     * debug builds, this relies on the class name.  If the debug release is obfuscated, this
     * will fail.  Relying on a direct reference to the class itself is not possible, because
     * then spackleLib would need an explicit dependency on the test library.
     */
    @NonNull
    private static final String FEATURE_CONTEXT_WRAPPER_CLASS_NAME
            = "com.twofortyfouram.test.context.FeatureContextWrapper"; //$NON-NLS-1$

    /**
     * Dynamically checks app permissions at runtime, with forward and backward
     * compatibility for pre-Marshmallow and post-Mashmallow permission behavior.
     * <p>
     * The behavior is different from {@code android.support.v4.content.PermissionChecker}.  This
     * method lets SDK and app developers handle scenarios when permissions may intentionally
     * be omitted from the manifest.
     * <p>
     * Note that on Marshmallow, this class also correctly handles checking for {@link
     * android.Manifest.permission#WRITE_SETTINGS},
     * {@link android.Manifest.permission#REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, and {@link
     * android.Manifest.permission#ACCESS_NOTIFICATION_POLICY}.
     *
     * @param context        Application context.
     * @param permissionName Name of the permission to check.
     * @return The status indicating whether {@code context} is able to use {@code
     * permissionName}.  Note that {@link PermissionStatus#NOT_GRANTED_BY_MANIFEST} is higher
     * priority than {@link com.twofortyfouram.spackle.PermissionCompat.PermissionStatus#NOT_GRANTED_BY_USER},
     * so {@link PermissionStatus#NOT_GRANTED_BY_MANIFEST} will be returned when both statuses
     * are true.
     */
    @NonNull
    public static PermissionStatus getPermissionStatus(@NonNull final Context context,
                                                       @NonNull @Size(min = 1) final String permissionName) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(permissionName, "permissionName"); //$NON-NLS-1$

        /*
         * Note: Do not call getApplicationContext(), because some unit tests depend on replacing the
         * context.
         */
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.M)) {
            return getPermissionStatusMarshmallow(context, permissionName);
        }

        return getPermissionStatusLegacy(context, permissionName);
    }

    @NonNull
    private static PermissionStatus getPermissionStatusLegacy(@NonNull final Context context,
                                                              @NonNull @Size(min = 1) final String permissionName) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(permissionName, "permissionName"); //$NON-NLS-1$

        if (PackageManager.PERMISSION_DENIED == context.getPackageManager().checkPermission(
                permissionName, context.getPackageName())) {
            if (Constants.IS_LOGGING_ENABLED) {
                Lumberjack.i("Permission %s is not granted via the AndroidManifest",
                        permissionName); //$NON-NLS-1$
            }
            return PermissionStatus.NOT_GRANTED_BY_MANIFEST;
        }

        /*
         * Some custom ROMs allowed permissions to be disabled dynamically before the advent of
         * Marshallow. It is not possible to detect when that happens.
         *
         * http://review.cyanogenmod.com/#/c/4055/
         */

        return PermissionStatus.GRANTED;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.M)
    private static PermissionStatus getPermissionStatusMarshmallow(@NonNull final Context context,
                                                                   @NonNull @Size(min = 1) final String permissionName) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(permissionName, "permissionName"); //$NON-NLS-1$
        /*
         * Note: Do not call getApplicationContext(), because some unit tests depend on replacing the
         * context.
         */

        if (!isPermissionGrantedByUser(context, permissionName)) {
            if (isPermissionGrantedByManifest(context, permissionName)) {
                return PermissionStatus.NOT_GRANTED_BY_USER;
            } else {
                return PermissionStatus.NOT_GRANTED_BY_MANIFEST;
            }
        } else {
            if (Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(permissionName) || Manifest.permission.ACTIVITY_RECOGNITION.equals(permissionName)) {
                if (!isPermissionGrantedByManifest(context, permissionName)) {
                    return PermissionStatus.NOT_GRANTED_BY_MANIFEST;
                }
            }
        }

        return PermissionStatus.GRANTED;
    }

    /**
     * @param context        Application context.
     * @param permissionName Name of the permission to check.
     * @return True if the permission is granted by the user.  If false, the permission may not be
     * granted by the user or may not be granted by the manifest.  Further disambiguation is
     * required by the caller.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static boolean isPermissionGrantedByUser(@NonNull final Context context,
                                                     @NonNull @Size(min = 1) final String permissionName) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(permissionName, "permissionName"); //$NON-NLS-1$

        /*
         * WRITE_SETTINGS, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, and ACCESS_NOTIFICATION_POLICY
         * behave differently from other permissions and have to be checked in a different way.  The
         * lack of consistency in the Android SDK is frustrating, so this implementation smooths
         * that over.
         *
         * To make unit testing easier, this class is aware of the FeatureContextWrapper
         * implementation.  If a test context, then we fall back to a different set of behaviors.
         */
        if (Manifest.permission.WRITE_SETTINGS.equals(permissionName)
                && !FEATURE_CONTEXT_WRAPPER_CLASS_NAME.equals(context.getClass().getName())) {
            if (Settings.System.canWrite(context)) {
                return true;
            }
        } else if (Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                .equals(permissionName) && !FEATURE_CONTEXT_WRAPPER_CLASS_NAME
                .equals(context.getClass().getName())) {
            if (context.getSystemService(PowerManager.class)
                    .isIgnoringBatteryOptimizations(context.getPackageName())) {
                return true;
            }
        } else if (Manifest.permission.ACCESS_NOTIFICATION_POLICY
                .equals(permissionName) && !FEATURE_CONTEXT_WRAPPER_CLASS_NAME
                .equals(context.getClass().getName())) {
            if (context.getSystemService(NotificationManager.class)
                    .isNotificationPolicyAccessGranted()) {
                return true;
            }
        } else if (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permissionName)&& !FEATURE_CONTEXT_WRAPPER_CLASS_NAME
                .equals(context.getClass().getName())) {
            if (Settings.canDrawOverlays(context)) {
                return true;
            }
        } else if (Manifest.permission.ACTIVITY_RECOGNITION.equals(permissionName) || Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(permissionName)) {
            final int targetSdkVersion = AppBuildInfo.getTargetSdkVersion(context);
            if (targetSdkVersion >= Build.VERSION_CODES.Q && AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.Q)) {
                if (PackageManager.PERMISSION_GRANTED == context
                        .checkSelfPermission(permissionName)) {
                    return true;
                }
            } else {
                // Always true if the target SDK version is not current
                return true;
            }
        } else if (PackageManager.PERMISSION_GRANTED == context
                .checkSelfPermission(permissionName)) {
            return true;
        }

        return false;
    }

    /**
     * @param context        Application context.
     * @param permissionName Name of the permission to check.
     * @return True if the permission is explicitly declared in the manifest.
     */
    // TODO: [Case 16597] This may not work correctly with a sharedUserId and a second package
    // that grants a permission.
    private static boolean isPermissionGrantedByManifest(@NonNull final Context context,
                                                         @NonNull @Size(min = 1) final String permissionName) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(permissionName, "permissionName"); //$NON-NLS-1$

        @NonNull final PackageInfo myPackageInfo = AppBuildInfo.getMyPackageInfo(context,
                PackageManager.GET_PERMISSIONS);

        final String[] requestedPermissions = myPackageInfo.requestedPermissions;
        if (null != requestedPermissions) {
            final int length = requestedPermissions.length;
            for (int i = 0; i < length; i++) {
                if (permissionName.equals(requestedPermissions[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Rather than use an IntDef, let ProGuard take care of optimizing this for us.
     */
    @ThreadSafe
    public enum PermissionStatus {
        /**
         * The permission is missing from the Android Manifest, so the permission cannot be used
         * and
         * the permission cannot be requested at runtime.  This state may not necessarily be due to
         * developer error; some applications and some SDKs may scale up
         * and down based on the permissions available.  For example, an application may have
         * different build flavors with different permissions enabled via the manifest or the user
         * may revoke permissions at runtime.
         */
        NOT_GRANTED_BY_MANIFEST,

        /**
         * The permission is not granted by the user, per the new permission APIs in Android
         * Marshmallow.  The app needs to ask the user to grant the permission.  This status will
         * only be returned on Marshmallow.
         */
        NOT_GRANTED_BY_USER,

        /**
         * The permission is granted and the app can use the permission.
         */
        GRANTED
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PermissionCompat() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
