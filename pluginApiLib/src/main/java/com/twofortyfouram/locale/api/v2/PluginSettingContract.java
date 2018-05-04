/*
 * android-plugin-api-for-locale
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

package com.twofortyfouram.locale.api.v2;

import android.content.ContentProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * ContentProvider contract implemented by version 2.0 of the Locale Plug-in API for plug-in settings.
 * The provider implementing this contract must export an Intent filter for
 * {@link #ACTION_FIRE_SETTING}.
 */
public interface PluginSettingContract {

    /**
     * <p>A ContentProvider implementing an Intent filter for this action and {@link PluginSettingContract}
     * will be called to fire a plug-in setting instance.</p>
     * <p>There must be only one {@code ContentProvider} per APK that implements an Intent-filter
     * for this action.
     * </p>
     *
     * @see LocalePluginIntentV2#EXTRA_BUNDLE
     */
    @NonNull
    String ACTION_FIRE_SETTING
            = "com.twofortyfouram.locale.intent.action.FIRE_SETTING"; //$NON-NLS-1$

    /**
     * If {@link LocalePluginIntentV2#EXTRA_BUNDLE} contained
     * {@link LocalePluginIntentV2#EXTRA_STRING_JSON} pointing to a String, then the String will be
     * extracted and passed as the second argument.
     *
     * Parameter Bundle:
     *
     * Result Bundle:
     *
     * @see ContentProvider#call(String, String, Bundle)
     */
    @NonNull
    String METHOD_FIRE_SETTING = "com.twofortyfouram.locale.method.fire_setting"; //$NON-NLS
}
