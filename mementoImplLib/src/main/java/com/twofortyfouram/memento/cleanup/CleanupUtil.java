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

package com.twofortyfouram.memento.cleanup;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.*;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.annotation.NotMultiProcessSafe;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.memento.util.QueryBundleBuilder;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.assertion.CursorAssertions.assertCursorOpen;
import static com.twofortyfouram.assertion.CursorAssertions.assertCursorPositionValid;


@Incubating
@ThreadSafe
@NotMultiProcessSafe
public final class CleanupUtil {

    @NonNull
    private static final String SELECTION_FOR_KEY_FORMAT = "%s = ?"; //$NON-NLS

    @NonNull
    private static final String SELECTION_FOR_KEY_AND_VERSION_FORMAT = "%s = ? AND %s <= ?"; //$NON-NLS

    @NonNull
    private static final String VERSION_SORT_ORDER_DESC_FORMAT = "%s DESC"; //$NON-NLS

    /**
     * Useful to clean up history.  Note that after the operations are performed, the history may not contain exactly
     * {@code maxRecordsToKeep} because more entries could have been inserted between the time the operations were
     * generated and executed.
     *
     * @param context Application context.
     * @param historyUri URI that can contain the key multiple times.
     * @param keyUri URI containing each key exactly once.  For example, could be a view created by {@link com.twofortyfouram.memento.model.SqliteLatestViewBuilder}.
     * @param keyColumn Column of the key.  Key may be text or numeric, although it will be cast as text during the execution of this method.
     * @param versionColumn Column of the version in {@code historyUri}, where for a given key version is always increasing.  An autoincrement primary key or epoch timestamp would be examples of valid versions.
     * @param maxRecordsToKeep Maximum number of historical records to keep.
     * @return ArrayList of operations to perform that will clean up historical records.
     */
    @NonNull
    @Size(min = 0)
    public static ArrayList<ContentProviderOperation> getDeleteOps(@NonNull final Context context, @NonNull final Uri historyUri, @NonNull final Uri keyUri, @NonNull final String keyColumn, @NonNull final String versionColumn, @IntRange(from = 1) final int maxRecordsToKeep) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(historyUri, "historyUri"); //$NON-NLS
        assertNotNull(keyUri, "keyUri"); //$NON-NLS
        assertNotNull(keyColumn, "keyColumn"); //$NON-NLS
        assertNotNull(versionColumn, "versionColumn"); //$NON-NLS
        assertInRangeInclusive(maxRecordsToKeep, 1, Integer.MAX_VALUE, "maxRecordsToKeep"); //$NON-NLS

        @NonNull final List<ContentProviderOperation> ops = new LinkedList<>();
        // Iterate through the child uri, which will have unique keys compared to the parent uri
        try (@Nullable final Cursor cursor = context.getContentResolver().query(keyUri, new String[]{keyColumn}, null, null, null)) {
            if (null != cursor) {
                final int cursorCount = cursor.getCount();
                if (0 < cursorCount) {
                    final int keyColumnIndex = cursor.getColumnIndexOrThrow(keyColumn);
                    cursor.moveToPosition(-1);

                    @NonNull final String selectionForKey = String.format(Locale.US, SELECTION_FOR_KEY_FORMAT, keyColumn);
                    @NonNull final String sortOrder = String.format(Locale.US, VERSION_SORT_ORDER_DESC_FORMAT, versionColumn);
                    @NonNull final String selectionForKeyAndVersion = String.format(Locale.US, SELECTION_FOR_KEY_AND_VERSION_FORMAT, keyColumn, versionColumn);
                    while (cursor.moveToNext()) {
                        @NonNull final String key = cursor.getString(keyColumnIndex);

                        @Nullable final ContentProviderOperation deleteOperation = getDeleteOpsForOldValuesForKey(context, historyUri, keyColumn, selectionForKey, selectionForKeyAndVersion, versionColumn, sortOrder, key, maxRecordsToKeep);

                        if (null != deleteOperation) {
                            ops.add(deleteOperation);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(ops);
    }

    /**
     * @param context          Application context.
     * @param key              Key to delete old values of.
     * @param maxRecordsToKeep Maximum number of records to keep for {@code key}.
     */
    @Slow(Slow.Speed.MILLISECONDS)
    @Nullable
    private static ContentProviderOperation getDeleteOpsForOldValuesForKey(@NonNull final Context context, @NonNull final Uri uri, @NonNull final String keyColumn, @NonNull final String selectionForKey, @NonNull final String selectionForKeyAndVersion, @NonNull final String versionColumn, @NonNull final String sortOrder, @NonNull final String key, @IntRange(from = 1) final int maxRecordsToKeep) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS
        assertNotNull(keyColumn, "keyColumn"); //$NON-NLS
        assertNotNull(versionColumn, "versionColumn"); //$NON-NLS
        assertNotNull(selectionForKey, "selectionForKey"); //$NON-NLS
        assertNotNull(selectionForKeyAndVersion, "selectionForKeyAndVersion"); //$NON-NLS
        assertNotNull(key, "key"); //$NON-NLS
        assertInRangeInclusive(maxRecordsToKeep, 1, Integer.MAX_VALUE, "maxRecordsToKeep"); //$NON-NLS

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.O)) {
            return getDeleteOpsForOldValuesForKeyOreoPlus(context, uri, selectionForKey, selectionForKeyAndVersion, versionColumn, sortOrder, key, maxRecordsToKeep);
        } else {
            return getDeleteOpsForOldValuesForKeyLegacy(context, uri, selectionForKey, selectionForKeyAndVersion, versionColumn, sortOrder, key, maxRecordsToKeep);
        }
    }

    /**
     * @param context          Application context.
     * @param key              Key to delete old values of.
     * @param maxRecordsToKeep Maximum number of records to keep for {@code key}.
     */
    @Slow(Slow.Speed.MILLISECONDS)
    @Nullable
    @RequiresApi(Build.VERSION_CODES.O)
    private static ContentProviderOperation getDeleteOpsForOldValuesForKeyOreoPlus(@NonNull final Context context, @NonNull final Uri uri, @NonNull final String selectionForKey, @NonNull final String selectionForKeyAndVersion, @NonNull final String versionColumn, @NonNull final String sortOrder, @NonNull final String key, @IntRange(from = 1) final int maxRecordsToKeep) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS
        assertNotNull(selectionForKey, "selectionForKey"); //$NON-NLS
        assertNotNull(selectionForKeyAndVersion, "selectionForKeyAndVersion"); //$NON-NLS
        assertNotNull(versionColumn, "versionColumn"); //$NON-NLS
        assertNotNull(key, "key"); //$NON-NLS
        assertInRangeInclusive(maxRecordsToKeep, 1, Integer.MAX_VALUE, "maxRecordsToKeep"); //$NON-NLS

        /*
         * Offset and limit allows for a significant optimization, only returning the single row we're looking for.
         */

        @NonNull final Bundle queryArgs = new Bundle();
        QueryBundleBuilder.withSelection(queryArgs, selectionForKey, new String[]{key});
        QueryBundleBuilder.withSortSql(queryArgs, sortOrder);
        QueryBundleBuilder.withOffsetAndLimit(queryArgs, maxRecordsToKeep, 1);

        try (@Nullable final Cursor cursor = context.getContentResolver().query(uri, new String[]{versionColumn}, queryArgs, null)) {
            if (null != cursor) {
                cursor.moveToPosition(-1);
                if (cursor.moveToFirst()) {
                    return newContentProviderOperation(cursor, uri, selectionForKeyAndVersion, versionColumn, key);
                }
            }
        }

        return null;
    }

