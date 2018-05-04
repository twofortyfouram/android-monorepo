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

import android.support.annotation.NonNull;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import net.jcip.annotations.ThreadSafe;

/**
 * Contains Intent constants necessary for interacting with the plug-in API for Locale version 2.0.
 *
 * Version 2.0 of the plug-in API uses ContentProvider call methods to query
 * conditions and fire settings, while version 1.0 uses explicit ordered Broadcasts.
 *
 * @see PluginConditionContract
 * @see PluginSettingContract
 */
@ThreadSafe
public final class LocalePluginIntentV2 {

    /**
     * <p>{@code Intent} action sent by the host to create or
     * edit a plug-in condition. When the host sends this {@code Intent}, it
     * will be explicit (i.e. sent directly to the package and class of the plug-in's
     * {@code Activity}).</p>
     * <p>The {@code Intent} MAY contain
     * {@link #EXTRA_BUNDLE} and {@link #EXTRA_STRING_BLURB} that was previously set by the {@code
     * Activity} result of ACTION_EDIT_CONDITION.</p>
     * <p>There SHOULD be only one {@code Activity} per APK that implements this
     * {@code Intent}. If a single APK wishes to export multiple plug-ins, it
     * MAY implement multiple Activity instances that implement this
     * {@code Intent}, however there must only be a single
     * {@link PluginConditionContract#ACTION_QUERY_CONDITION} ContentProvider. In such a scenario, it is the
     * responsibility of the Activity to store enough data in
     * {@link #EXTRA_BUNDLE} to allow the ContentProvider to disambiguate which
     * "plug-in" is being queried. To avoid user confusion, it is recommended
     * that only a single plug-in be implemented per APK.</p>
     */
    @NonNull
    public static final String ACTION_EDIT_CONDITION
            = LocalePluginIntentV1.ACTION_EDIT_CONDITION;

    /**
     * <p>
     * {@code Intent} action sent by the host to create or
     * edit a plug-in setting. When the host sends this {@code Intent}, it
     * will be explicit (i.e. sent directly to the package and class of the plug-in's
     * {@code Activity}).</p>
     * <p>The {@code Intent} MAY contain a {@link #EXTRA_BUNDLE} and {@link
     * #EXTRA_STRING_BLURB}
     * that was previously set by the {@code Activity} result of
     * ACTION_EDIT_SETTING.</p>
     * <p>
     * There SHOULD be only one {@code Activity} per APK that implements this
     * {@code Intent}. If a single APK wishes to export multiple plug-ins, it
     * MAY implement multiple Activity instances that implement this
     * {@code Intent}, however there must only be a single
     * {@link PluginSettingContract#ACTION_FIRE_SETTING} ContentProvider. In such a scenario, it is the
     * responsibility of the Activity to store enough data in
     * {@link #EXTRA_BUNDLE} to allow this ContentProvider to disambiguate which
     * "plug-in" is being fired. To avoid user confusion, it is recommended that
     * only a single plug-in be implemented per APK.
     * </p>
     */
    @NonNull
    public static final String ACTION_EDIT_SETTING
            = LocalePluginIntentV1.ACTION_EDIT_SETTING;

    /**
     * @see LocalePluginIntentV1#EXTRA_STRING_BLURB
     */
    @NonNull
    public static final String EXTRA_STRING_BLURB = LocalePluginIntentV1.EXTRA_STRING_BLURB;

    /**
     * @see LocalePluginIntentV1#EXTRA_BUNDLE
     * @see #EXTRA_STRING_JSON
     */
    @NonNull
    public static final String EXTRA_BUNDLE = LocalePluginIntentV1.EXTRA_BUNDLE;

    /**
     * @see LocalePluginIntentV1#EXTRA_BUNDLE
     */
    @NonNull
    public static final String EXTRA_STRING_JSON = LocalePluginIntentV1.EXTRA_STRING_JSON;

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private LocalePluginIntentV2() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
