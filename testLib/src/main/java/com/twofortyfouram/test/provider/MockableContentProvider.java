/*
 * android-test
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

package com.twofortyfouram.test.provider;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;

import com.twofortyfouram.test.context.ContentProviderMockContext;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;

import java.util.Collections;
import java.util.LinkedList;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * A content provider whose basic methods of query, insert, update, delete, and call are easily
 * mockable. Note that after instantiation, {@link #attachInfo(Context, ProviderInfo)} must be
 * called
 * manually.  To bypass this limitation, consider using the helper method
 * {@link #newMockProvider(Context, String)}.
 */
// This is a mock object to be used at runtime, so lint warnings about registration should be
// ignored
@SuppressLint("Registered")
@NotThreadSafe
public final class MockableContentProvider extends ContentProvider {

    private boolean mIsAttachInfoCalled = false;

    private int mQueryCount = 0;

    private int mInsertCount = 0;

    private int mUpdateCount = 0;

    private int mDeleteCount = 0;

    private int mCallCount = 0;

    @Nullable
    private final LinkedList<Cursor> mQueryResults = new LinkedList<>();

    // May contain null elements
    @NonNull
    private final LinkedList<Uri> mInsertResults = new LinkedList<>();

    // May not contain null elements.
    @NonNull
    private final LinkedList<Integer> mUpdateResults = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<Integer> mDeleteResults = new LinkedList<>();

    // May contain null elements
    @NonNull
    private final LinkedList<Bundle> mCallResults = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<QueryParams> mQueryParams = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<InsertParams> mInsertParams = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<UpdateParams> mUpdateParams = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<DeleteParams> mDeleteParams = new LinkedList<>();

