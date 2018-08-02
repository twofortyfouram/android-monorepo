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
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import net.jcip.annotations.NotThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Helper for determining if the Android configuration has changed in an interesting way
 * that would impact the UI.
 */
@NotThreadSafe
public final class UiConfigChangeChecker {

    /**
     * The configuration as of the last time {@link #checkNewConfig(Resources)}
     * was called.
     */
    @NonNull
    private final Configuration mLastConfiguration = new Configuration();

    /**
     * The density as of the last time {@link #checkNewConfig(Resources)} was
     * called.
     */
    private int mLastDensity = 0;

    /**
     * Call to provide the latest configuration.
     *
     * @param res New resources.
     * @return true if the new resources are different from the previous
     * resources.
     */
    @CheckResult
    public boolean checkNewConfig(@NonNull final Resources res) {
        assertNotNull(res, "res"); //$NON-NLS-1$

        /*
         * Note: this implementation is somewhat brittle, as future versions of
         * Android could introduce new configuration changes that this mechanism
         * doesn't detect.
         */
        final int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
        final boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;

        mLastDensity = res.getDisplayMetrics().densityDpi;

        return densityChanged
                || 0 != (configChanges & (ActivityInfo.CONFIG_LOCALE | ActivityInfo.CONFIG_UI_MODE
                | ActivityInfo.CONFIG_SCREEN_LAYOUT));
    }
}
