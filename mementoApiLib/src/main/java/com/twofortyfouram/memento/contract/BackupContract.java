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
public final class BackupContract {

    /**
     * Method supported by the {@link ContentProvider#call(String,
     * String, Bundle)} interface for performing backup of database.
     * This method restricted to being performed within the same package as the content provider.
     *
     * @see #RESULT_EXTRA_BOOLEAN_IS_SUCCESS
     * @see #backup(Context, Uri, String)
     */
    @NonNull
    public static final String METHOD_BACKUP
            = "com.twofortyfouram.memento.method.backup"; //$NON-NLS

    /**
     * Result of database backup operation.
     */
    @NonNull
    public static final String RESULT_EXTRA_BOOLEAN_IS_SUCCESS
            = "com.twofortyfouram.memento.extra.BOOLEAN_IS_SUCCESS"; //$NON-NLS

    /**
     * Backups entire database into a file provided by filePath.
     *
     * This method restricted to being performed within the same package as the content provider.
     *
     * @param filePath A file path where the database file will be copied.
     *                 File extension should be ".sqlite"
     */
    @Slow(Slow.Speed.MILLISECONDS)
    public static boolean backup(@NonNull final Context context,
            @NonNull final Uri authority,
            @NonNull final String filePath) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotNull(filePath, "filePath"); //$NON-NLS

        final Context ctx = ContextUtil.cleanContext(context);

        @Nullable final Bundle result = ctx.getContentResolver()
                .call(authority, METHOD_BACKUP, filePath, null);

        return null != result && result.getBoolean(RESULT_EXTRA_BOOLEAN_IS_SUCCESS);
    }

    private BackupContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
