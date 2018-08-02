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

package com.twofortyfouram.memento.test.main_process;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Contract for a simple ContentProvider table.
 */
@ThreadSafe
public final class TableOneContract implements BaseColumns {

    /**
     * Name of the table.
     */
    @NonNull
    /* package */ static final String TABLE_NAME = "table_one"; //$NON-NLS-1$

    /**
     * Mimetype for the entire directory.
     */
    @NonNull
    public static final String MIMETYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.com.twofortyfouram.memento.test.blabla"; //$NON-NLS-1$

    /**
     * Mimetype for a single item.
     */
    @NonNull
    public static final String MIMETYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.twofortyfouram.memento.test.blabla"; //$NON-NLS-1$

    /**
     * Type: {@code String}.
     * <p>
     * First column in the table.
     * <p>
     * Constraints: This column cannot be null.
     */
    @NonNull
    public static final String COLUMN_STRING_COLUMN_ONE = "column_one"; //$NON-NLS-1$

    /**
     * Intrinsic lock for guarding {@link #sContentUri}.
     */
    @NonNull
    private static final Object INTRINSIC_LOCK = new Object();

    /**
     * Content URI for {@link TableOneContract}.
     *
     * @see #getContentUri(Context)
     */
    @GuardedBy("INTRINSIC_LOCK")
    @Nullable
    @SuppressWarnings("StaticNonFinalField")
    private static volatile Uri sContentUri = null;

    /**
     * @param context Application context.
     * @return The content URI for {@link TableOneContract}.
     */
    @NonNull
    public static Uri getContentUri(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd edition page 283.
         */
        @SuppressWarnings("FieldAccessNotGuarded")
        Uri contentUri = sContentUri;
        if (null == contentUri) {
            //noinspection SynchronizationOnStaticField
            synchronized (INTRINSIC_LOCK) {
                contentUri = sContentUri;
                if (null == contentUri) {
                    final String authority = ContentProviderImpl.getContentAuthority(context);
                    sContentUri = contentUri = new Uri.Builder()
                            .scheme(ContentResolver.SCHEME_CONTENT).authority(authority)
                            .appendPath(TABLE_NAME).build();
                }
            }
        }

        return contentUri;
    }

    /**
     * Creates ContentValues for the table.
     *
     * @param columnOne String to associate with {@link #COLUMN_STRING_COLUMN_ONE}.
     * @return Initialized ContentValues.
     */
    @NonNull
    public static ContentValues getContentValues(@NonNull final String columnOne) {
        assertNotNull(columnOne, "columnOne"); //$NON-NLS-1$

        final ContentValues values = new ContentValues(1);
        values.put(COLUMN_STRING_COLUMN_ONE, columnOne);

        return values;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private TableOneContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
