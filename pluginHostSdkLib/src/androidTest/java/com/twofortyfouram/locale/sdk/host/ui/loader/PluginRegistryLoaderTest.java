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

package com.twofortyfouram.locale.sdk.host.ui.loader;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.host.internal.ThirdPartyPluginRegistry;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.locale.sdk.host.test.Junit4LoaderTestCase;
import com.twofortyfouram.locale.sdk.host.test.fixture.DebugPluginFixture;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.HONEYCOMB)
public final class PluginRegistryLoaderTest extends Junit4LoaderTestCase {

    @MediumTest
    @Test
    public void testLoad_conditions() {
        final PluginRegistryLoader loader = new PluginRegistryLoader(
                InstrumentationRegistry.getContext(),
                ThirdPartyPluginRegistry.getInstance(InstrumentationRegistry.getContext()),
                PluginType.CONDITION);

        final Map<String, IPlugin> loaderRegistry = getLoaderResultSynchronously(loader);

        assertThat(loaderRegistry, notNullValue());
        assertThat(loaderRegistry,
                hasKey(DebugPluginFixture.getDebugPluginCondition().getRegistryName()));
    }

    @MediumTest
    @Test
    public void testLoad_settings() {
        final PluginRegistryLoader loader = new PluginRegistryLoader(
                InstrumentationRegistry.getContext(),
                ThirdPartyPluginRegistry.getInstance(InstrumentationRegistry.getContext()),
                PluginType.SETTING);

        final Map<String, IPlugin> loaderRegistry = getLoaderResultSynchronously(loader);

        assertThat(loaderRegistry, notNullValue());
        assertThat(loaderRegistry,
                hasKey(DebugPluginFixture.getDebugPluginSetting().getRegistryName()));
    }
}
