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

package com.twofortyfouram.memento.livedata;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.api.BuildConfig;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.NotThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Queries on a background thread and emits a Cursor.
 * <p>
 * Note that clients should always reset the cursor position, as it may not be at position -1 due to prior usage.
 * Clients also shouldn't hold onto the cursor, as it can be closed.  Also note that multiple clients probably shouldn't
 * be trying to use the same CursorLiveData at the same time.
 * </p>
 * <p>
 * Note this class doesn't deal with paging.
 * </p>
 */
@NotThreadSafe
@Incubating
public final class CursorLiveData extends LiveData<Cursor> {

    @NonNull
    private final Context mContext;

    private final boolean mIsAsync;

    @NonNull
    private final Uri mUri;

    @Nullable
    private final String[] mProjection;

    @Nullable
    private final String mSelection;

    @Nullable
    private final String[] mSelectionArgs;

    @Nullable
    private final String mOrderBy;

    private boolean mIsLoadedYet = false;

    @Nullable
    private ContentObserver mContentObserver = null;

    @Nullable
    private CursorAsyncTask mAsyncTask = null;

    /**
     * Constructor for testing to force synchronous behavior.
     *
     * @param context
     * @param isAsync
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param orderBy
     */
    public CursorLiveData(@NonNull final Context context,
                               @Nullable final boolean isAsync,
                               @NonNull final Uri uri,
                               @Nullable final String[] projection,
                               @Nullable final String selection,
                               @Nullable final String[] selectionArgs,
                               @Nullable final String orderBy) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS

        mContext = ContextUtil.cleanContext(context);
        mIsAsync = isAsync;

        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mOrderBy = orderBy;
    }

    /**
     *
     * @param context Application context.
     * @param uri Uri to query.
     * @param projection Columns to return.
     * @param selection Optional selection arguments.
     * @param selectionArgs Optional arguments for {@code selection}.
     * @param orderBy Optional orderby.
     */
    public CursorLiveData(@NonNull final Context context,
                          @NonNull final Uri uri,
                          @Nullable final String[] projection,
                          @Nullable final String selection,
                          @Nullable final String[] selectionArgs,
                          @Nullable final String orderBy) {
        this(context, true, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    protected void onActive() {
        super.onActive();

        try {
            mContentObserver = new ContentObserverImpl(new Handler());
            mContext.getContentResolver().registerContentObserver(mUri, true, mContentObserver);
        }
        catch (final SecurityException e) {
            if (BuildConfig.DEBUG) {
                // Eat exception for automated tests
                mContentObserver = null;
            }
            else {
                throw e;
            }
        }

        loadData();
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        if (null != mContentObserver) {
            mContext.getContentResolver().unregisterContentObserver(mContentObserver);
            mContentObserver = null;
        }

        @Nullable final Cursor cursor = getValue();
        if (null != cursor) {
            cursor.close();
            setValue(null);
        }

        mIsLoadedYet = false;
    }

    private void loadData() {
        if (mIsAsync) {
            // Don't cancel the other async task, as the Cursor still needs to be closed.
            mAsyncTask = new CursorAsyncTask();
            mAsyncTask.execute();
        } else {
            // Kludge to make testing possible.  Might be able to refactor in the future with InstantTaskExecutorRule
            onPostExecute(doInBackground());
        }
    }

    /**
     * @return True if the data is actually loaded from the source (versus null by initial value).
     */
    public boolean isLoadedYet() {
        return mIsLoadedYet;
    }

    private final class ContentObserverImpl extends ContentObserver {

        public ContentObserverImpl(@NonNull final Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(final boolean selfChange, @NonNull final Uri uri) {
            super.onChange(selfChange, uri);

            Lumberjack.v("uri=%s changed", uri); //$NON-NLS

            if (mContentObserver != this) {
                return;
            }

            loadData();
        }
    }

    @Nullable
    @WorkerThread
    private Cursor doInBackground() {
        @Nullable final Cursor cursor = mContext.getContentResolver().query(mUri, mProjection, mSelection, mSelectionArgs, mOrderBy);

        if (null != cursor) {
            // Apparently fills cursor window?
            cursor.getCount();
        }

        return cursor;
    }

    private void onPostExecute(@Nullable final Cursor cursor) {
        mIsLoadedYet = true;

        @Nullable final Cursor oldValue = getValue();
        if (null != oldValue) {
            oldValue.close();
        }

        setValue(cursor);
    }

    @NotThreadSafe
    private final class CursorAsyncTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(final Void... voids) {
            return CursorLiveData.this.doInBackground();
        }

        @Override
        protected void onPostExecute(@Nullable final Cursor cursor) {
            CursorLiveData.this.onPostExecute(cursor);
        }
    }

}
