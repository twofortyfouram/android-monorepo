/*
 * android-plugin-client-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.client.internal;


import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.client.test.condition.ui.activity.PluginActivityFixture;
import com.twofortyfouram.locale.sdk.client.ui.activity.PluginType;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class PluginActivityDelegateTest {

    @SmallTest
    @Test
    public void isLocaleIntent_condition() {
        assertThat(PluginActivityDelegate
                .isLocalePluginEditIntent(
                        PluginActivityFixture.getDefaultStartIntent(PluginType.CONDITION)), is(true));
    }

    @SmallTest
    @Test
    public void isLocaleIntent_setting() {
        assertThat(PluginActivityDelegate
                .isLocalePluginEditIntent(
                        PluginActivityFixture.getDefaultStartIntent(PluginType.SETTING)), is(true));
    }

    @SmallTest
    @Test
    public void isLocaleIntent_neither() {
        assertThat(PluginActivityDelegate.isLocalePluginEditIntent(new Intent()), is(false));
    }
}
