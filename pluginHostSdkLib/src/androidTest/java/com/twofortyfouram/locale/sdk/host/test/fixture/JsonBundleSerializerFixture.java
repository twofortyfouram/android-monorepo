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

package com.twofortyfouram.locale.sdk.host.test.fixture;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.twofortyfouram.locale.api.LocalePluginIntent;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONObject;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.TESTS)
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public final class JsonBundleSerializerFixture {

    @NonNull
    private static final String EMPTY_JSON_OBJECT = new JSONObject().toString();

    @NonNull
    public static Bundle newEmptySerializableBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, EMPTY_JSON_OBJECT);

        return bundle;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private JsonBundleSerializerFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
