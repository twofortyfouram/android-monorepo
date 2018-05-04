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

/**
 * Common interface for plug-in errors.  An error is a problem that occurs while interacting with
 * the plug-in.
 */
public interface PluginError {

    /**
     * @return A non-localized error message with an explanation of the error.
     * This is intended to display to plug-in developers via a log
     * message.
     */
    @NonNull
    String getDeveloperExplanation();

    /**
     * @return True if the error is fatal.  If false, the error is non-fatal and should be
     * considered to be a warning.
     */
    boolean isFatal();
}
