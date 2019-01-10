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

package com.twofortyfouram.memento.test.main_process.contract;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.twofortyfouram.annotation.MultiProcessSafe;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderUtil;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
@MultiProcessSafe
public final class KeyValueContract implements TestKeyValueColumns {

    /**
     * Name of the table.
     */
    @NonNull
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static final String TABLE_NAME = "string_value"; //$NON-NLS-1$

    /**
     * Mimetype for the entire directory.
     */
    @NonNull
    public static final String MIMETYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE
            + ContentProviderUtil.MIME_PROVIDER_NAME_PART + ".keyvalue"; //$NON-NLS

    /**
     * Mimetype for a single item.
     */
    @NonNull
    public static final String MIMETYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + ContentProviderUtil.MIME_PROVIDER_NAME_PART + ".keyvalue"; //$NON-NLS

    /**
     * Intrinsic lock for guarding {@link #sContentUri}.
     */
    @NonNull
    private static final Object INTRINSIC_LOCK = new Object();

    /**
     * Content URI for {@link KeyValueContract}.
     *
     * @see #getContentUri(Context)
     */
    @GuardedBy("INTRINSIC_LOCK")
    @Nullable
    @SuppressWarnings("StaticNonFinalField")
    private static volatile Uri sContentUri = null;

    /**
     * @param context Application context.
     * @return The content URI for {@link KeyValueContract}.
     */
    @NonNull
    public static Uri getContentUri(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd edition page 283.
         */
        @SuppressWarnings("FieldAccessNotGuarded")
        @Nullable Uri contentUri = sContentUri;
        if (null == contentUri) {
            //noinspection SynchronizationOnStaticField
            synchronized (INTRINSIC_LOCK) {
                contentUri = sContentUri;
                if (null == contentUri) {
                    @NonNull final String authority = ContentProviderUtil.getContentAuthorityString(context);
                    sContentUri = contentUri = new Uri.Builder()
                            .scheme(ContentResolver.SCHEME_CONTENT).authority(authority)
                            .appendPath(TABLE_NAME).build();
                }
            }
        }

        return contentUri;
    }

    private KeyValueContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
