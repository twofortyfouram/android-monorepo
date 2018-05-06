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

package com.twofortyfouram.locale.sdk.host.test.setting.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;
import com.twofortyfouram.locale.sdk.host.test.setting.bundle.PluginJsonValues;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONObject;

@ThreadSafe
public final class PluginSettingReceiver extends AbstractPluginSettingReceiver {


    @Override
    protected boolean isJsonValid(@NonNull final JSONObject jsonObject) {
        return PluginJsonValues.isJsonValid(jsonObject);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    protected void firePluginSetting(@NonNull final Context context,
            @NonNull final JSONObject jsonObject) {
        context.sendBroadcast(new Intent(PluginJsonValues.getActionToFire(jsonObject)));
    }
}
