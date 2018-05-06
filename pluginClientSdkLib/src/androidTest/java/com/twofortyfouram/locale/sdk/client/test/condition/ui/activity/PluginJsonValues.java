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

package com.twofortyfouram.locale.sdk.client.test.condition.ui.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.spackle.AppBuildInfo;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Class for managing the {@link LocalePluginIntent#EXTRA_STRING_JSON} for this
 * plug-in.
 */
@ThreadSafe
public final class PluginJsonValues {

    /**
     * Type: {@code String}
     * <p>
     * An extra that maps to an arbitrary String value.
     */
    @NonNull
    private static final String STRING_VALUE = "value";//$NON-NLS-1$

    /**
     * Type: {@code int}.
     * <p>
     * versionCode of the plug-in that saved the JSON.
     */
    /*
     * This extra is not strictly required, however it makes backward and forward compatibility
     * significantly easier. For example, suppose a bug is found in how some version of the plug-in
     * stored its JSON. By having the version, the plug-in can better detect when such bugs occur.
     */
    @NonNull
    private static final String INT_VERSION_CODE = "version_code";//$NON-NLS-1$

    /**
     * Method to verify the content of the JSON object is correct.
     * <p>
     * This method will not mutate {@code jsonObject}.
     *
     * @param jsonObject JSON object to verify. May be null, which will always return false.
     * @return true if the JSON is valid, false if the bundle is invalid.
     */
    public static boolean isJsonValid(@Nullable final JSONObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        if (2 != jsonObject.length()) {
            return false;
        }

        if (jsonObject.isNull(STRING_VALUE)) {
            return false;
        }

        String value = null;
        try {
            value = jsonObject.getString(STRING_VALUE);
        } catch (final JSONException e) {
            return false;
        }

        if (TextUtils.isEmpty(value)) {
            return false;
        }


        if (jsonObject.isNull(INT_VERSION_CODE)) {
            return false;
        }

        int versionCode = 0;
        try {
            versionCode = jsonObject.getInt(INT_VERSION_CODE);
        } catch (final JSONException e) {
            return false;
        }

        return true;
    }

    /**
     * @param context Application context.
     * @param value   The value stored in the plug-in JSON.
     * @return A plug-in bundle.
     */
    @NonNull
    public static JSONObject generateJson(@NonNull final Context context,
            @NonNull final String value) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(value, "value"); //$NON-NLS-1$

        final JSONObject result = new JSONObject();
        try {
            result.put(INT_VERSION_CODE, AppBuildInfo.getVersionCode(context));
            result.put(STRING_VALUE, value);

            return result;
        } catch (final JSONException e) {
            //A failure creating the JSON object isn't expected.
            throw new RuntimeException(e);
        }
    }

    /**
     * @param jsonObject A valid plug-in jsonObject.
     * @return The value inside the plug-in jsonObject.
     */
    @NonNull
    public static String getValue(@NonNull final JSONObject jsonObject) {
        try {
            return jsonObject.getString(STRING_VALUE);
        }
        catch (final JSONException e) {
            // Users are expected to validate with isValid() first
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginJsonValues() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
