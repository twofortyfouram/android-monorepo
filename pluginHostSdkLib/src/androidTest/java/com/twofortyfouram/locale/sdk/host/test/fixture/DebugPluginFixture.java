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

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.test.InstrumentationRegistry;

import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.locale.sdk.host.test.condition.receiver.PluginConditionReceiver;
import com.twofortyfouram.locale.sdk.host.test.condition.ui.activity.PluginConditionActivity;
import com.twofortyfouram.locale.sdk.host.test.setting.receiver.PluginSettingReceiver;
import com.twofortyfouram.locale.sdk.host.test.setting.ui.activity.PluginSettingActivity;

import net.jcip.annotations.ThreadSafe;

/*
 * Fixture for the test plug-ins embedded in the test package.
 */
@ThreadSafe
@RestrictTo(RestrictTo.Scope.TESTS)
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public final class DebugPluginFixture {

    @NonNull
    public static Plugin getDebugPluginCondition() {
        return new Plugin(PluginType.CONDITION,
                InstrumentationRegistry.getContext().getPackageName(),
                PluginConditionActivity.class.getName(),
                PluginConditionReceiver.class.getName(), 0,
                PluginConfigurationFixture.newPluginConfiguration());
    }

    @NonNull
    public static Plugin getDebugPluginSetting() {
        return new Plugin(PluginType.SETTING,
                InstrumentationRegistry.getContext().getPackageName(),
                PluginSettingActivity.class.getName(),
                PluginSettingReceiver.class.getName(), 0,
                PluginConfigurationFixture.newPluginConfiguration());
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private DebugPluginFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}