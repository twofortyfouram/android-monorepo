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

package com.twofortyfouram.locale.sdk.host.test.condition.receiver;

import android.content.Context;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.api.v1.annotation.ConditionResult;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginConditionReceiver;
import com.twofortyfouram.locale.sdk.host.test.condition.bundle.PluginJsonValues;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONObject;

/**
 * A basic well-formed plug-in Condition receiver.
 */
@ThreadSafe
public final class PluginConditionReceiver extends AbstractPluginConditionReceiver {

    @Override
    protected boolean isJsonValid(@NonNull final JSONObject json) {
        return PluginJsonValues.isJsonValid(json);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    @ConditionResult
    protected int getPluginConditionResult(@NonNull final Context context,
            @NonNull final JSONObject json) {
        return PluginJsonValues.getResultCode(json);
    }
}
