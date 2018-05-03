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

package com.twofortyfouram.locale.sdk.host.model;

import android.support.annotation.NonNull;

import com.twofortyfouram.locale.api.LocalePluginIntent;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.log.Lumberjack.formatMessage;

/**
 * Possible errors that may occur during the edit phase of interacting with plug-ins.
 */
@ThreadSafe
public enum PluginErrorEdit implements IPluginError {
    @NonNull
    ACTIVITY_NOT_FOUND_EXCEPTION(
            "The Activity could not be found.  To resolve this issue, make sure the plug-in package is still installed.",
            //$NON-NLS-1$
            true),

    @NonNull
    BLURB_MISSING(formatMessage(
            "%s is missing.  To resolve this issue, put the blurb extra in the Activity result Intent.",
            //$NON-NLS-1$
            LocalePluginIntent.EXTRA_STRING_BLURB), true),

    @NonNull
    BUNDLE_TOO_LARGE(
            formatMessage(
                    "%s is larger than the allowed maximum of %d bytes.  To resolve this issue, store less data in the Bundle.",
                    //$NON-NLS-1$
                    LocalePluginIntent.EXTRA_BUNDLE,
                    PluginInstanceData.MAXIMUM_BUNDLE_SIZE_BYTES), true
    ),
    @NonNull
    BUNDLE_MISSING(
            formatMessage(
                    "Extra %s is required.  To resolve this issue, put the Bundle extra in the Activity result Intent.",
                    //$NON-NLS-1$
                    LocalePluginIntent.EXTRA_BUNDLE), true
    ),

    @NonNull
    BUNDLE_NOT_SERIALIZABLE(
            formatMessage(
                    "%s could not be serialized.  To resolve this issue, be sure the Bundle doesn't contain Parcelable or private Serializable subclasses",
                    //$NON-NLS-1$
                    LocalePluginIntent.EXTRA_BUNDLE), true
    ),

    @NonNull
    INTENT_NULL(
            "Activity result Intent is null.  To resolve this issue, the child Activity needs to call setResult(RESULT_OK, Intent) or setResult(RESULT_CANCELED) before finishing.",
            //$NON-NLS-1$
            true),

    /**
     * An Intent or Bundle from the plug-in contained a private serializable
     * subclass which the host's classloader does not know about.
     */
    @NonNull
    PRIVATE_SERIALIZABLE(
            "Intent or Bundle contains a private Serializable subclass which is not known to this app's classloader.  To resolve this issue, the DO NOT place a private Serializable subclass in Intents sent across processes.",
            //$NON-NLS-1$
            true),

    @NonNull
    SECURITY_EXCEPTION(
            "The Activity could not be launched because of a security error.  To resolve this issue, make sure the Activity is exported and does not require a permission.",
            //$NON-NLS-1$
            true),

    @NonNull
    UNKNOWN_ACTIVITY_RESULT_CODE(
            "Plug-ins must return one of the result codes ACTIVITY.RESULT_OK or ACTIVITY.RESULT_CANCELED",
            //$NON-NLS-1$
            true),;

    /**
     * A non-localized message describing the error.
     */
    @NonNull
    private final String mDeveloperExplanation;

    /**
     * Indicates whether the error is fatal or is just a warning.
     */
    private final boolean mIsFatal;

    PluginErrorEdit(@NonNull final String developerExplanation, final boolean isFatal) {
        assertNotNull(developerExplanation, "developerExplanation"); //$NON-NLS-1$

        mDeveloperExplanation = developerExplanation;
        mIsFatal = isFatal;
    }


    @Override
    @NonNull
    public String getDeveloperExplanation() {
        return mDeveloperExplanation;
    }

    @Override
    public boolean isFatal() {
        return mIsFatal;
    }
}
