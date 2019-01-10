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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Utility class for working with {@link ContentProvider} contracts implementing {@link BaseColumns}.
 */
@ThreadSafe
public final class BaseColumnsContract {

    /**
     * Projection for {@link BaseColumns#_COUNT}.
     */
    @NonNull
    private static final String[] PROJECTION_COUNT = {BaseColumns._COUNT};

    @NonNull
    public static final String SORT_ORDER_ASCENDING_BY_ID = BaseColumns._ID + " ASC"; //$NON-NLS

    @NonNull
    public static final String SORT_ORDER_DESCENDING_BY_ID = BaseColumns._ID + " DESC"; //$NON-NLS

    /**
     * This method should work for any content provider that correctly supports {@link
     * BaseColumns#_COUNT}.
     *
     * @param resolver Content resolver.
     * @param uri      URI to count.
     * @return The number of rows for {@code uri}.
     * @throws NullPointerException          If the content provider returned a null cursor.
     *                                       Memento will not cause this exception, but other
     *                                       providers might.
     * @throws UnsupportedOperationException If the content provider doesn't support querying for
     *                                       count, either by returning too many rows or by not
     *                                       returning a valid _COUNT column in the result cursor.
     */
    @Slow(Speed.MILLISECONDS)
    public static int getCountForUri(@NonNull final ContentResolver resolver,
                                     @NonNull final Uri uri) {
        assertNotNull(resolver, "resolver"); //$NON-NLS-1$
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        return getCountForUri(resolver, uri, null, null);
    }

    /**
     * This method should work for any content provider that correctly supports {@link
     * BaseColumns#_COUNT}.
     *
     * @param resolver Content resolver.
     * @param uri      URI to count.
     * @return The number of rows for {@code uri}.
     * @throws NullPointerException          If the content provider returned a null cursor.
     *                                       Memento will not cause this exception, but other
     *                                       providers might.
     * @throws UnsupportedOperationException If the content provider doesn't support querying for
     *                                       count, either by returning too many rows or by not
     *                                       returning a valid _COUNT column in the result cursor.
     */
    @Slow(Speed.MILLISECONDS)
    public static int getCountForUri(@NonNull final ContentResolver resolver,
                                     @NonNull final Uri uri, @Nullable final String selection,
                                     @Nullable String[] selectionArgs) {
        assertNotNull(resolver, "resolver"); //$NON-NLS-1$
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        int result = 0;

        try (@Nullable final Cursor cursor = resolver
                .query(uri, PROJECTION_COUNT, selection, selectionArgs, null)) {
            if (null != cursor) {
                final int cursorCount = cursor.getCount();

                if (1 != cursorCount) {
                    final String message = Lumberjack.formatMessage(
                            "Row count should be 1 but was actually %d", cursorCount); //$NON-NLS-1$
                    throw new UnsupportedOperationException(message);
                }

                cursor.moveToPosition(0);

                final int columnIndex;
                try {
                    columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._COUNT);
                } catch (final IllegalArgumentException e) {
                    throw new UnsupportedOperationException(e);
                }

                if (Cursor.FIELD_TYPE_NULL == cursor.getType(columnIndex)) {
                    throw new UnsupportedOperationException("Count value was null"); //$NON-NLS
                }

                try {
                    result = cursor.getInt(columnIndex);
                } catch (final NumberFormatException e) {
                    throw new UnsupportedOperationException(e);
                }
            } else {
                throw new NullPointerException("Content Provider returned null cursor"); //$NON-NLS
            }
        }

        return result;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private BaseColumnsContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
