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

package com.twofortyfouram.locale.sdk.client.internal;


import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.twofortyfouram.locale.sdk.client.test.condition.ui.activity.PluginActivityFixture;
import com.twofortyfouram.locale.sdk.client.ui.activity.PluginType;

public final class PluginActivityDelegateTest extends AndroidTestCase {


    @SmallTest
    public static void testIsLocaleIntent_condition() {
        assertTrue(PluginActivityDelegate
                .isLocalePluginIntent(PluginActivityFixture.getDefaultStartIntent(PluginType.CONDITION)));
    }

    @SmallTest
    public static void testIsLocaleIntent_setting() {
        assertTrue(PluginActivityDelegate
                .isLocalePluginIntent(PluginActivityFixture.getDefaultStartIntent(PluginType.SETTING)));
    }

    @SmallTest
    public static void testIsLocaleIntent_neither() {
        assertFalse(PluginActivityDelegate.isLocalePluginIntent(new Intent()));
    }
}
