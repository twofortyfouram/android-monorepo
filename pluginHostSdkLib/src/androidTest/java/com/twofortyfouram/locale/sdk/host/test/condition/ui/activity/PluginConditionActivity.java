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

package com.twofortyfouram.locale.sdk.host.test.condition.ui.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity;
import com.twofortyfouram.locale.sdk.host.test.R;
import com.twofortyfouram.locale.sdk.host.test.condition.bundle.PluginJsonValues;
import com.twofortyfouram.spackle.ResourceUtil;

import net.jcip.annotations.NotThreadSafe;

import org.json.JSONObject;

@NotThreadSafe
public final class PluginConditionActivity extends AbstractPluginActivity {

    @Nullable
    private Adapter mAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.com_twofortyfouram_locale_sdk_host_activity_condition);

        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, getResources()
                .getStringArray(R.array.com_twofortyfouram_locale_sdk_host_condition_states)
        );
    }

    @Override
    public boolean isJsonValid(@NonNull final JSONObject json) {
        return PluginJsonValues.isJsonValid(json);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull final JSONObject previousJson,
            @NonNull final String previousBlurb) {
        final int code = PluginJsonValues.getResultCode(previousJson);

        final int resourceId = getResourceIdForCode(code);

        if (0 != resourceId) {
            final int position = ResourceUtil
                    .getPositionForIdInArray(this.getApplicationContext(),
                            R.array.com_twofortyfouram_locale_sdk_host_condition_states,
                            resourceId);

            final ListView listView = findViewById(android.R.id.list);
            listView.setItemChecked(position, true);
        }
    }

    @Override
    public JSONObject getResultJson() {
        JSONObject resultJson = null;

        {
            final ListView listView = findViewById(android.R.id.list);

            final int position = listView.getCheckedItemPosition();
            if (AdapterView.INVALID_POSITION != position) {
                final int resourceId = ResourceUtil.getResourceIdForPositionInArray(this
                                .getApplicationContext(),
                        R.array.com_twofortyfouram_locale_sdk_host_condition_states, position
                );

                final int code;
                if (R.string.com_twofortyfouram_locale_sdk_host_condition_satisfied
                        == resourceId) {
                    code = LocalePluginIntent.RESULT_CONDITION_SATISFIED;
                } else if (R.string.com_twofortyfouram_locale_sdk_host_condition_unsatisfied
                        == resourceId) {
                    code = LocalePluginIntent.RESULT_CONDITION_UNSATISFIED;
                } else if (R.string.com_twofortyfouram_locale_sdk_host_condition_unknown
                        == resourceId) {
                    code = LocalePluginIntent.RESULT_CONDITION_UNKNOWN;
                } else {
                    throw new AssertionError();
                }

                resultJson = PluginJsonValues.generateJson(getApplicationContext(), code);
            }
        }

        return resultJson;
    }

    @Override
    public String getResultBlurb(@NonNull JSONObject json) {
        final int code = PluginJsonValues.getResultCode(json);

        return getString(getResourceIdForCode(code));
    }

    /**
     * @param code Plug-in code.
     * @return The resource ID or 0 if no resource could be found.
     */
    private static int getResourceIdForCode(final int code) {
        final int resourceId;
        switch (code) {
            case LocalePluginIntent.RESULT_CONDITION_SATISFIED: {
                resourceId = R.string.com_twofortyfouram_locale_sdk_host_condition_satisfied;
                break;
            }
            case LocalePluginIntent.RESULT_CONDITION_UNSATISFIED: {
                resourceId = R.string.com_twofortyfouram_locale_sdk_host_condition_unsatisfied;
                break;
            }
            case LocalePluginIntent.RESULT_CONDITION_UNKNOWN: {
                resourceId = R.string.com_twofortyfouram_locale_sdk_host_condition_unknown;
                break;
            }
            default: {
                resourceId = 0;
            }
        }

        return resourceId;
    }
}
