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
import androidx.annotation.*;
import androidx.lifecycle.LiveData;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.memento.api.BuildConfig;
import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.memento.util.CursorParser;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Queries on a background thread and emits a collection of objects.  Note this class is best used for small
 * queries rather than large ones.  It is highly recommended to use a limit or query arguments to reduce the number
 * of results returned.
 *
 * @param <T>
 */
@NotThreadSafe
@Incubating
public final class QueryLiveData<T> extends LiveData<List<T>> {

    @NonNull
    private final Context mContext;

    private final boolean mIsAsync;

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
    private final Integer mLimit;

    @Nullable
    private ContentObserver mContentObserver = null;

    @Nullable
    private AsyncTask<Void, Void, List<T>> mAsyncTask = null;

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public QueryLiveData(@NonNull final Context context, final boolean isAsync,
                         @NonNull final CursorParser<T> cursorParser,
                         @NonNull final Uri uri,
                         @Nullable final String[] projection,
                         @Nullable final String selection,
                         @Nullable final String[] selectionArgs,
                         @Nullable final String orderBy, @Nullable final Integer limit) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(cursorParser, "providerParser"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS

        mContext = ContextUtil.cleanContext(context);

        mCursorParser = cursorParser;

        mIsAsync = isAsync;

        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mOrderBy = orderBy;
        mLimit = limit;
    }

    public QueryLiveData(@NonNull final Context context, @NonNull final CursorParser<T> cursorParser,
                         @NonNull final Uri uri,
                         @Nullable final String[] projection,
                         @Nullable final String selection,
                         @Nullable final String[] selectionArgs,
                         @Nullable final String orderBy, @Nullable final Integer limit) {
        this(context, true, cursorParser, uri, projection, selection, selectionArgs, orderBy, limit);
    }

    @Override
    protected void onActive() {
        super.onActive();

        try {
            mContentObserver = new ContentObserverImpl(new Handler());
            mContext.getContentResolver().registerContentObserver(mUri, true, mContentObserver);
        } catch (final SecurityException e) {
            if (BuildConfig.DEBUG) {
                // Eat exception for automated tests
                mContentObserver = null;
            } else {
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
        }
    }

    private void loadData() {
        if (mIsAsync) {
            mAsyncTask = new LoadAsyncTask().execute();
        }
        else {
            onPostExecute(doInBackground());
        }
    }

    @NonNull
    @WorkerThread
    private List<T> doInBackground() {
        @NonNull final Uri uri;
        if (null == mLimit) {
            uri = mUri;
        }
        else {
            uri = MementoContract.addLimit(mUri.buildUpon(), mLimit).build();
        }

        try (@Nullable final Cursor cursor = mContext.getContentResolver().query(uri, mProjection, mSelection, mSelectionArgs, mOrderBy)) {
            if (null != cursor) {

                @NonNull final List<T> items = new ArrayList<>(cursor.getCount());
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    items.add(mCursorParser.newObject(cursor));
                }

                return Collections.unmodifiableList(items);
            }
        }

        return Collections.emptyList();
    }

    private void onPostExecute(@NonNull final List<T> list) {
        setValue(list);
    }

    private final class ContentObserverImpl extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public ContentObserverImpl(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            loadData();
        }
    }

    @NotThreadSafe
    private final class LoadAsyncTask extends AsyncTask<Void, Void, List<T>> {

        @Override
        protected List<T> doInBackground(final Void... voids) {
            return QueryLiveData.this.doInBackground();
        }

        @Override
        protected void onPostExecute(@NonNull final List<T> list) {
            QueryLiveData.this.onPostExecute(list);
        }
    }

}
