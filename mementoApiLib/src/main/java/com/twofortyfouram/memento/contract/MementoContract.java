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

package com.twofortyfouram.memento.contract;

import android.app.SearchManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;

@ThreadSafe
public interface MementoContract {

    /**
     * Optional boolean query string argument to suppress content change notifications.
     */
    @NonNull
    String QUERY_STRING_IS_SUPPRESS_NOTIFICATION
            = "com.twofortyfouram.memento.is_suppress_notification"; //$NON-NLS

    /**
     * Mutates the query string to include {@link MementoContract#QUERY_STRING_IS_SUPPRESS_NOTIFICATION}
     * set to true.
     *
     * Calling this method multiple times on the same builder has undefined behavior.
     *
     * @param builder Builder to mutate
     * @return Same object as {@code builder}, mutated with the query string parameter.
     */
    @NonNull
    static Uri.Builder addSuppressNotification(@NonNull final Uri.Builder builder) {
        //noinspection CallToNumericToString
        return builder
                .appendQueryParameter(QUERY_STRING_IS_SUPPRESS_NOTIFICATION,
                        Boolean.TRUE.toString());
    }

    /**
     * Mutates the query string to include {@link SearchManager#SUGGEST_PARAMETER_LIMIT} set to
     * {@code limit}. For Android O and greater, consider using {@link
     * android.content.ContentResolver#query(Uri, String[], Bundle, CancellationSignal)} with a
     * limit in the Bundle.
     *
     * Calling this method multiple times on the same builder has undefined behavior.
     *
     * @param builder Builder to mutate.
     * @return Same object as {@code builder}, mutated with the query string parameter.
     */
    @NonNull
    @Deprecated
    static Uri.Builder addLimit(@NonNull final Uri.Builder builder,
            @IntRange(from = 1) final int limit) {
        assertInRangeInclusive(limit, 1, Integer.MAX_VALUE, "limit"); //$NON-NLS

        //noinspection CallToNumericToString
        return builder.appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT,
                Integer.toString(limit));
    }
}
