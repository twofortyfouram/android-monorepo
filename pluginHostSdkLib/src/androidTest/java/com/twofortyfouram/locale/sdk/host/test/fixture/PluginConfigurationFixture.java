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

package com.twofortyfouram.locale.sdk.host.test.fixture;

import android.support.annotation.NonNull;

import com.twofortyfouram.locale.sdk.host.model.PluginConfiguration;

import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;

@ThreadSafe
public final class PluginConfigurationFixture {

    /**
     * Fixture to obtain a new plug-in configuration.
     */
    @NonNull
    public static PluginConfiguration newPluginConfiguration() {
        return new PluginConfiguration(false, false, false, false, false, false,
                new LinkedList<String>());
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginConfigurationFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
