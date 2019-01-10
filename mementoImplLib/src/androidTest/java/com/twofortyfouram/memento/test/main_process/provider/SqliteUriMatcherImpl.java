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

package com.twofortyfouram.memento.test.main_process.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.internal.ImmutableUriMatcher;
import com.twofortyfouram.memento.model.Operation;
import com.twofortyfouram.memento.model.SqliteUriMatch;
import com.twofortyfouram.memento.model.SqliteUriMatcher;
import com.twofortyfouram.memento.test.main_process.contract.KeyValueContract;
import com.twofortyfouram.memento.test.main_process.contract.LatestKeyValueContractView;
import com.twofortyfouram.memento.test.main_process.contract.TestTableOneContract;
import com.twofortyfouram.memento.test.main_process.contract.TestYouCanHazNoContract;
import com.twofortyfouram.spackle.ContextUtil;

import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Matcher for the Content Provider.
 */
@Immutable
public final class SqliteUriMatcherImpl implements SqliteUriMatcher {

    @NonNull
    private final ImmutableUriMatcher mUriMatcher;

    @NonNull
    private final SparseArray<SqliteUriMatch> mUriMatches;

    /**
     * Construct a new matcher.
     */
    public SqliteUriMatcherImpl(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        @NonNull final Context ctx = ContextUtil.cleanContext(context);

        @NonNull final String contentAuthority = ContentProviderUtil.getContentAuthorityString(ctx);
        @NonNull final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        @NonNull final SparseArray<SqliteUriMatch> uriMatches = new SparseArray<>(8);

        // Danger zone: This code is quite repetitive and it is easy to mix up getting this right

        int index = 0;

        uriMatcher.addURI(contentAuthority, TestTableOneContract.TABLE_NAME, index);
        uriMatches.put(index, newTableOneDirMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                TestTableOneContract.TABLE_NAME + "/#", index); //$NON-NLS-1$
        uriMatches.put(index, newTableOneItemMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority, TestYouCanHazNoContract.TABLE_NAME, index);
        uriMatches.put(index, newCanHazNoDirMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                TestYouCanHazNoContract.TABLE_NAME + "/#", index); //$NON-NLS-1$
        uriMatches.put(index, newCanHazNoItemMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                KeyValueContract.TABLE_NAME, index); //$NON-NLS-1$
        uriMatches.put(index, newKeyValueDirMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                KeyValueContract.TABLE_NAME + "/#", index); //$NON-NLS-1$
        uriMatches.put(index, newKeyValueItemMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                LatestKeyValueContractView.VIEW_NAME, index); //$NON-NLS-1$
        uriMatches.put(index, newLatestKeyValueDirMatch(ctx));
        index++;

        uriMatcher.addURI(contentAuthority,
                LatestKeyValueContractView.VIEW_NAME + "/#", index); //$NON-NLS-1$
        uriMatches.put(index, newLatestKeyValueItemMatch(ctx));
        index++;

        mUriMatcher = new ImmutableUriMatcher(uriMatcher);
        mUriMatches = uriMatches;
    }

    @NonNull
    @Override
    public SqliteUriMatch match(@NonNull final Uri uri) {
        final int match = mUriMatcher.match(uri);

        if (UriMatcher.NO_MATCH == match) {
            throw new IllegalArgumentException(Lumberjack.formatMessage(
                    "URI %s is unrecognized", uri)); //$NON-NLS-1$
        }

        return mUriMatches.get(match);
    }

    /**
     * @param context Application context.
     * @return A new match object for an item in {@link TestTableOneContract}.
     */
    @NonNull
    private static SqliteUriMatch newTableOneDirMatch(@NonNull final Context context) {
        final Uri baseUri = TestTableOneContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TestTableOneContract.TABLE_NAME;
        final String mimeType = TestTableOneContract.MIMETYPE_DIR;
        final boolean isIdUri = false;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    /**
     * @param context Application context.
     * @return A new match object for an item in {@link TestTableOneContract}.
     */
    @NonNull
    private static SqliteUriMatch newTableOneItemMatch(@NonNull final Context context) {
        final Uri baseUri = TestTableOneContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TestTableOneContract.TABLE_NAME;
        final String mimeType = TestTableOneContract.MIMETYPE_ITEM;
        final boolean isIdUri = true;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newCanHazNoDirMatch(@NonNull final Context context) {
        final Uri baseUri = TestYouCanHazNoContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TestYouCanHazNoContract.TABLE_NAME;
        final String mimeType = TestYouCanHazNoContract.MIMETYPE_DIR;
        final boolean isIdUri = false;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.noneOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newCanHazNoItemMatch(@NonNull final Context context) {
        final Uri baseUri = TestYouCanHazNoContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TestYouCanHazNoContract.TABLE_NAME;
        final String mimeType = TestYouCanHazNoContract.MIMETYPE_ITEM;
        final boolean isIdUri = true;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.noneOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newKeyValueDirMatch(@NonNull final Context context) {
        final Uri baseUri = KeyValueContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(2);
        notifyUris.add(baseUri);
        notifyUris.add(LatestKeyValueContractView.getContentUri(context));

        final String tableName = KeyValueContract.TABLE_NAME;
        final String mimeType = KeyValueContract.MIMETYPE_DIR;
        final boolean isIdUri = false;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newKeyValueItemMatch(@NonNull final Context context) {
        final Uri baseUri = KeyValueContract.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(2);
        notifyUris.add(baseUri);
        notifyUris.add(LatestKeyValueContractView.getContentUri(context));

        final String tableName = KeyValueContract.TABLE_NAME;
        final String mimeType = KeyValueContract.MIMETYPE_ITEM;
        final boolean isIdUri = true;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newLatestKeyValueDirMatch(@NonNull final Context context) {
        final Uri baseUri = LatestKeyValueContractView.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = LatestKeyValueContractView.VIEW_NAME;
        final String mimeType = LatestKeyValueContractView.MIMETYPE_DIR;
        final boolean isIdUri = false;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.of(Operation.QUERY), tableName,
                mimeType, isIdUri
        );
    }

    @NonNull
    private static SqliteUriMatch newLatestKeyValueItemMatch(@NonNull final Context context) {
        final Uri baseUri = LatestKeyValueContractView.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = LatestKeyValueContractView.VIEW_NAME;
        final String mimeType = LatestKeyValueContractView.MIMETYPE_ITEM;
        final boolean isIdUri = true;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.of(Operation.QUERY), tableName,
                mimeType, isIdUri
        );
    }
}
