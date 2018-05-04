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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.ContextUtil;

import net.jcip.annotations.NotThreadSafe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * <p>Tracks what changed in a ContentProvider.  Once a change is committed,
 * notifications are sent out via the ContentResolver and {@link Intent#ACTION_PROVIDER_CHANGED}.
 * </p>
 * A queue operates in one of two modes: batch or non-batch.  In batch mode, no content change
 * notifications are sent until {@link #endBatch(boolean)} is called.  If the same URI changes more
 * than once
 * in a batch, the notifications are coalesced.  In non-batch mode,
 * changes
 * are sent as soon as {@link #onContentChanged(android.net.Uri)} is called.
 */
@NotThreadSafe
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ContentChangeNotificationQueue {

    /**
     * Type: {@code int}.
     * <p>
     * Maps to the count of records in the data Uri.
     *
     * @see Intent#ACTION_PROVIDER_CHANGED
     */
    @NonNull
    private static final String EXTRA_COUNT = "count"; //$NON-NLS-1$

    /**
     * Application context.
     */
    @NonNull
    private final Context mContext;

    /**
     * Flag indicating whether the owning content provider is exported.
     */
    private final boolean mIsExported;

    /**
     * Uris that have changed.
     */
    @NonNull
    private final Set<Uri> mUris = new LinkedHashSet<>();

    /**
     * Flag indicating whether a batch transaction is active.
     */
    private boolean mIsBatch = false;

    /**
     * Optional permission required to read the Content Provider.
     */
    @Nullable
    private final String mReadPermission;

    /**
     * @param context        Application context.
     * @param isExported     True if the provider is exported. False if the provider
     *                       is not exported.  If true, {@link Intent#ACTION_PROVIDER_CHANGED}
     *                       broadcasts will be set to be package-only (this only has an effect on
     *                       API 14 or later).
     * @param readPermission Optional read permission of the ContentProvider.  If provided, {@link
     *                       Intent#ACTION_PROVIDER_CHANGED} broadcasts will have this permission
     *                       set.
     */
    public ContentChangeNotificationQueue(@NonNull final Context context, final boolean isExported,
            @Nullable final String readPermission) {
        mContext = ContextUtil.cleanContext(context);
        mIsExported = isExported;
        mReadPermission = readPermission;
    }

    /**
     * @return True if this is currently tracking a batch operation.
     */
    public boolean isBatch() {
        return mIsBatch;
    }

    /**
     * @param uri Uri whose content changed.
     */
    public void onContentChanged(@NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        Lumberjack.v("Content change at %s", uri); //$NON-NLS-1$

        if (mIsBatch) {
            mUris.add(uri);
        } else {
            notifyChange(uri);
        }
    }

    /**
     * @param uris Collection of Uris whose content changed.
     */
    public void onContentChanged(@NonNull final List<Uri> uris) {
        assertNotNull(uris, "uris"); //$NON-NLS-1$

        for (int x = 0; x < uris.size(); x++) {
            onContentChanged(uris.get(x));
        }
    }

    /**
     * Begin a batch transaction.
     *
     * @throws IllegalStateException If a batch has already been started.
     * @see #endBatch(boolean)
     */
    public void startBatch() {
        if (mIsBatch) {
            throw new IllegalStateException("batch has already started"); //$NON-NLS-1$
        }

        mIsBatch = true;
    }

    /**
     * End a batch transaction.
     *
     * @param shouldNotify if true, a change notification should be sent for all
     *                     queued Uris.
     * @throws IllegalStateException If there is no current batch.
     * @see #startBatch()
     */
    public void endBatch(final boolean shouldNotify) {
        if (!mIsBatch) {
            throw new IllegalStateException("batch was not started"); //$NON-NLS-1$
        }

        if (shouldNotify) {
            for (final Uri uri : mUris) {
                Lumberjack.v("Sending content change notification %s", uri); //$NON-NLS-1$
                notifyChange(uri);
            }
        }

        mUris.clear();
        mIsBatch = false;
    }

    private void notifyChange(@NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        final ContentResolver resolver;
        try {
            resolver = mContext.getContentResolver();
        } catch (final UnsupportedOperationException e) {
            /*
             * This happens during unit tests with a mock context.
             */
            return;
        }

        resolver.notifyChange(uri, null);

        try {
            mContext.sendBroadcast(getContentChangeNotificationIntent(uri),
                    mReadPermission);
        } catch (final UnsupportedOperationException e) {
            // This occurs during unit tests on API level 10 and below and API level 21 and above.
        } catch (final RuntimeException e) {
            // Fix for case 15288.
            // Android can sometimes fail to send broadcasts and crash.
        }
    }

    @NonNull
    private Intent getContentChangeNotificationIntent(
            @NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        final Intent intent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
        intent.setDataAndType(uri, mContext.getContentResolver().getType(uri));

        // Disable including the COUNT; it just adds disk IO and clients probably don't care
        //intent.putExtra(EXTRA_COUNT, ContentProviderUtil.getCountForUri(mContext, uri));

        if (!mIsExported) {
            final String packageName;
            try {
                packageName = mContext.getPackageName();
            } catch (final UnsupportedOperationException e) {
                // This occurs during unit tests on API 21 or later.
                return intent;
            }

            intent.setPackage(packageName);
        }

        return intent;
    }
}
