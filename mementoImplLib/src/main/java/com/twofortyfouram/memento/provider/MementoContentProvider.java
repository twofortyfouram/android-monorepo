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

package com.twofortyfouram.memento.provider;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.*;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.contract.ExportContract;
import com.twofortyfouram.memento.contract.BatchContract;
import com.twofortyfouram.memento.contract.MementoContract;
import com.twofortyfouram.memento.contract.TransactionContract;
import com.twofortyfouram.memento.impl.BuildConfig;
import com.twofortyfouram.memento.internal.Constants;
import com.twofortyfouram.memento.internal.ExportTransactable;
import com.twofortyfouram.memento.internal.ContentChangeNotificationQueue;
import com.twofortyfouram.memento.internal.QueryStringUtil;
import com.twofortyfouram.memento.model.Operation;
import com.twofortyfouram.memento.model.SqliteUriMatch;
import com.twofortyfouram.memento.model.SqliteUriMatcher;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.Clock;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static java.util.Objects.requireNonNull;


/**
 * Assists in creating a first-class SQLite-backed ContentProvider that is DRY,
 * thread-safe, and bug-free. Typical Android ContentProviders require building a {@link
 * UriMatcher} and then repeating the same switch statement within each of query(), insert(),
 * update(), and delete(). This class takes a different approach: Subclasses return an instance of
 * {@link SqliteUriMatcher} via {@link #newSqliteUriMatcher()}, which handles all of the
 * Uri-matching logic. Not only is this significantly more DRY, but it also separates the
 * responsibilities of the ContentProvider from the Uri-matching. Unit testing then becomes easy,
 * because developers only really need to worry about testing their {@link SqliteUriMatcher}
 * implementation.
 * <p>
 * Additional features of this class include:
 * <ul>
 * <li>Atomic transactions for {@link #applyBatch(ArrayList)} and
 * {@link #bulkInsert(Uri, ContentValues[])}</li>
 * <li>Automatic content change notifications via the {@link android.content.ContentResolver} as
 * well as via {@link android.content.Intent#ACTION_PROVIDER_CHANGED}.  Note that {@link
 * android.content.Intent#ACTION_PROVIDER_CHANGED} will be secured if the ContentProvider has a
 * read permission as per {@link android.content.ContentProvider#getReadPermission()} (path
 * permissions as per {@link android.content.ContentProvider#getPathPermissions()} are not
 * currently supported for securing {@link android.content.Intent#ACTION_PROVIDER_CHANGED} Intents).
 * If the provider is not exported, the Intent will be package-only for API 14 and above.  Content
 * change notifications are only sent after a transaction succeeds, so for example no notifications
 * will be sent if a call to {@link #applyBatch(java.util.ArrayList)} fails.  Also note that
 * content change notifications will be for the base URI of the item that changed and will not
 * contain /#ID as the last path segment.  Finally note that if the same URI changes more than once
 * in a batch operation, the multiple duplicate notifications are coalesced.</li>
 * <li>Support for the query parameter {@link SearchManager#SUGGEST_PARAMETER_LIMIT}</li>
 * <li>Support for {@link BaseColumns#_COUNT} queries</li>
 * </ul>
 * <p>An example implementation can be found in the tests of the library.</p>
 */
@ThreadSafe
public abstract class MementoContentProvider extends ContentProvider {

    /**
     * Debug flag to slow down ContentProvider methods. The primary purpose is to make it easier to
     * test asynchronous operations, such as Loaders in a UI.
     * <p>
     * This is a debug feature only and MUST be disabled in release builds.
     *
     * @see #SLOW_ACCESS_DELAY_MILLISECONDS
     */
    @VisibleForTesting
    /* package */ static final boolean IS_SLOW_ACCESS_ENABLED = false;

    /**
     * Time in milliseconds to delay access.
     *
     * @see #IS_SLOW_ACCESS_ENABLED
     */
    private static final long SLOW_ACCESS_DELAY_MILLISECONDS = 2 * DateUtils.SECOND_IN_MILLIS;

    @NonNull
    private static final String WHERE_ID = String.format(Locale.US, "%s = ?", //NON-NLS
            BaseColumns._ID);

    @NonNull
    private static final String COUNT = String
            .format(Locale.US, "COUNT(*) AS %s", BaseColumns._COUNT); //$NON-NLS

    @NonNull
    private static final String[] COUNT_COLUMNS = {COUNT};

