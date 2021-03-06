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

import android.content.Context;
import android.os.Parcel;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.host.test.R;
import com.twofortyfouram.locale.sdk.host.test.condition.receiver.PluginConditionReceiver;
import com.twofortyfouram.locale.sdk.host.test.condition.ui.activity.PluginConditionActivity;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginConfigurationFixture;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginFixture;
import com.twofortyfouram.locale.sdk.host.test.setting.receiver.PluginSettingReceiver;
import com.twofortyfouram.locale.sdk.host.test.setting.ui.activity.PluginSettingActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public final class PluginTest {

    @SmallTest
    @Test
    public void getPackageName() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin.getPackageName(), is(PluginFixture.DEFAULT_PACKAGE));
    }

    @SmallTest
    @Test
    public void getActivityClassName() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin.getActivityClassName(), is(PluginFixture.DEFAULT_ACTIVITY));
    }

    @SmallTest
    @Test
    public void getReceiverClassName() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin.getReceiverClassName(), is(PluginFixture.DEFAULT_RECEIVER));
    }

    @SmallTest
    @Test
    public void getVersionCode() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin.getVersionCode(), is(PluginFixture.DEFAULT_VERSION_CODE));
    }

    @SmallTest
    @Test
    public void getConfiguration() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin.getConfiguration(), is(PluginFixture.DEFAULT_CONFIGURATION));
    }

    @SmallTest
    @Test
    public void equals_null() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin, not(equalTo(null)));
    }

    @SmallTest
    @Test
    public void equals_different_object_type() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin, not(equalTo(new Object())));
    }

    @SmallTest
    @Test
    public void equals_same_object() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin, is(defaultPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_object() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();
        final Plugin defaultPlugin2 = PluginFixture.newDefaultPlugin();

        assertThat(defaultPlugin, is(defaultPlugin2));
    }

    @SmallTest
    @Test
    public void equals_different_type() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.SETTING,
                PluginFixture.DEFAULT_PACKAGE, PluginFixture.DEFAULT_ACTIVITY,
                PluginFixture.DEFAULT_RECEIVER,
                PluginFixture.DEFAULT_VERSION_CODE,
                PluginFixture.DEFAULT_CONFIGURATION);

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_package() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.CONDITION,
                PluginFixture.DEFAULT_PACKAGE + "foo", PluginFixture.DEFAULT_ACTIVITY,
                PluginFixture.DEFAULT_RECEIVER,
                PluginFixture.DEFAULT_VERSION_CODE,
                PluginFixture.DEFAULT_CONFIGURATION);

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_activity() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.CONDITION,
                PluginFixture.DEFAULT_PACKAGE, PluginFixture.DEFAULT_ACTIVITY + "foo",
                PluginFixture.DEFAULT_RECEIVER,
                PluginFixture.DEFAULT_VERSION_CODE,
                PluginFixture.DEFAULT_CONFIGURATION);

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_receiver() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.CONDITION,
                PluginFixture.DEFAULT_PACKAGE, PluginFixture.DEFAULT_ACTIVITY,
                PluginFixture.DEFAULT_RECEIVER + "foo",
                PluginFixture.DEFAULT_VERSION_CODE,
                PluginFixture.DEFAULT_CONFIGURATION);

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_version() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.CONDITION,
                PluginFixture.DEFAULT_PACKAGE, PluginFixture.DEFAULT_ACTIVITY,
                PluginFixture.DEFAULT_RECEIVER,
                PluginFixture.DEFAULT_VERSION_CODE + 1,
                PluginFixture.DEFAULT_CONFIGURATION);

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void equals_different_configuration() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final Plugin differentPlugin = new Plugin(PluginType.CONDITION,
                PluginFixture.DEFAULT_PACKAGE, PluginFixture.DEFAULT_ACTIVITY,
                PluginFixture.DEFAULT_RECEIVER,
                PluginFixture.DEFAULT_VERSION_CODE,
                new PluginConfiguration(true, true, true, true, true, true,
                        Collections.emptyList()));

        assertThat(defaultPlugin, not(differentPlugin));
    }

    @SmallTest
    @Test
    public void hashCode_same() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();
        assertThat(defaultPlugin.hashCode(), is(defaultPlugin.hashCode()));
    }

    @SmallTest
    @Test
    public void toString_not_null() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, notNullValue());
    }

    @SmallTest
    @Test
    public void toString_type() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(plugin.getType().toString()));
    }

    @SmallTest
    @Test
    public void toString_package() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(plugin.getPackageName()));
    }

    @SmallTest
    @Test
    public void toString_activity() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(plugin.getActivityClassName()));
    }

    @SmallTest
    @Test
    public void toString_receiver() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(plugin.getReceiverClassName()));
    }

    @SmallTest
    @Test
    public void toString_version() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(Integer.toString(plugin.getVersionCode())));
    }

    @SmallTest
    @Test
    public void toString_configuration() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final String pluginString = plugin.toString();

        assertThat(pluginString, containsString(plugin.getConfiguration().toString()));
    }

    @SmallTest
    @Test
    public void parcelable() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();

        final Parcel parcel = Parcel.obtain();
        try {
            plugin.writeToParcel(parcel, 0);

            /*
             * Reset parcel for reading.
             */
            parcel.setDataPosition(0);

            final Plugin pluginUnparceled = Plugin.CREATOR.createFromParcel(parcel);
            assertThat(pluginUnparceled, is(plugin));
        } finally {
            parcel.recycle();
        }
    }

    @SmallTest
    @Test
    public void parcelable_describe_contents() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();

        assertThat(plugin.describeContents(), is(0));
    }

    @SmallTest
    @Test
    public void getLabel_none() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();

        final String actual = plugin.getActivityLabel(InstrumentationRegistry.getContext());

        assertThat(actual, is(PluginFixture.DEFAULT_ACTIVITY));
    }

    @SmallTest
    @Test
    public void getLabel_debug_condition() {
        final Context context = InstrumentationRegistry.getContext();

        final Plugin plugin = new Plugin(PluginType.SETTING, context.getPackageName(),
                PluginConditionActivity.class.getName(), PluginConditionReceiver.class.getName(),
                1, PluginConfigurationFixture.newPluginConfiguration());

        final String expected = context
                .getString(R.string.com_twofortyfouram_locale_sdk_host_condition_name);

        assertThat(plugin.getActivityLabel(context), is(expected));
    }

    @SmallTest
    @Test
    public void getLabel_debug_setting() {
        final Context context = InstrumentationRegistry.getContext();

        final Plugin plugin = new Plugin(PluginType.SETTING, context.getPackageName(),
                PluginSettingActivity.class.getName(), PluginSettingReceiver.class.getName(), 1,
                PluginConfigurationFixture.newPluginConfiguration());

        final String expected = context
                .getString(R.string.com_twofortyfouram_locale_sdk_host_setting_name);

        assertThat(plugin.getActivityLabel(context), is(expected));
    }

    @SmallTest
    @Test
    public void getIcon_none() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final Context context = InstrumentationRegistry.getContext();

        assertThat(plugin.getActivityIcon(context), is(notNullValue()));
    }

    @SmallTest
    @Test
    public void getRegistryName() {
        final Plugin defaultPlugin = PluginFixture.newDefaultPlugin();

        final String expected = Plugin.generateRegistryName(PluginFixture.DEFAULT_PACKAGE,
                PluginFixture.DEFAULT_ACTIVITY);

        assertThat(defaultPlugin.getRegistryName(), is(expected));
    }

    @SmallTest
    @Test
    public void generateRegistryName() {
        assertThat(Plugin.generateRegistryName("foo", "bar"), is("foo:bar")); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void testGenerateRegistryName_null_package() {
        Plugin.generateRegistryName(null, "foo"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void testGenerateRegistryName_null_class() {
        Plugin.generateRegistryName("foo", null); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void testGenerateRegistryName_empty_package() {
        Plugin.generateRegistryName("", "foo"); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void testGenerateRegistryName_empty_class() {
        Plugin.generateRegistryName("foo", ""); //$NON-NLS
    }
}
