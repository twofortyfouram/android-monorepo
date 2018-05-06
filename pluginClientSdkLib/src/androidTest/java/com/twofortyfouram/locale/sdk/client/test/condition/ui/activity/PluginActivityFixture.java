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


import android.content.Intent;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.client.ui.activity.PluginType;

import net.jcip.annotations.Immutable;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@Immutable
public final class PluginActivityFixture {

    /**
     * @param type Plug-in type.
     * @return The default Intent to start the plug-in Activity. The Intent will
     * contain
     * {@link LocalePluginIntent#EXTRA_STRING_BREADCRUMB}
     * .
     */
    @NonNull
    public static Intent getDefaultStartIntent(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        final Intent activityIntentAction = new Intent(type.getActivityIntentAction());

        activityIntentAction.putExtra(LocalePluginIntent.EXTRA_STRING_BREADCRUMB,
                "Edit Situation"); //$NON-NLS-1$

        return activityIntentAction;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be
     *                                       instantiated.
     */
    private PluginActivityFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
