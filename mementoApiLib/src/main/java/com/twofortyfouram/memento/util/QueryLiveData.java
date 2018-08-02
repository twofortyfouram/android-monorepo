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

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.api.BuildConfig;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Queries on a background thread and emits a collection of objects.  Note this class is best used for small
 * queries rather than large ones.
 *
 * @param <T>
 */
@NotThreadSafe
@Incubating
public final class QueryLiveData<T> extends LiveData<Collection<T>> {

    @NonNull
    private final Context mContext;

    private boolean mIsAsync;

    @NonNull
    private final CursorParser<T> mCursorParser;

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

    @Nullable
    private ContentObserver mContentObserver = null;

    @Nullable
    private AsyncTask<Void, Void, Collection<T>> mAsyncTask;

    @VisibleForTesting
        /*package*/ QueryLiveData(@NonNull final Context context, final boolean isAsync,
                                  @NonNull final CursorParser<T> cursorParser,
                                  @NonNull final Uri uri,
                                  @Nullable final String[] projection,
                                  @Nullable final String selection,
                                  @Nullable final String[] selectionArgs,
                                  @Nullable final String orderBy) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(cursorParser, "providerParser"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS

        mContext = ContextUtil.cleanContext(context);
        mIsAsync = isAsync;
        mCursorParser = cursorParser;

        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mOrderBy = orderBy;
    }

    public QueryLiveData(@NonNull final Context context, @NonNull final CursorParser<T> cursorParser,
                         @NonNull final Uri uri,
                         @Nullable final String[] projection,
                         @Nullable final String selection,
                         @Nullable final String[] selectionArgs,
                         @Nullable final String orderBy) {
        this(context, true, cursorParser, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    protected void onActive() {
        super.onActive();

        mContentObserver = new ContentObserverImpl(new Handler());
        try {
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

        // TODO: Is it standard to null out the value here?
    }

    private void loadData() {
        if (mIsAsync) {
            // Don't cancel the other async task, as the Cursor still needs to be closed.
            mAsyncTask = new QueryAsyncTask();
            mAsyncTask.execute();
        } else {
            // Kludge to make testing possible.  Might be able to refactor in the future with InstantTaskExecutorRule
            onPostExecute(doInBackground());
        }
    }

    private final class ContentObserverImpl extends ContentObserver {

        public ContentObserverImpl(@NonNull final Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(final boolean selfChange, @NonNull final Uri uri) {
            super.onChange(selfChange, uri);

            Lumberjack.v("uri=%s changed"); //$NON-NLS

            if (mContentObserver != this) {
                return;
            }

            loadData();
        }
    }

    @Nullable
    @WorkerThread
    private Collection<T> doInBackground() {
        try (@Nullable final Cursor cursor = mContext.getContentResolver().query(mUri, mProjection, mSelection, mSelectionArgs, mOrderBy)) {
            if (null != cursor) {
                @NonNull final Collection<T> items = new ArrayList<>(cursor.getCount());
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    items.add(mCursorParser.newObject(cursor));
                }

                return Collections.unmodifiableCollection(items);
            }
        }

        return Collections.emptyList();
    }

    private void onPostExecute(@Nullable final Collection<T> parsedItems) {
        setValue(parsedItems);
    }

    @NotThreadSafe
    private final class QueryAsyncTask extends AsyncTask<Void, Void, Collection<T>> {

        @Override
        protected Collection<T> doInBackground(final Void... voids) {
            return doInBackground();
        }

        @Override
        protected void onPostExecute(@Nullable final Collection<T> items) {
            QueryLiveData.this.onPostExecute(items);
        }
    }

    @ThreadSafe
    public interface CursorParser<T> {
        /**
         * Extracts an object from a Cursor.  This method assumes that the Cursor contains all the columns of the
         * contract and that the Cursor is positioned to a row that is ready to be read. This method will not mutate
         * the Cursor or move the Cursor position.
         *
         * @param cursor Cursor from a query to a contract this parser can handle.
         * @return a new Object.
         * @throws AssertionError If the cursor is closed or the cursor is out of range.
         */
        @NonNull
        @WorkerThread
        T newObject(@NonNull final Cursor cursor);
    }
}
