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

package com.twofortyfouram.memento.util;

import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.Size;
import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.*;

@ThreadSafe
@RequiresApi(Build.VERSION_CODES.O)
public final class QueryBundleBuilder {

    /**
     * @param bundle Input bundle to mutate.
     * @param selection Args to associate with {@link ContentResolver#QUERY_ARG_SQL_SELECTION}.
     * @param selectionArgs Args to associate with {@link ContentResolver#QUERY_ARG_SQL_SELECTION_ARGS}.
     * @return Mutated input {@code bundle} for chained calls.
     */
    @NonNull
    public static Bundle withSelection(@NonNull final Bundle bundle, @NonNull final String selection, @NonNull final String[] selectionArgs) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertNotNull(selection, "selection"); //$NON-NLS
        assertNotNull(selectionArgs, "selectionArgs"); //$NON-NLS

        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);

        return bundle;
    }

    /**
     * @param bundle Input bundle to mutate.
     * @param limit limit to associate with {@link ContentResolver#QUERY_ARG_LIMIT}.
     * @return Mutated input {@code bundle} for chained calls.
     */
    @NonNull
    public static Bundle withLimit(@NonNull final Bundle bundle, @NonNull @IntRange(from = 1) final int limit) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertInRangeInclusive(limit, 1, Integer.MAX_VALUE, "limit"); //$NON-NLS

        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);

        return bundle;
    }

    /**
     * @param bundle Input bundle to mutate.
     * @param offset offset to associate with {@link ContentResolver#QUERY_ARG_OFFSET}.
     * @param limit limit to associate with {@link ContentResolver#QUERY_ARG_LIMIT}.
     * @return Mutated input {@code bundle} for chained calls.
     */
    @NonNull
    public static Bundle withOffsetAndLimit(@NonNull final Bundle bundle, @NonNull @IntRange(from = 1) final int offset, @NonNull @IntRange(from = 1) final int limit) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertInRangeInclusive(offset, 1, Integer.MAX_VALUE, "offset"); //$NON-NLS
        assertInRangeInclusive(limit, 1, Integer.MAX_VALUE, "limit"); //$NON-NLS

        bundle.putInt(ContentResolver.QUERY_ARG_OFFSET, offset);
        bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);

        return bundle;
    }


    /**
     * Note: Deletes {@link ContentResolver#QUERY_ARG_SQL_SORT_ORDER} if it is present.
     *
     * @param bundle Input bundle to mutate.
     * @param columns columns to associate with {@link ContentResolver#QUERY_ARG_SORT_COLUMNS}.
     * @param direction direction to associate with {@link ContentResolver#QUERY_ARG_SORT_DIRECTION}.
     * @return Mutated input {@code bundle} for chained calls.
     */
    @NonNull
    public static Bundle withSortColumnsAndDirection(@NonNull final Bundle bundle, @NonNull @Size(min = 1) final String[] columns, @NonNull final int direction) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertNotNull(columns, "columns"); //$NON-NLS
        assertNotEmpty(columns, "columns"); //$NON-NLS
        assertNoNullElements(columns, "columns"); //$NON-NLS
        assertInRangeInclusive(direction, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING, "direction"); //$NON-NLS

        bundle.remove(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);

        bundle.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, columns);
        bundle.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, direction);

        return bundle;
    }

    /**
     * Note: Deletes {@link ContentResolver#QUERY_ARG_SORT_COLUMNS}, {@link ContentResolver#QUERY_ARG_SORT_DIRECTION}, and {@link ContentResolver#QUERY_ARG_SORT_COLLATION} if they are present.
     *
     * @param bundle Input bundle to mutate.
     * @param sortSql offset to associate with {@link ContentResolver#QUERY_ARG_SQL_SORT_ORDER}.
     * @return Mutated input {@code bundle} for chained calls.
     */
    @NonNull
    public static Bundle withSortSql(@NonNull final Bundle bundle, @NonNull @Size(min = 1) final String sortSql) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertNotNull(sortSql, "sortSql"); //$NON-NLS
        assertNotEmpty(sortSql, "sortSql"); //$NON-NLS

        bundle.remove(ContentResolver.QUERY_ARG_SORT_COLUMNS);
        bundle.remove(ContentResolver.QUERY_ARG_SORT_DIRECTION);
        bundle.remove(ContentResolver.QUERY_ARG_SORT_COLLATION);

        bundle.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortSql);

        return bundle;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private QueryBundleBuilder() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
