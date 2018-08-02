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

import androidx.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

/**
 * A result code for methods that may or may not modify the registry
 * representation.
 */
@ThreadSafe
public enum PackageResult {
    /**
     * Neither the set of conditions nor the set of settings changed
     */
    @NonNull
    NOTHING_CHANGED,

    /**
     * Only the set of conditions changed
     */
    @NonNull
    CONDITIONS_CHANGED,

    /**
     * Only the set of settings changed
     */
    @NonNull
    SETTINGS_CHANGED,

    /**
     * Both conditions and settings changed
     */
    @NonNull
    CONDITIONS_AND_SETTINGS_CHANGED;

    /**
     * @param conditionsChanged true if conditions changed
     * @param settingsChanged   true if settings changed
     * @return The proper enum for the given parameters
     */
    @NonNull
    public static PackageResult get(final boolean conditionsChanged,
            final boolean settingsChanged) {
        if (conditionsChanged && settingsChanged) {
            return PackageResult.CONDITIONS_AND_SETTINGS_CHANGED;
        } else if (conditionsChanged) {
            return PackageResult.CONDITIONS_CHANGED;
        } else if (settingsChanged) {
            return PackageResult.SETTINGS_CHANGED;
        } else {
            return PackageResult.NOTHING_CHANGED;
        }
    }
}
