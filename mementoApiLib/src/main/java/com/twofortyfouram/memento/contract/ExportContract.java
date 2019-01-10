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
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
@Incubating
public final class ExportContract {

    /**
     * Method supported by the {@link ContentProvider#call(String,
     * String, Bundle)} interface for performing export of a database.
     * This method restricted to being performed within the same package as the content provider.  The arg is a
     * writable file path where the export will be placed as a ZIP.  The database and any -wal or -journal files will
     * be copied.
     * <p>
     * Note this method doesn't work if the database is in-memory (null filename).
     *
     * @see #RESULT_EXTRA_BOOLEAN_IS_SUCCESS
     * @see #callExport(Context, Uri, String)
     */
    @NonNull
    public static final String METHOD_EXPORT
            = "com.twofortyfouram.memento.method.EXPORT"; //$NON-NLS

    /**
     * Result of database export operation.
     */
    @NonNull
    public static final String RESULT_EXTRA_BOOLEAN_IS_SUCCESS
            = "com.twofortyfouram.memento.extra.BOOLEAN_IS_SUCCESS"; //$NON-NLS

    @NonNull
    public static final String WAL_SUFFIX = "-wal"; //$NON-NLS

    @NonNull
    public static final String JOURNAL_SUFFIX = "-journal"; //$NON-NLS

    /**
     * Exports entire database into a ZIP file to be written to {@code destinationPath}.  Exports are done to a ZIP so
     * that -wal and -journal files can also be copied, yet end up contained in a single file.
     * <p>
     * This method restricted to being performed within the same package as the content provider.
     * <p>
     * Note this method doesn't work if the database is in-memory (null filename).
     *
     * @param destinationPath A writable file path where the database file will be copied, along with any -wal and
     *                        -journal files.
     */
    @Slow(Slow.Speed.MILLISECONDS)
    public static boolean callExport(@NonNull final Context context,
                                     @NonNull final Uri authority,
                                     @NonNull final String destinationPath) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotNull(destinationPath, "destinationPath"); //$NON-NLS

        @NonNull final Context ctx = ContextUtil.cleanContext(context);

        @Nullable final Bundle result = ctx.getContentResolver()
                .call(authority, METHOD_EXPORT, destinationPath, null);

        return null != result && result.getBoolean(RESULT_EXTRA_BOOLEAN_IS_SUCCESS);
    }

    private ExportContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
