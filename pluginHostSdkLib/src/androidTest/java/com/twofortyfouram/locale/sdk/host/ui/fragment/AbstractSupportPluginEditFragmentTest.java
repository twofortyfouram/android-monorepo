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

package com.twofortyfouram.locale.sdk.host.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginFixture;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.HONEYCOMB)
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class AbstractSupportPluginEditFragmentTest {

    @SmallTest
    @Test
    public void newArgs_without_previous_values() {
        final Bundle bundle = AbstractSupportPluginEditFragment
                .newArgs(PluginFixture.newDefaultPlugin(), null);

        BundleAssertions.assertKeyCount(bundle, 1);
        assertEquals(
                PluginFixture.newDefaultPlugin(),
                bundle.getParcelable(
                        AbstractSupportPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN)
        );
    }

    @SmallTest
    @Test
    public void newArgs_with_previous_values() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final PluginInstanceData pluginInstanceData = new PluginInstanceData(plugin.getType(),
                plugin.getRegistryName(), new Bundle(),
                "foo");  //$NON-NLS-1$

        final Bundle bundle = AbstractSupportPluginEditFragment
                .newArgs(PluginFixture.newDefaultPlugin(),
                        pluginInstanceData);

        BundleAssertions.assertKeyCount(bundle, 2);
        assertEquals(
                plugin,
                bundle.getParcelable(
                        AbstractSupportPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN)
        );
        assertEquals(pluginInstanceData, bundle.getParcelable(
                AbstractSupportPluginEditFragment.ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA));
    }
}
