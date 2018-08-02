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

package com.twofortyfouram.locale.sdk.host.test.setting.ui.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity;
import com.twofortyfouram.locale.sdk.host.test.setting.bundle.PluginJsonValues;

import net.jcip.annotations.NotThreadSafe;

import org.json.JSONObject;

@NotThreadSafe
public final class PluginSettingActivity extends AbstractPluginActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();
    }

    @Override
    public boolean isJsonValid(@NonNull final JSONObject jsonObject) {
        return PluginJsonValues.isJsonValid(jsonObject);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull final JSONObject previousJson,
            @NonNull final String previousBlurb) {

    }

    @Override
    public JSONObject getResultJson() {
        return new JSONObject();
    }

    @Override
    @NonNull
    public String getResultBlurb(@NonNull final JSONObject jsonObject) {
        return "";
    }

}
