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

package com.twofortyfouram.locale.sdk.host.test.condition.bundle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.twofortyfouram.locale.annotation.ConditionResult;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.spackle.AppBuildInfo;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
public final class PluginJsonValues {

    /**
     * Type: {@code int}
     * <p>
     * An extra that contains the result code that the debug plug-in condition should return when
     * queried.
     *
     * @see LocalePluginIntent#RESULT_CONDITION_SATISFIED
     * @see LocalePluginIntent#RESULT_CONDITION_UNSATISFIED
     * @see LocalePluginIntent#RESULT_CONDITION_UNKNOWN
     */
    @NonNull
    public static final String INT_RESULT_CODE = "result_code"; //$NON-NLS-1$

    /**
     * Type: {@code int}.
     * <p>
     * versionCode of the plug-in that saved the JSON.
     */
    /*
     * This extra is not strictly required, however it makes backward and forward compatibility
     * significantly easier. For example, suppose a bug is found in how some version of the plug-in
     * stored its Bundle. By having the version, the plug-in can better detect when such bugs occur.
     */
    @NonNull
    public static final String INT_VERSION_CODE = "version_code"; //$NON-NLS-1$

    /**
     * Method to verify the content of the json are correct.
     * <p>
     * This method will not mutate {@code json}.
     *
     * @param json bundle to verify. May be null, which will always return false.
     * @return true if the json is valid, false if the json is invalid.
     */
    public static boolean isJsonValid(@Nullable final JSONObject json) {
        if (null == json) {
            return false;
        }

        if (null == json) {
            return false;
        }

        if (2 != json.length()) {
            return false;
        }

        if (json.isNull(INT_RESULT_CODE)) {
            return false;
        }

        int resultCode = 0;
        try {
            resultCode = json.getInt(INT_RESULT_CODE);
        } catch (final JSONException e) {
            return false;
        }

        try {
            assertInRangeInclusive(resultCode,
                    LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN,
                    "resultCode"); //$NON-NLS-1$
        } catch (final AssertionError e) {
            return false;
        }

        if (json.isNull(INT_VERSION_CODE)) {
            return false;
        }

        int versionCode = 0;
        try {
            versionCode = json.getInt(INT_VERSION_CODE);
        } catch (final JSONException e) {
            return false;
        }

        return true;
    }

    /**
     * @param context    Application context.
     * @param resultCode The result code the plug-in should respond with when queried.
     * @return A plug-in JSON object.
     */
    @NonNull
    public static JSONObject generateJson(@NonNull final Context context,
            @ConditionResult final int resultCode) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertInRangeInclusive(resultCode,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN,
                "resultCode"); //$NON-NLS-1$

        final JSONObject json = new JSONObject();
        try {
            json.put(INT_VERSION_CODE, AppBuildInfo.getVersionCode(context));
            json.put(INT_RESULT_CODE, resultCode);
        } catch (final JSONException e) {
            throw new AssertionError(e);
        }

        return json;
    }

    /**
     * @param json A valid plug-in bundle.
     * @return The result code inside the plug-in json.  Will return
     * {@link LocalePluginIntent#RESULT_CONDITION_UNKNOWN} if the result code does not exist in
     * {@code json}.
     */
    @ConditionResult
    public static int getResultCode(@NonNull final JSONObject json) {
        @ConditionResult
        final int resultCode = json
                .optInt(INT_RESULT_CODE, LocalePluginIntent.RESULT_CONDITION_UNKNOWN);

        assertInRangeInclusive(resultCode,
                LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN,
                "resultCode"); //$NON-NLS-1$

        return resultCode;
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
