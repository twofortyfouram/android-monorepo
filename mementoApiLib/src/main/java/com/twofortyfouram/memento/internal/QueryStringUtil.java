/*
 * android-memento
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

package com.twofortyfouram.memento.internal;

import android.app.SearchManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.spackle.AndroidSdkVersion;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class QueryStringUtil {

    /**
     * @param uri To test.
     * @return True if {@code uri} contains the query string parameter
     * {@link MementoContract#QUERY_STRING_IS_SUPPRESS_NOTIFICATION} set to true.
     */
    public static boolean isSuppressNotification(@NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS

        return getQueryStringBoolean(uri,
                MementoContract.QUERY_STRING_IS_SUPPRESS_NOTIFICATION, false);
    }

    /**
     * For Android O and greater, consider using {@link android.content.ContentResolver#query(Uri,
     * String[], Bundle, CancellationSignal)} with a limit in the Bundle.
     *
     * @param uri URI to check for {@link SearchManager#SUGGEST_PARAMETER_LIMIT}.
     * @return String representing the integer of the query string limit.  If a non-parsable value
     * or an out of bounds value, this method will return null.
     */
    @Nullable
    public static String getLimit(@NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS

        final String queryStringParam = getQueryString(uri, SearchManager.SUGGEST_PARAMETER_LIMIT);

        if (null != queryStringParam) {
            try {
                final int intParam = Integer.parseInt(queryStringParam);

                if (intParam < 1) {
                    Lumberjack.e("%d is not a valid positive int", intParam); //$NON-NLS
                    return null;
                }
            } catch (final NumberFormatException e) {
                Lumberjack.e("%s is not a valid int", queryStringParam); //$NON-NLS

                return null;
            }
        }

        return queryStringParam;
    }

    private static boolean getQueryStringBoolean(@NonNull final Uri uri, @NonNull final String key,
            final boolean defaultValue) {

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            return uri.getBooleanQueryParameter(key, defaultValue);
        } else {
            return Boolean.parseBoolean(uri.getQueryParameter(key));
        }
    }

    @Nullable
    private static String getQueryString(@NonNull final Uri uri, @NonNull final String key) {
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            final String param = uri.getQueryParameter(key);

            return param;
        } else {
            return null;
        }
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private QueryStringUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
