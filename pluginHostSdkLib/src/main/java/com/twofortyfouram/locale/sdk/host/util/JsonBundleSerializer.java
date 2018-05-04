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

package com.twofortyfouram.locale.sdk.host.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * For plug-ins implementing the Plug-in API for Locale 1.1.0.
 */
@ThreadSafe
public final class JsonBundleSerializer implements BundleSerializer<JSONObject> {

    /**
     * @return True if {@code bundle} contains exactly one extra, {@link LocalePluginIntentV1#EXTRA_STRING_JSON},
     * and
     * that extra can be serialized as a JSON Object.
     */
    public boolean isSerializable(@NonNull final Bundle bundle) {
        assertNotNull(bundle, "bundle"); //$NON-NLS

        if (1 != bundle.size()) {
            return false;
        }

        try {
            BundleAssertions.assertHasString(bundle, LocalePluginIntentV1.EXTRA_STRING_JSON, false, false);
        } catch (final AssertionError e) {
            return false;
        }

        final String extra = bundle.getString(LocalePluginIntentV1.EXTRA_STRING_JSON);

        try {
            new JSONObject(extra);
        } catch (final JSONException e) {
            return false;
        }

        return true;
    }

    /**
     * @param bundle Contains {@link LocalePluginIntentV1#EXTRA_STRING_JSON}.
     * @return {@link LocalePluginIntentV1#EXTRA_STRING_JSON}.
     */
    @NonNull
    @Override
    public JSONObject serialize(@NonNull final Bundle bundle) throws BundleSerializationException {
        assertNotNull(bundle, "bundle"); //$NON-NLS

        final String result = bundle.getString(LocalePluginIntentV1.EXTRA_STRING_JSON, null);

        if (null == result) {
            throw new BundleSerializationException(Lumberjack
                    .formatMessage("bundle does not contain extra %s", LocalePluginIntentV1.EXTRA_STRING_JSON));
        }

        try {
            return new JSONObject(result);
        } catch (final JSONException e) {
            throw new BundleSerializationException(e);
        }
    }

    /**
     * @param json Result previously returned by {@link #serialize(Bundle)}.
     * @return A new {@code Bundle} containing {@code json} in {@link LocalePluginIntentV1#EXTRA_STRING_JSON}.
     */
    @NonNull
    @Override
    public Bundle deserialize(@NonNull final JSONObject json) throws BundleSerializationException {
        assertNotNull(json, "json"); //$NON-NLS

        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntentV1.EXTRA_STRING_JSON, json.toString());

        return bundle;
    }
}
