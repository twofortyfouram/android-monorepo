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

package com.twofortyfouram.locale.sdk.host.model;

import android.support.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Possible errors that may occur during the execute phase of interacting with plug-ins.
 */
@ThreadSafe
public enum PluginErrorExecute implements IPluginError {

    @NonNull
    CONDITION_RESULT_CODE_BAD(
            "Plug-in conditions must return one of the result codes RESULT_SATISFIED, RESULT_UNSATISFIED, or RESULT_UNKNOWN",
            true); //$NON-NLS-1$

    /**
     * A non-localized message describing the error.
     */
    @NonNull
    private final String mDeveloperExplanation;

    /**
     * Indicates whether the error is fatal or is just a warning.
     */
    private final boolean mIsFatal;

    PluginErrorExecute(@NonNull final String developerExplanation, final boolean isFatal) {
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
