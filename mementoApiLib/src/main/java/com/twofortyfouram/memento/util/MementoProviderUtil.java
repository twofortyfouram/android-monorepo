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

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.internal.ContentProviderClientCompat;
import com.twofortyfouram.memento.internal.TransactionAware;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.log.Lumberjack.formatMessage;

/**
 * Utility class for working with {@link ContentProvider}.
 */
@ThreadSafe
public final class MementoProviderUtil {

    /*
     * Hard coded class name from the test package to enable unit testing of runInTransaction()
     */
    @NonNull
    @VisibleForTesting
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static final String MOCKABLE_CONTENT_PROVIDER_CLASS_NAME
            = "com.twofortyfouram.test.provider.MockableContentProvider"; //$NON-NLS

    /**
     * Projection for {@link BaseColumns#_COUNT}.
     */
    @NonNull
    private static final String[] PROJECTION_COUNT = {
            BaseColumns._COUNT
    };

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
     * This can only be used for a ContentProvider that subclasses {@link TransactionAware}.
     *
     * Note: This method is not safe for multi-process use.  This method can only be called from
     * the same process and package as the ContentProvider is running in.
     *
     * @param context          Application context.
     * @param contentAuthority Authority of the Content Provider.
     * @param transactable     Transactable to execute.
     * @return The result of {@code transactable}.
     * @throws IllegalArgumentException      If provider for {@code contentAuthority} is not found.
     * @throws UnsupportedOperationException If provider for {@code contentAuthority} is not an
     *                                       instance of {@link TransactionAware}.
     */
    /*
     * TODO: It might be possible to make this work across process boundaries within the same
     * package by using the call API and restricting the transactable to being a static, parcelable object.
     */
    @Nullable
    @Incubating
    public static <V> V runInTransaction(@NonNull final Context context,
            @NonNull final String contentAuthority,
            @NonNull final Transactable<V> transactable) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(contentAuthority, "contentAuthority"); //$NON-NLS-1$
        assertNotNull(transactable, "transactable"); //$NON-NLS-1$

        @Nullable V result = null;

        // Don't use try with resources until minSdk = N or greater, because it didn't become
        // autoclosable until recently.
        @Nullable ContentProviderClient client = null;
        try {
            client = context.getContentResolver().acquireContentProviderClient(contentAuthority);
            @Nullable final ContentProvider provider = client.getLocalContentProvider();

            if (null == provider) {
                throw new IllegalArgumentException(
                        formatMessage("No provider found for authority %s", //$NON-NLS-1$
                                contentAuthority)
                );
            }

            if (provider instanceof TransactionAware) {
                //noinspection CastToConcreteClass
                final TransactionAware mementoContentProvider
                        = (TransactionAware) provider;
                result = mementoContentProvider.runInTransaction(transactable);
            } else {
                if (MOCKABLE_CONTENT_PROVIDER_CLASS_NAME
                        .equals(provider.getClass().getName())) {
                    // Special case for automated tests
                    result = transactable.runInTransaction();
                } else {
                    throw new UnsupportedOperationException(
                            formatMessage(
                                    "Provider with authority %s is not an instance of %s",//NON-NLS
                                    contentAuthority,
                                    TransactionAware.class.getName()));
                }
            }
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);
                //noinspection UnusedAssignment
                client = null;
            }
        }

        return result;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private MementoProviderUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
