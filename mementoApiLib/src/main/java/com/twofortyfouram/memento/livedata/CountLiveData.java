/*
 * android-memento
 * https://github.com/twofortyfouram/android-monorepo
 * Copyright (C) 2008–2019 two forty four a.m. LLC
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.*;
import androidx.lifecycle.LiveData;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.memento.api.BuildConfig;
import com.twofortyfouram.memento.contract.BaseColumnsContract;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.NotThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Queries on a background thread and emits a count.
 */
@NotThreadSafe
@Incubating
public final class CountLiveData extends LiveData<Integer> {

    @NonNull
    private final Context mContext;

    private final boolean mIsAsync;

    @NonNull
    private final Uri mUri;

    @Nullable
    private final String mSelection;

    @Nullable
    private final String[] mSelectionArgs;

    @Nullable
    private ContentObserver mContentObserver = null;

    @Nullable
    private AsyncTask<Void, Void, Integer> mAsyncTask = null;

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public CountLiveData(@NonNull final Context context, final boolean isAsync,
                         @NonNull final Uri uri,
                         @Nullable final String selection,
                         @Nullable final String[] selectionArgs) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(uri, "uri"); //$NON-NLS

        mContext = ContextUtil.cleanContext(context);

        mIsAsync = isAsync;

        mUri = uri;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
    }

    public CountLiveData(@NonNull final Context context,
                         @NonNull final Uri uri,
                         @NonNull final String selection,
                         @NonNull final String[] selectionArgs) {
        this(context, true, uri, assertNotNull(selection, "selection"), //$NON-NLS
                assertNotNull(selectionArgs, "selectionArgs")); //$NON-NLS
    }

    public CountLiveData(@NonNull final Context context,
                         @NonNull final Uri uri) {
        this(context, true, uri, null, null);
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

    @Nullable
    @WorkerThread
    private Integer doInBackground() {

        if (null == mSelection || null == mSelectionArgs) {
            return BaseColumnsContract.getCountForUri(mContext.getContentResolver(), mUri);
        }
        else {
            return BaseColumnsContract.getCountForUri(mContext.getContentResolver(), mUri, mSelection, mSelectionArgs);
        }
    }

    @MainThread
    private void onPostExecute(@NonNull final Integer count) {
        setValue(count);
    }

    @NotThreadSafe
    @MainThread
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
    private final class LoadAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(final Void... voids) {
            return CountLiveData.this.doInBackground();
        }

        @Override
        protected void onPostExecute(@NonNull final Integer count) {
            CountLiveData.this.onPostExecute(count);
        }
    }

}
