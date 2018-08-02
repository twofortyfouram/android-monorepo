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

package com.twofortyfouram.locale.sdk.client.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.locale.api.LocalePluginIntent;

import org.json.JSONObject;

/**
 * Common interface for plug-in Activities.
 */
public interface IPluginActivity {

    /**
     * @return The {@link LocalePluginIntent#EXTRA_STRING_JSON EXTRA_JSON} that
     * was previously saved to the host and subsequently passed back to this Activity for further
     * editing.  Internally, this method relies on {@link #isJsonValid(JSONObject)}.  If
     * the JSON exists but is not valid, this method will return null.
     */
    @Nullable
    JSONObject getPreviousJson();

    /**
     * @return The {@link LocalePluginIntent#EXTRA_STRING_BLURB
     * EXTRA_STRING_BLURB} that was previously saved to the host and subsequently passed back to
     * this Activity for further editing.
     */
    @Nullable
    String getPreviousBlurb();

    /**
     * <p>Validates the Bundle, to ensure that a malicious application isn't attempting to pass
     * an invalid JSON object.</p>
     *
     * @param jsonObject The plug-in's JSON previously returned by the edit
     *                   Activity.  {@code jsonObject} should not be mutated by this method.
     * @return true if {@code jsonObject} is valid.
     */
    boolean isJsonValid(@NonNull final JSONObject jsonObject);

    /**
     * Plug-in Activity lifecycle callback to allow the Activity to restore
     * state for editing a previously saved plug-in instance. This callback will
     * occur during the onPostCreate() phase of the Activity lifecycle.
     * <p>{@code bundle} will have been validated by {@link #isJsonValid(JSONObject)} prior to this
     * method being called.  If {@link #isJsonValid(JSONObject)} returned false, then this method
     * will not be called.  This helps ensure that plug-in Activity subclasses only have to
     * worry about bundle validation once, in the {@link #isJsonValid(JSONObject)} method.</p>
     * <p>Note this callback only occurs the first time the Activity is created, so it will not be
     * called when the Activity is recreated (e.g. {@code savedInstanceState != null}) such as after
     * a configuration change like a screen rotation.</p>
     *
     * @param previousJsonObject Previous JSON object that the Activity saved.
     * @param previousBlurb      Previous blurb that the Activity saved
     */
    void onPostCreateWithPreviousResult(
            @NonNull final JSONObject previousJsonObject, @NonNull final String previousBlurb);

    /**
     * @return Result for the plug-in or {@code null} which indicates that the plug-in doesn't have
     * anything to save.
     */
    @Nullable
    JSONObject getResultJson();

    /**
     * @param jsonObject Valid JSON for the plug-in instance.
     * @return Blurb for {@code jsonObject}.
     */
    @NonNull
    String getResultBlurb(@NonNull final JSONObject jsonObject);

}
