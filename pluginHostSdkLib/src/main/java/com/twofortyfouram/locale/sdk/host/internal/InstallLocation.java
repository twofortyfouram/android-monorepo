/*
 * android-plugin-sdk-for-locale
 * https://github.com/twofortyfouram/android-plugin-sdk-for-locale
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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.spackle.AndroidSdkVersion;

import net.jcip.annotations.ThreadSafe;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Represents the Android Manifest's possible states for install location.
 */
@ThreadSafe
public enum InstallLocation {
    /**
     * The application permits installation to either internal or external
     * storage, with Android automatically deciding.
     */
    auto,

    /**
     * The application can only be installed to internal storage.
     */
    internalOnly,

    /**
     * The application permits installation to either internal or external
     * storage, with preference for external storage.
     */
    preferExternal,

    /**
     * No install location was specified in the Android Manifest. In terms of
     * how Android interprets this, it is basically the same as
     * {@link #internalOnly}.
     */
    MISSING,

    /**
     * An unknown install location, such as a new install location added in
     * newer versions of Android.
     */
    UNKNOWN;

    /**
     * The Android Manifest int value for auto install location.
     */
    /*
     * Although this API became visible in API 21, the constant value was the same for previous Android versions.
     * Suppressing the lint warning is OK here.
     */
    @VisibleForTesting
    @SuppressLint("InlinedApi")
    /* package */ static final int MANIFEST_INSTALL_LOCATION_AUTO
            = PackageInfo.INSTALL_LOCATION_AUTO;

    /**
     * The Android Manifest int value for internal only install location.
     */
    /*
     * Although this API became visible in API 21, the constant value was the same for previous Android versions.
     * Suppressing the lint warning is OK here.
     */
    @VisibleForTesting
    @SuppressLint("InlinedApi")
    /* package */ static final int MANIFEST_INSTALL_LOCATION_INTERNAL_ONLY
            = PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY;

    /**
     * The Android Manifest int value for internal or external storage, with
     * preference for external storage.
     */
    /*
     * Although this API became visible in API 21, the constant value was the same for previous Android versions.
     * Suppressing the lint warning is OK here.
     */
    @VisibleForTesting
    @SuppressLint("InlinedApi")
    /* package */ static final int MANIFEST_INSTALL_LOCATION_PREFER_EXTERNAL
            = PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL;

    /**
     * Takes the integer value of install location from the Android Manifest and
     * converts it to an enum value.
     *
     * @param location one of the Android Manifest install locations.
     * @return The enum type for the install location.
     */
    @NonNull
    /* package */ static InstallLocation getInstallLocation(final int location) {
        switch (location) {
            case MANIFEST_INSTALL_LOCATION_AUTO: {
                return auto;
            }
            case MANIFEST_INSTALL_LOCATION_INTERNAL_ONLY: {
                return internalOnly;
            }
            case MANIFEST_INSTALL_LOCATION_PREFER_EXTERNAL: {
                return preferExternal;
            }
            default: {
                return UNKNOWN;
            }
        }
    }

    /**
     * Gets a package's install location, as per the Android Manifest.
     *
     * @param context     Application context.
     * @param packageName Package whose install location is to be checked.
     * @return the install location.
     * @throws android.content.pm.PackageManager.NameNotFoundException if {@code packageName} isn't
     *                                                                 installed.
     * @throws org.xmlpull.v1.XmlPullParserException                   If the target package's
     *                                                                 manifest couldn't
     *                                                                 be parsed.
     * @throws java.io.IOException                                     If an error occurred reading
     *                                                                 the target package.
     */
    @NonNull
    @Slow(Speed.MILLISECONDS)
    public static InstallLocation getManifestInstallLocation(@NonNull final Context context,
            @NonNull final String packageName) throws NameNotFoundException,
            XmlPullParserException, IOException {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.LOLLIPOP)) {
            return getInstallLocationLollipop(context, packageName);
        } else {
            return getInstallLocationLegacy(context, packageName);
        }
    }

    @NonNull
    private static InstallLocation getInstallLocationLegacy(@NonNull final Context context,
            @NonNull final String packageName)
            throws NameNotFoundException, XmlPullParserException, IOException {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$
        /*
         * There isn't a public API to check the installLocation of an APK on older Android
         * versions, so this is a hacky implementation to read the value directly from the
         * package's AndroidManifest.
         */
        try (final XmlResourceParser xml = context
                .createPackageContext(packageName, Context.CONTEXT_RESTRICTED).getAssets()
                .openXmlResourceParser("AndroidManifest.xml")) {
            for (int eventType = xml.getEventType(); XmlPullParser.END_DOCUMENT != eventType;
                    eventType = xml
                            .nextToken()) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if (xml.getName().matches("manifest")) { //$NON-NLS-1$
                            for (int x = 0; x < xml.getAttributeCount(); x++) {
                                if (xml.getAttributeName(x)
                                        .matches("installLocation")) { //$NON-NLS-1$
                                    return InstallLocation.getInstallLocation(Integer.parseInt(xml
                                            .getAttributeValue(x)));
                                }
                            }
                        }

                        break;
                    }
                }
            }

            /*
             * Once this point is reached, it can be assumed the installLocation
             * didn't exist in the AndroidManifest
             */
            return InstallLocation.MISSING;
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static InstallLocation getInstallLocationLollipop(@NonNull final Context context,
            @NonNull final String packageName) throws NameNotFoundException {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
        return InstallLocation.getInstallLocation(info.installLocation);
    }
}