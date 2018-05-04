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


import android.os.Bundle;
import android.support.annotation.NonNull;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import net.jcip.annotations.ThreadSafe;

/**
 * ContentProvider contract implemented by hosts for version 2.0 of the Plug-in API for Locale.
 */
@ThreadSafe
public interface HostContract {

    /**
     * <p>A ContentProvider implementing an Intent filter for this action and {@link HostContract} will be called to
     * notify a host that the plug-in's ContentProvider implementing
     * {@link PluginConditionContract#ACTION_QUERY_CONDITION} wishes to be queried.</p>
     * <p>
     * This merely serves as a hint to the host that a condition wants to be queried. There is no guarantee as to when or
     * if the plug-in will be queried. If the host does not respond to the plug-in condition after a requery request, the plug-in SHOULD shut
     * itself down and stop requesting requeries. A lack of response from the host
     * indicates that the host is not currently interested in this plug-in. When
     * the host becomes interested in the plug-in again, the host will query it again.</p>
     * <p>
     * Plug-in conditions SHOULD NOT use this unless there is some sort of
     * asynchronous event that has occurred, such as a broadcast {@code Intent}
     * being received by the plug-in. Plug-ins SHOULD NOT periodically request a
     * requery as a way of implementing polling behavior.
     * </p>
     * <p>
     * Hosts MAY throttle plug-ins that request queries too frequently.
     * </p>
     */
    @NonNull
    String ACTION_REQUEST_QUERY
            = LocalePluginIntentV1.ACTION_REQUEST_QUERY;

    /**
     * ContentProvider method for {@link android.content.ContentProvider#call(String, String, Bundle)}.
     *
     * The argument string is the Activity class name of the plug-in.  If a plug-in package implements multiple
     * conditions, this argument allows the host to disambiguate which condition is requesting a requery.
     *
     * The Bundle argument is currently unused.  Within the Bundle, the com.twofortyfouram namespace for keys is
     * reserved for future use.
     */
    // TODO: Specify whether the Bundle is non-null?
    @NonNull
    String METHOD_REQUEST_QUERY = "com.twofortyfouram.locale.method.request_query"; //$NON-NLS
}
