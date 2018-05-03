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

package com.twofortyfouram.locale.sdk.host.model;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.api.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class PluginTypeTest {

    @Test
    @SmallTest
    public void getActivityIntentAction_condition() {
        assertThat(PluginType.CONDITION.getActivityIntentAction(),
                is(Intent.ACTION_EDIT_CONDITION));
    }

    @Test
    @SmallTest
    public void getActivityIntentAction_setting() {
        assertThat(PluginType.SETTING.getActivityIntentAction(),
                is(Intent.ACTION_EDIT_SETTING));
    }

    @Test
    @SmallTest
    public void getReceiverIntentAction_condition() {
        assertThat(PluginType.CONDITION.getReceiverIntentAction(),
                is(Intent.ACTION_QUERY_CONDITION));
    }

    @Test
    @SmallTest
    public void getReceiverIntentAction_setting() {
        assertThat(PluginType.SETTING.getReceiverIntentAction(),
                is(Intent.ACTION_FIRE_SETTING));
    }
}