    /**
     * @param context          Application context.
     * @param key              Key to delete old values of.
     * @param maxRecordsToKeep Maximum number of records to keep for {@code key}.
     */
    @Slow(Slow.Speed.MILLISECONDS)
    @Nullable
    private static ContentProviderOperation getDeleteOpsForOldValuesForKeyLegacy(@NonNull final Context context, @NonNull final Uri uri, @NonNull final String selectionForKey, @NonNull final String selectionForKeyAndVersion, @NonNull final String versionColumn, @NonNull final String sortOrder, @NonNull final String key, @IntRange(from = 1) final int maxRecordsToKeep) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(key, "key"); //$NON-NLS
        assertNotNull(selectionForKey, "selectionForKey"); //$NON-NLS
        assertNotNull(selectionForKeyAndVersion, "selectionForKeyAndVersion"); //$NON-NLS
        assertInRangeInclusive(maxRecordsToKeep, 1, Integer.MAX_VALUE, "maxRecordsToKeep"); //$NON-NLS

        @NonNull final String[] projection = new String[]{versionColumn};
        @NonNull final String[] selectionArgs = {key};

        try (@Nullable final Cursor cursor = context.getContentResolver().query(MementoContract.addLimit(uri.buildUpon(), maxRecordsToKeep + 1).build(), projection, selectionForKey, selectionArgs, sortOrder)) {
            if (null != cursor) {
                cursor.moveToPosition(-1);
                if (cursor.moveToPosition(maxRecordsToKeep)) {
                    return newContentProviderOperation(cursor, uri, selectionForKeyAndVersion, versionColumn, key);
                }
            }
        }

        return null;
    }

    @NonNull
    private static ContentProviderOperation newContentProviderOperation(@NonNull final Cursor cursor, @NonNull final Uri uri, @NonNull final String selectionForKeyAndVersion, @NonNull final String versionColumn, @NonNull final String key) {
        assertNotNull(cursor, "cursor"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS
        assertNotNull(selectionForKeyAndVersion, "selectionForKeyAndVersion"); //$NON-NLS
        assertNotNull(versionColumn, "versionColumn"); //$NON-NLS
        assertNotNull(key, "key"); //$NON-NLS
        assertCursorOpen(cursor);
        assertCursorPositionValid(cursor);

        final int versionIndex = cursor.getColumnIndexOrThrow(versionColumn);
        final long version = cursor.getLong(versionIndex);

        return ContentProviderOperation.newDelete(uri).withSelection(selectionForKeyAndVersion, new String[]{key, Long.toString(version)}).build();
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private CleanupUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
