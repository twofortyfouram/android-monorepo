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
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import net.jcip.annotations.ThreadSafe;

/**
 * ContentProvider contract implemented by version 2.0 of the Locale Plug-in API for plug-in conditions.
 * The provider implementing this contract must export an Intent filter for
 * {@link #ACTION_QUERY_CONDITION}.
 */
@ThreadSafe
public interface PluginConditionContract {
    /**
     * <p>A ContentProvider implementing this Intent filter signals that it implements {@link PluginConditionContract}.
     * Such a ContentProvider will be called by the host to query a plug-in condition instance.</p>
     * <p>There must be only one {@code ContentProvider} per APK that implements an Intent-filter
     * for this action.
     * </p>
     *
     * @see LocalePluginIntentV2#EXTRA_BUNDLE
     */
    @NonNull
    String ACTION_QUERY_CONDITION
            = LocalePluginIntentV1.ACTION_QUERY_CONDITION;

    /**
     * Result code indicating that a plug-in condition's state
     * is satisfied (true).
     */
    int RESULT_CONDITION_SATISFIED = 16;

    /**
     * Result code indicating that a plug-in condition's state
     * is not satisfied (false).
     */
    int RESULT_CONDITION_UNSATISFIED = 17;

    /**
     * <p>
     * Result code indicating that a plug-in condition's state
     * is unknown (neither true nor false).
     * </p>
     * <p>
     * If a condition returns UNKNOWN, then the host will use the last known
     * return value on a best-effort basis. Best-effort means that the host may
     * not persist known values forever (e.g. last known values could
     * hypothetically be cleared after a device reboot or a restart of the
     * host's process). If there is no last known return value, then unknown is
     * treated as not satisfied (false).
     * </p>
     * <p>
     * The purpose of an UNKNOWN result is to allow a plug-in condition more time to process a query without resetting
     * the state of the condition in the host.
     * </p>
     */
    int RESULT_CONDITION_UNKNOWN = 18;

    /**
     * If {@link LocalePluginIntentV2#EXTRA_BUNDLE} contained
     * {@link LocalePluginIntentV2#EXTRA_STRING_JSON} pointing to a String, then the String will be
     * extracted and passed as the second argument.
     *
     * Parameter Bundle: Contains {@link #EXTRA_INT_RESULT} of the previous state of the condition being queried,
     * allowing the plug-in to use previous state for debouncing.  Other keys in the com.twofortyfouram namespace are
     * reserved for future use.
     *
     * Result Bundle: a Bundle containing {@link #EXTRA_INT_RESULT}.  Other keys in the com.twofortyfouram namespace are
     * reserved for future use.
     *
     * @see ContentProvider#call(String, String, Bundle)
     */
    @NonNull
    String METHOD_QUERY_CONDITION = "com.twofortyfouram.locale.method.query_condition"; //$NON-NLS

    /**
     * Bundle extra returned by {@link #METHOD_QUERY_CONDITION}.  Must be one of {@link #RESULT_CONDITION_SATISFIED},
     * {@link #RESULT_CONDITION_UNSATISFIED}, or {@link #RESULT_CONDITION_UNKNOWN}.
     */
    @NonNull
    String EXTRA_INT_RESULT = "com.twofortyfouram.locale.intent.extra.INT_RESULT"; //$NON-NLS
}
