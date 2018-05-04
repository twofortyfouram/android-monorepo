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

package com.twofortyfouram.locale.sdk.host.test.fixture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.locale.sdk.host.model.ThirdPartyPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.model.PluginType;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.TESTS)
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public final class PluginInstanceDataFixture {

    @NonNull
    public static final PluginType DEFAULT_TYPE = PluginType.SETTING;

    @NonNull
    public static final String DEFAULT_REGISTRY_NAME = ThirdPartyPlugin
            .generateRegistryName("foo", "bar");

    @NonNull
    public static final String DEFAULT_BLURB = "Thanks Obama"; //$NON-NLS-1$

    @NonNull
    public static Bundle getDefaultBundle() {
        return new Bundle();
    }

    @NonNull
    public static PluginInstanceData newDefaultPluginInstanceData() {
        return new PluginInstanceData(DEFAULT_TYPE,
                DEFAULT_REGISTRY_NAME,
                getDefaultBundle(),
                DEFAULT_BLURB);
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginInstanceDataFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
