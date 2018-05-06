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

package com.twofortyfouram.locale.sdk.host.test.setting.bundle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.twofortyfouram.spackle.AppBuildInfo;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
public final class PluginJsonValues {

    /**
     * Type: {@code String}
     * <p>
     * Action string that the plug-in should broadcast.
     */
    private static final String STRING_ACTION_TO_FIRE = "action_to_fire"; //$NON-NLS-1$

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
     * Method to verify the content of the jsonObject are correct.
     * <p>
     * This method will not mutate {@code jsonObject}.
     *
     * @param jsonObject jsonObject to verify. May be null, which will always return false.
     * @return true if the Bundle is valid, false if the jsonObject is invalid.
     */
    public static boolean isJsonValid(@Nullable final JSONObject jsonObject) {
        if (null == jsonObject) {
            return false;
        }

        if (2 != jsonObject.length()) {
            return false;
        }

        if (jsonObject.isNull(STRING_ACTION_TO_FIRE)) {
            return false;
        }

        final String actionToFire;
        try {
            actionToFire = jsonObject.getString(STRING_ACTION_TO_FIRE);
        } catch (final JSONException e) {
            return false;
        }

        if (TextUtils.isEmpty(actionToFire)) {
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
     * @param context      Application context.
     * @param actionToFire The Intent action to broadcast.
     * @return A plug-in bundle.
     */
    @NonNull
    public static JSONObject generateJson(@NonNull final Context context,
            @NonNull final String actionToFire) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(actionToFire, "actionToFire"); //$NON-NLS-1$

        final JSONObject result = new JSONObject();
        try {
            result.put(INT_VERSION_CODE, AppBuildInfo.getVersionCode(context));
            result.put(STRING_ACTION_TO_FIRE, actionToFire);
        } catch (final JSONException e) {
            throw new AssertionError(e);
        }

        return result;
    }

    /**
     * @param jsonObject A valid plug-in jsonObject.
     * @return The action to fire.
     */
    @NonNull
    public static String getActionToFire(@NonNull final JSONObject jsonObject) {
        return jsonObject.optString(STRING_ACTION_TO_FIRE);
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