    /**
     * Helper to open the database.
     * <p>
     * This field will be initialized in {@link #onCreate()}.
     */
    @Nullable
    private volatile SupportSQLiteOpenHelper mSqliteOpenHelper = null;

    /**
     * Matcher for {@code Uri}s.
     * <p>
     * This field will be initialized in {@link #onCreate()}.
     */
    @Nullable
    private volatile SqliteUriMatcher mSqliteUriMatcher = null;

    /**
     * Thread-specific container for operation results.
     */
    @NonNull
    private final ThreadLocal<ContentChangeNotificationQueue>
            mThreadLocalContentChangeNotificationQueue
            = new ThreadLocal<>();

    /**
     * Flag indicating whether the ContentProvider is exported.
     */
    private volatile boolean mIsExported = false;

    /**
     * Read permission for the provider.  May be null if there is no read permission.
     */
    @Nullable
    private volatile String mReadPermission = null;

    @Override
    public boolean onCreate() {
        Lumberjack.v("Creating ContentProvider %s at elapsedRealtimeMillis=%d", getClass().getName(), Clock.getInstance().getRealTimeMillis()); //$NON-NLS

        /*
         * These fields are volatile, which therefore makes their initialization here in onCreate()
         * thread-safe. No additional synchronization is required because onCreate() is guaranteed
         * to be called by the Android framework before any of the other methods that need these fields.
         */
        mSqliteOpenHelper = newSqliteOpenHelper();
        mSqliteUriMatcher = newSqliteUriMatcher();

        return true;
    }

    @Override
    public void attachInfo(@NonNull final Context context, final ProviderInfo info) {
        super.attachInfo(context, info);

        // info should only be null during unit tests
        if (null != info) {
            /*
             * These fields are volatile, which therefore makes their initialization here in attachInfo()
             * thread-safe. No additional synchronization is required because attachInfo() is guaranteed
             * to be called by the Android framework before any of the other methods that need these fields.
             */
            mIsExported = info.exported;
            mReadPermission = info.readPermission;
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void shutdown() {
        mSqliteOpenHelper.close();

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            super.shutdown();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // Releasing memory isn't supported by non-platform implementations.
        final int bytesReleased = SQLiteDatabase.releaseMemory();
        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("Released %d bytes of memory", bytesReleased); //$NON-NLS-1$
        }
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        try {
            @NonNull final SqliteUriMatch match = mSqliteUriMatcher.match(uri);

            return match.getMimeType();
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    @Slow(Speed.MILLISECONDS)
    public int delete(@NonNull final Uri uri, @Nullable final String selection,
                      @Nullable final String[] selectionArgs) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        slowAccessForDebugging();

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("uri: %s, selection: %s, selectionArgs: %s", uri, selection, //$NON-NLS-1$
                    selectionArgs);
        }

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        int count = 0;

        @NonNull final SqliteUriMatch match = mSqliteUriMatcher.match(uri);

        if (!match.isOperationAllowed(Operation.DELETE)) {
            throw new IllegalArgumentException(
                    Lumberjack.formatMessage("Uri %s does not support the operation %s",
                            uri, Operation.DELETE));
        }

        if (match.isIdUri()) {
            @NonNull final String tableName = match.getTableName();
            @NonNull final String segment = uri.getLastPathSegment();
            @NonNull final String idSelectionArg = newAndIdSelection(selection);
            @NonNull final String[] idSelectionArgs = newAndIdSelectionArgs(segment, selectionArgs);

            count = database.delete(tableName, idSelectionArg, idSelectionArgs);
        } else {
            if (null == selection) {
                // Per the docs, pass "1" to get a count returned.  It looks weird but is correct.
                count = database.delete(match.getTableName(), "1", null); //$NON-NLS-1$
            } else {
                count = database.delete(match.getTableName(), selection, selectionArgs);
            }
        }

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("%s rows actually deleted", count); //$NON-NLS-1$
        }

        if (0 < count) {
            if (!QueryStringUtil.isSuppressNotification(uri)) {
                getContentChangeNotificationQueue().onContentChanged(match.getNotifyUris());
            }
        }

        return count;
    }

