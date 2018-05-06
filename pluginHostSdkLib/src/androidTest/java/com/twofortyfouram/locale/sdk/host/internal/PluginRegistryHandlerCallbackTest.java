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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.locale.sdk.host.test.condition.ui.activity.PluginConditionActivity;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginConfigurationFixture;
import com.twofortyfouram.locale.sdk.host.test.setting.ui.activity.PluginSettingActivity;
import com.twofortyfouram.spackle.HandlerThreadFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsMapContaining.hasKey;

@RunWith(AndroidJUnit4.class)
public final class PluginRegistryHandlerCallbackTest {

    @MediumTest
    @Test
    public void handleInit() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        assertThat(registryHandler.mMutableConditionMap, nullValue());
        assertThat(registryHandler.mMutableSettingMap, nullValue());

        registryHandler.handleInit();

        assertThat(registryHandler.mMutableConditionMap, notNullValue());
        assertThat(registryHandler.mMutableSettingMap, notNullValue());

        assertThat(registryHandler.mMutableConditionMap.isEmpty(), is(false));
        assertThat(registryHandler.mMutableSettingMap.isEmpty(), is(false));

        /*
         * Verify the debug plug-in condition and debug plug-in setting were
         * detected.
         */
        assertThat(registryHandler.mMutableConditionMap, hasKey(Plugin
                .generateRegistryName(context.getPackageName(),
                        PluginConditionActivity.class.getName())));
        assertThat(registryHandler.mMutableSettingMap, hasKey(Plugin.generateRegistryName(
                context.getPackageName(), PluginSettingActivity.class.getName())));
    }

    @MediumTest
    @Test
    public void handleRemovePackage_condition_and_setting() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);

        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        /*
         * Insert a fake condition
         */
        final Plugin testPluginCondition = new Plugin(
                PluginType.CONDITION,
                "com.twofortyfouram.locale.condition.hack",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.EditActivity",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.QueryReceiver",//$NON-NLS
                1, PluginConfigurationFixture
                .newPluginConfiguration());
        registryHandler.mMutableConditionMap.put(testPluginCondition.getRegistryName(),
                testPluginCondition);

        /*
         * Insert a fake setting
         */
        final Plugin testPluginSetting = new Plugin(
                PluginType.CONDITION,
                "com.twofortyfouram.locale.condition.hack",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.EditActivity",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.FireReceiver",//$NON-NLS
                1, PluginConfigurationFixture
                .newPluginConfiguration());
        registryHandler.mMutableSettingMap.put(testPluginSetting.getRegistryName(),
                testPluginCondition);

        assertThat(registryHandler.handlePackageRemoved(
                "com.twofortyfouram.locale.condition.hack"),
                is(PackageResult.CONDITIONS_AND_SETTINGS_CHANGED)); //$NON-NLS-1$

        /*
         * Verify the maps are back to their original states
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleRemovePackage_no_plugin() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler
                        .handlePackageRemoved("com.twofortyfouram.locale.hack"), //$NON-NLS-1$
                is(PackageResult.NOTHING_CHANGED));

        /*
         * Verify the maps were unchanged
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));

    }

    @MediumTest
    @Test
    public void handleRemovePackage_condition() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        /*
         * Insert a fake condition
         */
        final Plugin testPlugin = new Plugin(
                PluginType.CONDITION,
                "com.twofortyfouram.locale.condition.hack",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.EditActivity",//$NON-NLS
                "com.twofortyfouram.locale.condition.hack.QueryReceiver",//$NON-NLS
                1, PluginConfigurationFixture
                .newPluginConfiguration());
        registryHandler.mMutableConditionMap.put(testPlugin.getRegistryName(), testPlugin);

        assertThat(registryHandler
                        .handlePackageRemoved(
                                "com.twofortyfouram.locale.condition.hack"), //$NON-NLS-1$
                is(PackageResult.CONDITIONS_CHANGED));

        /*
         * Verify the maps are back to their original states
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleRemovePackage_setting() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        /*
         * Insert a fake setting
         */
        final Plugin testPlugin = new Plugin(
                PluginType.SETTING,
                "com.twofortyfouram.locale.setting.hack", //$NON-NLS
                "com.twofortyfouram.locale.setting.hack.EditActivity",//$NON-NLS
                "com.twofortyfouram.locale.setting.hack.FireReceiver",//$NON-NLS
                1, PluginConfigurationFixture
                .newPluginConfiguration());
        registryHandler.mMutableSettingMap.put(testPlugin.getRegistryName(), testPlugin);

        assertThat(registryHandler.handlePackageRemoved(
                "com.twofortyfouram.locale.setting.hack"), //$NON-NLS-1$
                is(PackageResult.SETTINGS_CHANGED));

        /*
         * Verify the maps are back to their original states
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));

    }

    @MediumTest
    @Test
    public void handleAddPackage_no_package() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler
                        .handlePackageAdded("com.twofortyfouram.locale.hack"), //$NON-NLS-1$
                is(PackageResult.NOTHING_CHANGED));

        /*
         * Verify the maps were unchanged
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleAddPackage_no_plugin() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler.handlePackageAdded("com.google.maps"), //$NON-NLS-1$
                is(PackageResult.NOTHING_CHANGED));

        /*
         * Verify the maps were unchanged
         */
        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleAddPackage_condition() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final ComponentName disabledComponent = new ComponentName(context,
                PluginSettingActivity.class.getName());

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        try {
            context.getPackageManager().setComponentEnabledSetting(disabledComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            registryHandler.handleInit();

            registryHandler.mMutableConditionMap.clear();
            registryHandler.mMutableSettingMap.clear();

            assertThat(registryHandler
                    .handlePackageAdded(
                            context.getPackageName()), is(PackageResult.CONDITIONS_CHANGED));

            final String conditionKey = Plugin.generateRegistryName(context.getPackageName(),
                    PluginConditionActivity.class.getName());

            assertThat(registryHandler.mMutableConditionMap, hasKey(conditionKey));
            assertThat(registryHandler.mMutableSettingMap.isEmpty(), is(true));
        } finally {
            context.getPackageManager().setComponentEnabledSetting(disabledComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }
    }

    @MediumTest
    @Test
    public void handleAddPackage_setting() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final ComponentName disabledComponent = new ComponentName(context,
                PluginConditionActivity.class.getName());

        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread(uuid, HandlerThreadFactory.ThreadPriority.DEFAULT);
        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        try {
            context.getPackageManager().setComponentEnabledSetting(disabledComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            registryHandler.handleInit();

            registryHandler.mMutableConditionMap.clear();
            registryHandler.mMutableSettingMap.clear();

            assertThat(registryHandler
                    .handlePackageAdded(
                            context.getPackageName()), is(PackageResult.SETTINGS_CHANGED));

            final String settingKey = Plugin.generateRegistryName(context.getPackageName(),
                    PluginSettingActivity.class.getName());

            assertThat(registryHandler.mMutableConditionMap.isEmpty(), is(true));
            assertThat(registryHandler.mMutableSettingMap, hasKey(settingKey));
        } finally {
            context.getPackageManager().setComponentEnabledSetting(disabledComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }
    }

    @MediumTest
    @Test
    public void handleAddPackage_condition_and_setting() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        registryHandler.mMutableConditionMap.clear();
        registryHandler.mMutableSettingMap.clear();

        assertThat(registryHandler
                        .handlePackageAdded(
                                context.getPackageName()),
                is(PackageResult.CONDITIONS_AND_SETTINGS_CHANGED));

        final String conditionKey = Plugin.generateRegistryName(context.getPackageName(),
                PluginConditionActivity.class.getName());
        final String settingKey = Plugin.generateRegistryName(context.getPackageName(),
                PluginSettingActivity.class.getName());

        assertThat(registryHandler.mMutableConditionMap, hasKey(conditionKey));
        assertThat(registryHandler.mMutableSettingMap, hasKey(settingKey));
    }

    @MediumTest
    @Test
    public void handleAddPackage_no_change() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);

        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler
                .handlePackageChanged(
                        context.getPackageName()), is(PackageResult.NOTHING_CHANGED));

        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleChangePackage_no_change() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread(uuid, HandlerThreadFactory.ThreadPriority.DEFAULT);
        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);
        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler.handlePackageChanged("com.google.maps"), //$NON-NLS-1$
                is(PackageResult.NOTHING_CHANGED));

        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }

    @MediumTest
    @Test
    public void handleChangePackage_no_package() {
        final Context context = InstrumentationRegistry.getContext();
        final String uuid = UUID.randomUUID().toString();

        final PluginRegistryHandlerCallback registryHandler = new PluginRegistryHandlerCallback(
                context, uuid, uuid);

        registryHandler.handleInit();

        final Map<String, IPlugin> conditionsBefore = new HashMap<>(
                registryHandler.mMutableConditionMap);
        final Map<String, IPlugin> settingsBefore = new HashMap<>(
                registryHandler.mMutableSettingMap);

        assertThat(registryHandler
                        .handlePackageChanged("com.twofortyfouram.locale.hack"), //$NON-NLS-1$
                is(PackageResult.NOTHING_CHANGED));

        assertThat(registryHandler.mMutableConditionMap, is(conditionsBefore));
        assertThat(registryHandler.mMutableSettingMap, is(settingsBefore));
    }
}
