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

package com.twofortyfouram.locale.sdk.client.test.condition.receiver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.twofortyfouram.locale.api.v1.annotation.ConditionResult;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import com.twofortyfouram.spackle.AppBuildInfo;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Class for managing the {@link LocalePluginIntentV1#EXTRA_BUNDLE} for this
 * plug-in.
 */
@ThreadSafe
public final class PluginJsonValues {

    /**
     * Type: {@code int}
     * <p>
     * An extra that contains the result code that the test plug-in condition should return when
     * queried.
     *
     * @see LocalePluginIntentV1#RESULT_CONDITION_SATISFIED
     * @see LocalePluginIntentV1#RESULT_CONDITION_UNSATISFIED
     * @see LocalePluginIntentV1#RESULT_CONDITION_UNKNOWN
     */
    private static final String INT_RESULT_CODE = "result_code"; //$NON-NLS-1$

    /**
     * Type: {@code int}.
     * <p>
     * versionCode of the plug-in that saved the Bundle.
     */
    /*
     * This extra is not strictly required, however it makes backward and forward compatibility
     * significantly easier. For example, suppose a bug is found in how some version of the plug-in
     * stored its Bundle. By having the version, the plug-in can better detect when such bugs occur.
     */
    @NonNull
    private static final String INT_VERSION_CODE = "version_code"; //$NON-NLS-1$

    /**
     * Method to verify the content of the JSON are correct.
     * <p>
     * This method will not mutate {@code jsonObject}.
     *
     * @param jsonObject JSON to verify. May be null, which will always return false.
     * @return true if the JSON is valid, false if the JSON is invalid.
     */
    public static boolean isJsonValid(@Nullable final JSONObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        if (2 != jsonObject.length()) {
            return false;
        }

        if (jsonObject.isNull(INT_RESULT_CODE)) {
            return false;
        }

        {
            int resultCode = 0;
            try {
                resultCode = jsonObject.getInt(INT_RESULT_CODE);
            } catch (final JSONException e) {
                return false;
            }
        }

        {
            if (jsonObject.isNull(INT_VERSION_CODE)) {
                return false;
            }

            int versionCode = 0;
            try {
                versionCode = jsonObject.getInt(INT_VERSION_CODE);
            } catch (final JSONException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param context    Application context.
     * @param resultCode The result code the plug-in should respond with when queried.
     * @return A plug-in bundle.
     */
    @NonNull
    public static JSONObject generateJson(@NonNull final Context context, final int resultCode) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertInRangeInclusive(resultCode,
                LocalePluginIntentV1.RESULT_CONDITION_SATISFIED,
                LocalePluginIntentV1.RESULT_CONDITION_UNKNOWN,
                "resultCode"); //$NON-NLS-1$

        final JSONObject json = new JSONObject();
        try {
            json.put(INT_VERSION_CODE, AppBuildInfo.getVersionCode(context));
        }
        catch (final JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            json.put(INT_RESULT_CODE, resultCode);
        }
        catch (final JSONException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    /**
     * @param json A valid plug-in JSON object.
     * @return The result code inside the plug-in JSON.
     */
    @ConditionResult
    public static int getResultCode(@NonNull final JSONObject json) {
        try {
            @ConditionResult
            final int result = json.getInt(INT_RESULT_CODE);

            return result;
        } catch (final JSONException e) {
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