    @Override
    @Slow(Speed.MILLISECONDS)
    public Uri insert(@NonNull final Uri uri, @NonNull final ContentValues values) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        assertNotNull(values, "values"); //$NON-NLS-1$
        slowAccessForDebugging();

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("uri: %s, values: %s", uri, values); //$NON-NLS-1$
        }

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        @NonNull final SqliteUriMatch match = mSqliteUriMatcher.match(uri);

        if (!match.isOperationAllowed(Operation.INSERT)) {
            throw new IllegalArgumentException(
                    Lumberjack.formatMessage("Uri %s does not support the operation %s",
                            uri, Operation.INSERT));
        }

        @NonNull final ContentValues valuesToInsert;
        if (match.isIdUri()) {
            // Make a copy to avoid mutating the input parameter
            valuesToInsert = new ContentValues(values.size() + 1);
            valuesToInsert.putAll(values);
            valuesToInsert.put(BaseColumns._ID, uri.getLastPathSegment());
        } else {
            valuesToInsert = values;
        }

        @Nullable Uri resultUri = null;

        final long rowID = database
                .insert(match.getTableName(), SQLiteDatabase.CONFLICT_ABORT, valuesToInsert);

        if (-1 != rowID) {
            resultUri = ContentUris.withAppendedId(match.getBaseUri(), rowID);
        }

        if (null != resultUri) {
            final boolean isSuppressNotification = uri.getBooleanQueryParameter(
                    MementoContract.QUERY_STRING_IS_SUPPRESS_NOTIFICATION, false);
            if (!isSuppressNotification) {
                getContentChangeNotificationQueue().onContentChanged(match.getNotifyUris());
            }
        }

        return resultUri;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation guarantees that bulk inserts are fully atomic.
     */
    @Override
    @Slow(Speed.MILLISECONDS)
    public int bulkInsert(@NonNull final Uri uri, @NonNull final ContentValues[] values) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        assertNotNull(values, "values"); //$NON-NLS-1$

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("uri: %s, values: %s", uri, values); //$NON-NLS-1$
        }

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();
        @NonNull final ContentChangeNotificationQueue contentChangeNotificationQueue
                = getContentChangeNotificationQueue();

        int count = 0;

        if (contentChangeNotificationQueue.isBatch()) {
            count = super.bulkInsert(uri, values);
        } else {
            boolean isSuccess = false;

            contentChangeNotificationQueue.startBatch();
            database.beginTransaction();
            try {
                count = super.bulkInsert(uri, values);

                database.setTransactionSuccessful();
                isSuccess = true;
            } finally {
                database.endTransaction();

                contentChangeNotificationQueue.endBatch(isSuccess);
            }
        }

        return count;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method supports {@link SearchManager#SUGGEST_PARAMETER_LIMIT} and queries with a
     * projection consisting only of {@link android.provider.BaseColumns#_COUNT}.</p>
     * <p>
     * Note: The order of columns in the returned cursor are not guaranteed to be consistent from
     * call to call or guaranteed to match the order of the columns in {@code projection}.
     * </p>
     */
    @Override
    @Slow(Speed.MILLISECONDS)
    public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
                        @Nullable final String selection,
                        @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        slowAccessForDebugging();

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack
                    .v("uri: %s, projection: %s, selection: %s, selectionArgs: %s, sortOrder: %s",
                            //NON-NLS
                            uri, projection, selection, selectionArgs, sortOrder);
        }

        @Nullable final String limit = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);

        return queryHelper(uri, projection, selection, selectionArgs, sortOrder, limit, null);
    }

    @NonNull
    @Slow(Speed.MILLISECONDS)
    private Cursor queryHelper(@NonNull final Uri uri, @Nullable final String[] projection,
                               @Nullable final String selection,
                               @Nullable final String[] selectionArgs, @Nullable final String sortOrder,
                               @Nullable final String limit, @Nullable final String offset) {
        if (null != offset && null == limit) {
            throw new AssertionError("Limit must be set when using offset parameter."); //$NON-NLS
        }

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        @Nullable Cursor result = null;

        @NonNull final SqliteUriMatch match = mSqliteUriMatcher.match(uri);

        if (!match.isOperationAllowed(Operation.QUERY)) {
            throw new IllegalArgumentException(
                    Lumberjack.formatMessage("Uri %s does not support the operation %s", //$NON-NLS
                            uri, Operation.QUERY));
        }

        @NonNull final SupportSQLiteQueryBuilder qb = SupportSQLiteQueryBuilder
                .builder(match.getTableName());

        if (null != projection && 1 == projection.length
                && BaseColumns._COUNT.equals(projection[0])) {
            qb.columns(COUNT_COLUMNS);
        } else {
            qb.columns(projection);
        }

        @Nullable final String idSelectionArg;
        @Nullable final String[] idSelectionArgs;
        if (match.isIdUri()) {
            final String segment = uri.getLastPathSegment();
            idSelectionArg = newAndIdSelection(selection);
            idSelectionArgs = newAndIdSelectionArgs(segment, selectionArgs);
        } else {
            idSelectionArg = selection;
            idSelectionArgs = selectionArgs;
        }

        qb.selection(idSelectionArg, idSelectionArgs);
        qb.orderBy(sortOrder);

        //Counterintuitive but correct. When using the comma syntax, offset comes first.
        //When using the keyword syntax, "LIMIT 1 OFFSET 2" then the offset comes second.
        //This is just how the SQLite API is designed.
        if (null == offset) {
            qb.limit(limit);
        } else {
            qb.limit(String.format(Locale.US, "%s,%s", offset, limit)); //NON-NLS
        }

        @NonNull final SupportSQLiteQuery query = qb.create();

        result = database.query(query);

        result.setNotificationUri(getContext().getContentResolver(), match.getBaseUri());
        // If implemented properly (which it is by memento) where the inserts, deletes, and updates
        // cause multiple notifications to be sent, then this isn't necessary.
//        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.Q)) {
//            result.setNotificationUris(getContext().getContentResolver(), match.getNotifyUris());
//        }

        return result;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
                        @Nullable final String selection,
                        @Nullable final String[] selectionArgs, @Nullable final String sortOrder,
                        @Nullable final CancellationSignal cancellationSignal) {

        @Nullable final String limit = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);

        return queryHelper(uri, projection, selection, selectionArgs, sortOrder, limit, null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method supports {@link SearchManager#SUGGEST_PARAMETER_LIMIT} and queries with a
     * projection consisting only of {@link android.provider.BaseColumns#_COUNT}.</p>
     * <p>
     * <p>
     * This method also supports {@link ContentResolver#QUERY_ARG_SQL_SELECTION}, {@link
     * ContentResolver#QUERY_ARG_SQL_SELECTION_ARGS}, {@link ContentResolver#QUERY_ARG_SQL_SORT_ORDER},
     * {@link ContentResolver#QUERY_ARG_OFFSET} and {@link ContentResolver#QUERY_ARG_LIMIT}.
     * If any of these arguments are invalid, then the invalid argument will be ignored and the
     * rest of the query will proceed.
     * </p>
     * <p>
     * Do not combine {@link SearchManager#SUGGEST_PARAMETER_LIMIT} and {@link
     * ContentResolver#QUERY_ARG_LIMIT}.  If both exist, {@link
     * ContentResolver#QUERY_ARG_LIMIT} takes precedence even if it is invalid.
     * </p>
     * <p>
     * When you use {@link ContentResolver#QUERY_ARG_OFFSET} you must also specify
     * {@link ContentResolver#QUERY_ARG_LIMIT} otherwise the offset will be ignored.
     * </p>
     * <p>
     * Note: The order of columns in the returned cursor are not guaranteed to be consistent from
     * call to call or guaranteed to match the order of the columns in {@code projection}.
     * </p>
     */
    @Override
    @TargetApi(Build.VERSION_CODES.O)
    public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
                        @Nullable final Bundle queryArgs,
                        @Nullable final CancellationSignal cancellationSignal) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$

        if (BundleScrubber.scrub(queryArgs)) {
            Lumberjack.e(
                    "queryArgs contains Serializable/Parcelable from a different class loader; all arguments are being ignored"); //$NON-NLS
        }

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack
                    .v("uri: %s, projection: %s, queryArgs: %s, cancellationSignal: %s", //NON-NLS
                            uri, projection, queryArgs, cancellationSignal);
        }

        Uri uriToPass = uri;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        String limit = QueryStringUtil.getLimit(uri); // may be replaced if Bundle arg exists
        String offset = null;

        @NonNull final LinkedList<String> honoredBundleArgs = new LinkedList<>();

        if (null != queryArgs) {
            if (queryArgs.containsKey(ContentResolver.QUERY_ARG_SQL_SELECTION)) {
                try {
                    BundleAssertions
                            .assertHasString(queryArgs, ContentResolver.QUERY_ARG_SQL_SELECTION,
                                    false,
                                    false);

                    selection = queryArgs.getString(ContentResolver.QUERY_ARG_SQL_SELECTION);
                    honoredBundleArgs.add(ContentResolver.QUERY_ARG_SQL_SELECTION);
                } catch (final AssertionError e) {
                    Lumberjack.e(
                            "queryArgs doesn't have valid non-null, non-empty String for %s",
                            //$NON-NLS
                            ContentResolver.QUERY_ARG_SQL_SELECTION);
                }
            }

            if (queryArgs.containsKey(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS)) {
                try {
                    BundleAssertions
                            .assertHasStringArray(queryArgs,
                                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);

                    selectionArgs = queryArgs
                            .getStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);
                    honoredBundleArgs.add(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);
                } catch (final AssertionError e) {
                    Lumberjack
                            .e("queryArgs doesn't have valid non-null String[] for %s",
                                    //$NON-NLS
                                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS);
                }
            }

            if (queryArgs.containsKey(ContentResolver.QUERY_ARG_SQL_SORT_ORDER)) {
                try {
                    BundleAssertions
                            .assertHasString(queryArgs,
                                    ContentResolver.QUERY_ARG_SQL_SORT_ORDER);

                    sortOrder = queryArgs
                            .getString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
                    honoredBundleArgs.add(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
                } catch (final AssertionError e) {
                    Lumberjack.e(
                            "queryArgs doesn't have valid non-null, non-empty String for %s",
                            //$NON-NLS
                            ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
                }
            }

            if (queryArgs.containsKey(ContentResolver.QUERY_ARG_LIMIT)) {
                try {
                    BundleAssertions.assertHasInt(queryArgs, ContentResolver.QUERY_ARG_LIMIT, 1,
                            Integer.MAX_VALUE);

                    if (null != limit) {
                        Lumberjack
                                .e("Query contains both a Bundle argument for limit and query string SUGGEST_PARAMETER_LIMIT; using bundle argument"); //$NON-NLS
                    }

                    final int bundleLimit = queryArgs.getInt(ContentResolver.QUERY_ARG_LIMIT);

                    limit = Integer.toString(bundleLimit);
                    honoredBundleArgs.add(ContentResolver.QUERY_ARG_LIMIT);
                } catch (final AssertionError e) {
                    Lumberjack.e(
                            "queryArgs doesn't have valid int in the range [1, %d] for %s",
                            //$NON-NLS
                            Integer.MAX_VALUE, ContentResolver.QUERY_ARG_LIMIT);

                    limit = null;
                }
            }

            if (queryArgs.containsKey(ContentResolver.QUERY_ARG_OFFSET)) {
                try {
                    BundleAssertions.assertHasInt(queryArgs, ContentResolver.QUERY_ARG_OFFSET, 0,
                            Integer.MAX_VALUE);

                    if (null == limit) {
                        offset = null;
                        Lumberjack.e(
                                "%s must be set when using %s parameter.",//$NON-NLS
                                ContentResolver.QUERY_ARG_LIMIT, ContentResolver.QUERY_ARG_OFFSET);
                    } else {
                        offset = Integer
                                .toString(queryArgs.getInt(ContentResolver.QUERY_ARG_OFFSET));
                        honoredBundleArgs.add(ContentResolver.QUERY_ARG_OFFSET);
                    }
                } catch (final AssertionError e) {
                    Lumberjack.e(
                            "queryArgs doesn't have valid int in the range [0, %d] for %s",//$NON-NLS
                            Integer.MAX_VALUE, ContentResolver.QUERY_ARG_OFFSET);

                    offset = null;
                }
            }
        }

        final Cursor resultCursor = queryHelper(uriToPass, projection, selection, selectionArgs,
                sortOrder, limit, offset);

        final Bundle honoredBundle;
        if (!honoredBundleArgs.isEmpty()) {
            honoredBundle = new Bundle();

            final String[] honoredBundleArgsArray = honoredBundleArgs
                    .toArray(new String[honoredBundleArgs.size()]);

            honoredBundle
                    .putStringArray(ContentResolver.EXTRA_HONORED_ARGS, honoredBundleArgsArray);
        } else {
            honoredBundle = Bundle.EMPTY;
        }

        resultCursor.setExtras(honoredBundle);

        return resultCursor;
    }

    @Override
    @Slow(Speed.MILLISECONDS)
    public int update(@NonNull final Uri uri, @Nullable final ContentValues values,
                      @Nullable final String selection,
                      @Nullable final String[] selectionArgs) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        slowAccessForDebugging();

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack
                    .v("uri: %s, values: %s, selection: %s, selectionArgs: %s",     //NON-NLS
                            uri, values, selection, selectionArgs);
        }

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        @NonNull final SqliteUriMatch match = mSqliteUriMatcher.match(uri);

        if (!match.isOperationAllowed(Operation.UPDATE)) {
            throw new IllegalArgumentException(
                    Lumberjack.formatMessage("Uri %s does not support the operation %s", //$NON-NLS
                            uri, Operation.UPDATE));
        }

        int count = 0;
        final String idSelectionArg;
        final String[] idSelectionArgs;
        if (match.isIdUri()) {
            final String segment = uri.getLastPathSegment();
            idSelectionArg = newAndIdSelection(selection);
            idSelectionArgs = newAndIdSelectionArgs(segment, selectionArgs);
        } else {
            idSelectionArg = selection;
            idSelectionArgs = selectionArgs;
        }

        count = database
                .update(match.getTableName(), SQLiteDatabase.CONFLICT_ABORT, values, idSelectionArg,
                        idSelectionArgs);

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("%s rows updated", count); //$NON-NLS-1$
        }

        if (0 < count) {
            if (!QueryStringUtil.isSuppressNotification(uri)) {
                getContentChangeNotificationQueue().onContentChanged(match.getNotifyUris());
            }
        }

        return count;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation guarantees that batch operations are fully atomic.
     */
    @Override
    @Slow(Speed.MILLISECONDS)
    @NonNull
    public ContentProviderResult[] applyBatch(
            @NonNull final ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        @NonNull final ContentChangeNotificationQueue contentChangeNotificationQueue
                = getContentChangeNotificationQueue();

        @Nullable ContentProviderResult[] result = null;

        if (contentChangeNotificationQueue.isBatch()) {
            result = super.applyBatch(operations);
        } else {
            boolean isSuccessful = false;

            contentChangeNotificationQueue.startBatch();
            database.beginTransaction();
            try {
                result = super.applyBatch(operations);
                database.setTransactionSuccessful();

                isSuccessful = true;
            } finally {
                database.endTransaction();

                contentChangeNotificationQueue.endBatch(isSuccessful);
            }
        }

        return result;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Bundle call(@NonNull final String method, @Nullable final String arg,
                       @Nullable final Bundle extras) {
        assertNotNull(method, "method"); //$NON-NLS

        final boolean isSelfPackage = isSelfPackage();

        /* Required for Transactable objects to be recognized.  This has to be set before scrubbing or logging.
         *
         * I can imagine a scenario where this is a security risk, as it allows other processes access to the
         * classloader for the ContentProvider's package, but I can't come up with the exact scenario where this is
         * actually dangerous.  Just to be safe, check whether the calling package is the current package.
         */
        if (isSelfPackage) {
            if (null != extras) {
                extras.setClassLoader(MementoContentProvider.class.getClassLoader());
            }
        }

        if (BundleScrubber.scrub(extras)) {
            Lumberjack.w("Bad bundle"); //$NON-NLS
            return null;
        }

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack.v("method: %s, arg: %s, extras: %s", method, arg, extras); //$NON-NLS
        }

        switch (method) {
            case BatchContract.METHOD_BATCH_OPERATIONS:
                // We can't rely on the ContentProvider's default security, because security checks for
                // query, insert, update, and delete will be bypassed once they are being initiated
                // from within the call() method.  To make this simple, only allow batches from within
                // the same package.
                if (!isSelfPackage) {
                    throw new SecurityException("This method must be performed within the same package as the content provider."); //$NON-NLS
                }

                if (null == extras) {
                    return super.call(method, arg, extras);
                }

                final ArrayList<ArrayList<ContentProviderOperation>> operations;
                try {
                    operations = (ArrayList<ArrayList<ContentProviderOperation>>) extras
                            .getSerializable(BatchContract.EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);
                } catch (final ClassCastException e) {
                    throw new IllegalArgumentException(
                            "Extra is not ArrayList<ArrayList<ContentProviderOperation>>"); //$NON-NLS
                }

                applyBatchWithAlternatives(operations);

                return Bundle.EMPTY;
            case ExportContract.METHOD_EXPORT:
                if (!isSelfPackage) {
                    throw new SecurityException("This method must be performed within the same package as the content provider."); //$NON-NLS
                }

                // arg is destination file path
                if (TextUtils.isEmpty(arg)) {
                    Lumberjack.e("Arg (file path) is null or empty."); //NON-NLS

                    @NonNull final Bundle result = new Bundle();
                    result.putBoolean(ExportContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false);
                    return result;
                }

                @Nullable final String databasePath = mSqliteOpenHelper.getWritableDatabase().getPath();

                if (null == databasePath) {
                    Lumberjack.e("Database filename is null, indicating an in-memory database"); //NON-NLS

                    @NonNull final Bundle result = new Bundle();
                    result.putBoolean(ExportContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, false);
                    return result;
                }

                @NonNull final Bundle transactableArgs = ExportTransactable.newDataBundle(
                        databasePath, arg);
                @NonNull final Transactable exportTransactable = new ExportTransactable();
                @NonNull final Bundle resultBundle = runInTransaction(exportTransactable, transactableArgs);
                final boolean operationValid = ExportTransactable.getResultFromBundle(resultBundle);

                if (operationValid) {
                    // TODO: it would be better to skip the media scanner if the file is not on external storage
                    MediaScannerConnection.scanFile(getContext(), new String[]{arg}, null,
                            (path, uri) -> Lumberjack.d("MediaScanner update - %s", path)); //NON-NLS
                }

                @NonNull final Bundle result = new Bundle();
                result.putBoolean(ExportContract.RESULT_EXTRA_BOOLEAN_IS_SUCCESS, operationValid);

                return result;
            case TransactionContract.METHOD_RUN_IN_TRANSACTION:
                // We can't rely on the ContentProvider's default security, because security checks for
                // query, insert, update, and delete will be bypassed once they are being initiated
                // from within the call() method.  To make this simple, only allow batches from within
                // the same package.
                if (!isSelfPackage) {
                    throw new SecurityException("This method must be performed within the same package as the content provider."); //$NON-NLS
                }

                if (null == extras) {
                    return super.call(method, arg, extras);
                }

                @NonNull final Transactable transactable
                        = requireNonNull(extras.getParcelable(TransactionContract.EXTRA_BUNDLE_PARCELABLE_TRANSACTABLE));
                @NonNull final Bundle transactableData
                        = requireNonNull(extras.getBundle(TransactionContract.EXTRA_BUNDLE_TRANSACTABLE_DATA));

                return runInTransaction(transactable, transactableData);
        }

        return super.call(method, arg, extras);
    }

    /**
     * @return Whether the calling package is the current package.
     */
    protected boolean isSelfPackage() {
        final boolean isSelfPackage;
        if (BuildConfig.DEBUG && ContextUtil.isTestContext(getContext())) {
            isSelfPackage = true;
        } else {
            isSelfPackage = getContext().getPackageName().equals(getCallingPackage());
        }

        return isSelfPackage;
    }

    /**
     * Takes a collection of batch operations and applies them one at a time in order until one of
     * them succeeds.
     *
     * @param operations Collection of batch operations to try performing.
     * @see BatchContract#applyBatchWithAlternatives(Context,
     * Uri, ArrayList)
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void applyBatchWithAlternatives(
            @NonNull final ArrayList<ArrayList<ContentProviderOperation>> operations) {
        assertNotNull(operations, "operations"); //$NON-NLS

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();

        @NonNull final ContentChangeNotificationQueue contentChangeNotificationQueue
                = getContentChangeNotificationQueue();
        if (contentChangeNotificationQueue.isBatch()) {
            throw new IllegalStateException("Transaction is already in progress"); //$NON-NLS
        }

        boolean isSuccessful = false;
        try {
            database.beginTransaction();
            database.execSQL("SAVEPOINT outersavepoint"); //$NON-NLS

            for (@NonNull final ArrayList<ContentProviderOperation> ops : operations) {

                contentChangeNotificationQueue.startBatch();
                try {
                    database.execSQL("SAVEPOINT innersavepoint"); //$NON-NLS
                    super.applyBatch(ops);
                    isSuccessful = true;

                    // Exit the loop
                    break;
                } catch (final OperationApplicationException e) {
                    // Note: Semicolon works around a bug in Android
                    // https://code.google.com/p/android/issues/detail?id=38706
                    database.execSQL("; ROLLBACK TO innersavepoint"); //$NON-NLS
                    contentChangeNotificationQueue.endBatch(false);
                }
            }

            database.execSQL("RELEASE outersavepoint"); //$NON-NLS
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            contentChangeNotificationQueue.endBatch(isSuccessful);
        }
    }

    /**
     * When an ID is appended to the base URI, this method is used to format a new where clause for
     * {@code _id = ? AND (selection)}.  This new where clause is intended to be used in
     * conjunction
     * with the selection arguments generated by {@link #newAndIdSelectionArgs(String, String[])}.
     *
     * @param selection Where clause to select.
     * @return Formatted where clause with the ID selection and the where cause combined.  Be sure
     * to use {@link #newAndIdSelectionArgs(String, String[])} for the args.
     * @see #newAndIdSelectionArgs(String, String[])
     */
    @NonNull
    @VisibleForTesting
    /*package*/ static String newAndIdSelection(@Nullable final String selection) {
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            return concatenateWhereHoneycombPlus(selection);
        } else {
            if (!TextUtils.isEmpty(selection)) {
                return String.format(Locale.US, "(%s) AND (%s)", WHERE_ID, //$NON-NLS-1$
                        selection);
            }
            return WHERE_ID;
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static String concatenateWhereHoneycombPlus(@Nullable final String selection) {
        return DatabaseUtils.concatenateWhere(WHERE_ID, selection);
    }

    /**
     * @param id   ID of the element to select.
     * @param args Selection arguments.
     * @return New selection arguments including the {@code id}.
     */
    @NonNull
    @VisibleForTesting
    /*package*/ static String[] newAndIdSelectionArgs(@NonNull final String id,
                                                      @Nullable final String[] args) {
        assertNotEmpty(id, "id"); //$NON-NLS-1$

        if (null == args || 0 == args.length) {
            return new String[]{id};
        }

        @NonNull final String[] result = new String[args.length + 1];
        result[0] = id;

        System.arraycopy(args, 0, result, 1, args.length);

        return result;
    }

    /**
     * Runs a block of code inside a single atomic transaction. For the most part, multiple
     * operations should be performed using {@link #applyBatch(ArrayList)}. In certain cases where
     * that isn't possible (for example, queries), this method can be used.
     *
     * @param transactable to execute inside the database transaction.
     * @param data         for {@link Transactable#runInTransaction(Context, Bundle)}.
     * @see TransactionContract#runInTransaction(Context, Uri, Transactable, Bundle)
     */
    @Slow(Speed.MILLISECONDS)
    @Nullable
    protected Bundle runInTransaction(@NonNull final Transactable transactable, @NonNull final Bundle data) {
        assertNotNull(transactable, "transactable"); //$NON-NLS-1$
        assertNotNull(data, "data"); //$NON-NLS-1$

        @NonNull final SupportSQLiteDatabase database = mSqliteOpenHelper.getWritableDatabase();
        @NonNull final ContentChangeNotificationQueue contentChangeNotificationQueue
                = getContentChangeNotificationQueue();

        @Nullable Bundle result = null;

        if (contentChangeNotificationQueue.isBatch()) {
            result = transactable.runInTransaction(getContext(), data);
        } else {
            boolean isSuccessful = false;

            contentChangeNotificationQueue.startBatch();
            database.beginTransaction();
            try {
                result = transactable.runInTransaction(getContext(), data);
                database.setTransactionSuccessful();

                isSuccessful = true;
            } finally {
                database.endTransaction();

                contentChangeNotificationQueue.endBatch(isSuccessful);
            }
        }

        return result;
    }

    /**
     * @return Gets a {@link ContentChangeNotificationQueue} for the current thread.
     */
    @NonNull
    private ContentChangeNotificationQueue getContentChangeNotificationQueue() {
        @Nullable ContentChangeNotificationQueue queue = mThreadLocalContentChangeNotificationQueue
                .get();

        if (null == queue) {
            queue = new ContentChangeNotificationQueue(getContext(), mIsExported, mReadPermission);
            mThreadLocalContentChangeNotificationQueue.set(queue);
        }

        return queue;
    }

    private static void slowAccessForDebugging() {
        if (IS_SLOW_ACCESS_ENABLED) {
            SystemClock.sleep(SLOW_ACCESS_DELAY_MILLISECONDS);
        }
    }

    /**
     * @return An {@link SqliteUriMatcher} appropriate for the current ContentProvider. The object
     * returned by this method must be thread-safe.
     */
    @NonNull
    protected abstract SqliteUriMatcher newSqliteUriMatcher();

    /**
     * @return A {@link SQLiteOpenHelper} appropriate for the current ContentProvider. The object
     * returned by this method must be thread-safe.
     */
    @NonNull
    protected abstract SupportSQLiteOpenHelper newSqliteOpenHelper();

}