    // May not contain null elements
    @NonNull
    private final LinkedList<CallParams> mCallParams = new LinkedList<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public void attachInfo(final Context context, @NonNull final ProviderInfo info) {
        super.attachInfo(context, info);

        mIsAttachInfoCalled = true;

        if (null == info.authority) {
            throw new AssertionError("ProviderInfo.authority is null"); //$NON-NLS
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
            @Nullable final String selection, @Nullable final String[]
            selectionArgs,
            @Nullable final String sortOrder) {
        assertAttachInfoCalled();

        mQueryCount++;

        final QueryParams params = new QueryParams(uri, projection, selection, selectionArgs,
                sortOrder);
        mQueryParams.addLast(params);

        final Cursor result = mQueryResults.pollFirst();

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues contentValues) {
        assertAttachInfoCalled();

        mInsertCount++;

        mInsertParams.addLast(new InsertParams(uri, contentValues));

        return mInsertResults.pollFirst();
    }

    @Override
    public int update(@NonNull final Uri uri, @Nullable final ContentValues contentValues,
            @Nullable final String selection, @Nullable final String[] selectionArgs) {
        assertAttachInfoCalled();

        mUpdateCount++;

        mUpdateParams.addLast(new UpdateParams(uri, contentValues, selection, selectionArgs));

        final Integer updateResult = mUpdateResults.pollFirst();

        if (null == updateResult) {
            return 0;
        } else {
            return updateResult;
        }
    }

    @Override
    public int delete(@NonNull final Uri uri, @Nullable final String s,
            @Nullable final String[] strings) {
        assertAttachInfoCalled();

        mDeleteCount++;

        mDeleteParams.addLast(new DeleteParams(uri, s, strings));

        final Integer deleteresult = mDeleteResults.pollFirst();

        if (null == deleteresult) {
            return 0;
        } else {
            return deleteresult;
        }
    }

    @Nullable
    @Override
    public Bundle call(@NonNull final String method, @Nullable final String arg,
            @Nullable final Bundle extras) {
        assertAttachInfoCalled();
        mCallCount++;

        mCallParams.addLast(new CallParams(method, arg, extras));

        return mCallResults.pollFirst();
    }

    /**
     * @param queryResult A result that will be added to the FIFO queue to be consumed and returned
     *                    by a call to {@link
     *                    #query(Uri, String[],
     *                    String,
     *                    String[], String)}.
     */
    public void addQueryResult(@Nullable final Cursor queryResult) {
        mQueryResults.addLast(queryResult);
    }

    /**
     * @param updateResult A result that will be added to the FIFO queue to be consumed and
     *                     returned
     *                     by a call to {@link
     *                     #update(Uri, ContentValues, String, String[])}.
     */
    public void addUpdateResult(final int updateResult) {
        mUpdateResults.addLast(updateResult);
    }

    /**
     * @param insertResult A result that will be added to the FIFO queue to be consumed and
     *                     returned
     *                     by a call to {@link
     *                     #insert(Uri, ContentValues)}.
     */
    public void addInsertResult(@Nullable final Uri insertResult) {
        mInsertResults.addLast(insertResult);
    }

    /**
     * @param deleteResult A result that will be added to the FIFO queue to be consumed and
     *                     returned
     *                     by a call to {@link
     *                     #delete(Uri, String, String[])}.
     */
    public void addDeleteResult(final int deleteResult) {
        mDeleteResults.addLast(deleteResult);
    }

    /**
     * @param callResult A result that will be added to the FIFO queue to be consumed and returned
     *                   by a call to {@link
     *                   #call(String, String, Bundle)}.
     */
    public void addCallResult(@Nullable final Bundle callResult) {
        mCallResults.addLast(callResult);
    }

    /**
     * @return Polls the earliest query params or null if there are none.
     */
    @Nullable
    public QueryParams getQueryParams() {
        return mQueryParams.pollFirst();
    }

    /**
     * @return Polls the earliest insert params or null if there are none.
     */
    @Nullable
    public InsertParams getInsertParams() {
        return mInsertParams.pollFirst();
    }

    /**
     * @return Polls the earliest update params or null if there are none.
     */
    @Nullable
    public UpdateParams getUpdateParams() {
        return mUpdateParams.pollFirst();
    }

    /**
     * @return Polls the earliest delete params or null if there are none.
     */
    @Nullable
    public DeleteParams getDeleteParams() {
        return mDeleteParams.pollFirst();
    }

    /**
     * @return Polls the earliest call params or null if there are none.
     */
    @Nullable
    public CallParams getCallParams() {
        return mCallParams.pollFirst();
    }

    /*
     * A common error in testing is to forget to force calling attach info on the content provider.
     */
    private void assertAttachInfoCalled() {
        if (!mIsAttachInfoCalled) {
            throw new AssertionError("Call attachInfo() first"); //$NON-NLS
        }
    }

    /**
     * @return The number of times the {@link #query(Uri, String[], String, String[], String)}
     * method was called.
     */
    public int getQueryCount() {
        return mQueryCount;
    }

    /**
     * @return The number of times the {@link #insert(Uri, ContentValues)} method was called.
     */
    public int getInsertCount() {
        return mInsertCount;
    }

    /**
     * @return The number of times the {{@link #update(Uri, ContentValues, String, String[])}}
     * method was called.
     */
    public int getUpdateCount() {
        return mUpdateCount;
    }

    /**
     * @return The number of times the {@link #delete(Uri, String, String[])} method was called.
     */
    public int getDeleteCount() {
        return mDeleteCount;
    }

    /**
     * @return The number of times the {@link #call(String, String, Bundle)} method was called.
     */
    public int getCallCount() {
        return mCallCount;
    }

    @NonNull
    private static String[] copyArray(@NonNull final String[] toCopy) {
        assertNotNull(toCopy, "toCopy"); //$NON-NLS-1$

        final String[] result;
        result = new String[toCopy.length];
        System.arraycopy(toCopy, 0, result, 0, toCopy.length);

        return result;
    }

    /**
     * Helper method to make using the {@code MockableContentProvider} less verbose in some
     * standard use cases.
     *
     * @param baseContext Base context.
     * @param authority   Content authority.
     * @return A provider with a context whose content resolver will resolve {@code authority} to
     * the returned ContentProvider.  Note: Be sure to use {@link ContentProvider#getContext()} to
     * retrieve the appropriate context for querying the {@code ContentResolver}.
     */
    @NonNull
    public static MockableContentProvider newMockProvider(@NonNull final Context baseContext,
            @NonNull final String authority) {
        assertNotNull(authority, "authority"); //$NON-NLS

        final MockableContentProvider mockableContentProvider = new MockableContentProvider();

        final Context mockContext = new ContentProviderMockContext(
                InstrumentationRegistry.getContext(),
                Collections.singletonMap(authority,
                        mockableContentProvider));

        final ProviderInfo info = new ProviderInfo();
        info.authority = authority;

        mockableContentProvider.attachInfo(mockContext, info);

        return mockableContentProvider;
    }

    /**
     * Represents the parameters passed to {@link ContentProvider#query(Uri, String[], String,
     * String[], String)}.
     */
    @Immutable
    public static final class QueryParams {

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

        public QueryParams(@NonNull final Uri uri, @Nullable final String[] projection,
                @Nullable final String selection, @Nullable final String[] selectionArgs,
                @Nullable final String orderBy) {
            mUri = uri;
            mProjection = null == projection ? projection : copyArray(projection);
            mSelection = selection;
            mSelectionArgs = null == selectionArgs ? selectionArgs : copyArray(selectionArgs);
            mOrderBy = orderBy;
        }

        @NonNull
        public Uri getUri() {
            return mUri;
        }

        @Nullable
        public String[] getProjection() {
            if (null == mProjection) {
                return null;
            }

            return copyArray(mProjection);
        }

        @Nullable
        public String getSelection() {
            return mSelection;
        }

        @Nullable
        public String[] getSelectionArgs() {
            if (null == mSelectionArgs) {
                return null;
            }

            return copyArray(mSelectionArgs);
        }

        @Nullable
        public String getOrderBy() {
            return mOrderBy;
        }
    }

    /**
     * Represents the parameters passed to {@link ContentProvider#insert(Uri, ContentValues)}.
     */
    @Immutable
    public static final class InsertParams {

        @NonNull
        private final Uri mUri;

        @Nullable
        private final ContentValues mContentValues;

        public InsertParams(@NonNull final Uri uri, @Nullable final ContentValues values) {
            mUri = uri;
            // Note: shallow copy for blobs
            mContentValues = new ContentValues(values);
        }

        @NonNull
        public Uri getUri() {
            return mUri;
        }

        @Nullable
        public ContentValues getContentValues() {
            return new ContentValues(mContentValues);
        }

    }

    /**
     * Represents the parameters passed to {@link ContentProvider#update(Uri, ContentValues,
     * String,
     * String[])}.
     */
    @Immutable
    public static final class UpdateParams {

        @NonNull
        private final Uri mUri;

        @NonNull
        private final ContentValues mContentValues;

        @Nullable
        private final String mSelection;

        @NonNull
        private final String[] mSelectionArgs;

        public UpdateParams(@NonNull final Uri uri, @NonNull final ContentValues values,
                @Nullable final String selection, @Nullable final String[] selectionArgs) {
            mUri = uri;
            // Note: shallow copy for blobs
            mContentValues = new ContentValues(values);
            mSelection = selection;
            mSelectionArgs = null == selectionArgs ? null : copyArray(selectionArgs);
        }

        @NonNull
        public Uri getUri() {
            return mUri;
        }

        @NonNull
        public ContentValues getContentValues() {
            return new ContentValues(mContentValues);
        }

        @NonNull
        public String getSelection() {
            return mSelection;
        }

        @NonNull
        public String[] getSelectionArgs() {
            return copyArray(mSelectionArgs);
        }
    }


    /**
     * Represents the parameters passed to {@link ContentProvider#delete(Uri, String, String[])}.
     */
    @Immutable
    public static final class DeleteParams {

        @NonNull
        private final Uri mUri;

        @NonNull
        private final String mSelection;

        @NonNull
        private final String[] mSelectionArgs;

        public DeleteParams(@NonNull final Uri uri,
                @Nullable final String selection, @Nullable final String[] selectionArgs) {
            mUri = uri;
            mSelection = selection;
            mSelectionArgs = null == selectionArgs ? null : copyArray(selectionArgs);
        }

        @NonNull
        public Uri getUri() {
            return mUri;
        }

        @NonNull
        public String getSelection() {
            return mSelection;
        }

        @NonNull
        public String[] getSelectionArgs() {
            return copyArray(mSelectionArgs);
        }
    }

    /**
     * Represents the parameters passed to {@link ContentProvider#call(String, String, Bundle)}.
     * This class is not completely immutable, as the Bundle is a shallow copy.
     */
    @Immutable
    public static final class CallParams {

        @NonNull
        private final String mMethod;

        @Nullable
        private final String mArg;

        @Nullable
        private final Bundle mExtras;

        /**
         * @param method Method that was provided.
         * @param arg    Argument that was provided.
         * @param extras Extras that were provided.  Note that only a shallow copy of {@code
         *               extras} is made by this class.
         */
        public CallParams(@NonNull final String method, @Nullable final String arg,
                @Nullable final Bundle extras) {
            mMethod = method;
            mArg = arg;
            mExtras = shallowCopy(extras);
        }

        @NonNull
        public String getMethod() {
            return mMethod;
        }

        public String getArg() {
            return mArg;
        }

        /**
         * @return A shallow copy of the extras.
         */
        @Nullable
        public Bundle getExtras() {
            return shallowCopy(mExtras);
        }

        @Nullable
        private static Bundle shallowCopy(@Nullable final Bundle bundleToCopy) {
            if (null != bundleToCopy) {
                // Note: shallow copy for Bundle
                return new Bundle(bundleToCopy);
            } else {
                return null;
            }
        }
    }
}
