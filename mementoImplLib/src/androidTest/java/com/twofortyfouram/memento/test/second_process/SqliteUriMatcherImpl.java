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

package com.twofortyfouram.memento.test.second_process;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.internal.ImmutableUriMatcher;
import com.twofortyfouram.memento.model.Operation;
import com.twofortyfouram.memento.model.SqliteUriMatch;
import com.twofortyfouram.memento.model.SqliteUriMatcher;
import com.twofortyfouram.memento.test.main_process.provider.ContentProviderUtil;
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

    private static final int MATCH_TABLE_ONE_DIR = 0;

    private static final int MATCH_TABLE_ONE_ITEM = 1;

    @NonNull
    private final ImmutableUriMatcher mUriMatcher;

    @NonNull
    private final SqliteUriMatch mTableOneDirMatch;

    @NonNull
    private final SqliteUriMatch mTableOneItemMatch;

    /**
     * Construct a new matcher.
     */
    public SqliteUriMatcherImpl(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        final Context ctx = ContextUtil.cleanContext(context);

        mUriMatcher = new ImmutableUriMatcher(newUriMatcher(ctx));

        /*
         * Although a new match could be allocated each time #match(Uri) is called, let's avoid those
         * extra allocations for every call to the ContentProvider.
         */
        mTableOneDirMatch = newTableOneDirMatch(ctx);
        mTableOneItemMatch = newTableOneItemMatch(ctx);
    }

    @NonNull
    @Override
    public SqliteUriMatch match(@NonNull final Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_TABLE_ONE_DIR: {
                return mTableOneDirMatch;
            }
            case MATCH_TABLE_ONE_ITEM: {
                return mTableOneItemMatch;
            }
            default: {
                throw new IllegalArgumentException(Lumberjack.formatMessage(
                        "URI %s is unrecognized", uri)); //$NON-NLS-1$
            }
        }
    }

    @NonNull
    private static UriMatcher newUriMatcher(@NonNull final Context context) {
        final String contentAuthority = ContentProviderUtil.getContentAuthorityString(context);
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(contentAuthority, TableOneContractSecondProcess.TABLE_NAME, MATCH_TABLE_ONE_DIR);
        matcher.addURI(contentAuthority,
                TableOneContractSecondProcess.TABLE_NAME + "/#", MATCH_TABLE_ONE_ITEM); //$NON-NLS-1$

        return matcher;
    }

    /**
     * @param context Application context.
     * @return A new match object for an item in {@link TableOneContractSecondProcess}.
     */
    @NonNull
    private static SqliteUriMatch newTableOneDirMatch(@NonNull final Context context) {
        final Uri baseUri = TableOneContractSecondProcess.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TableOneContractSecondProcess.TABLE_NAME;
        final String mimeType = TableOneContractSecondProcess.MIMETYPE_DIR;
        final boolean isIdUri = false;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }

    /**
     * @param context Application context.
     * @return A new match object for an item in {@link TableOneContractSecondProcess}.
     */
    @NonNull
    private static SqliteUriMatch newTableOneItemMatch(@NonNull final Context context) {
        final Uri baseUri = TableOneContractSecondProcess.getContentUri(context);

        final Collection<Uri> notifyUris = new ArrayList<>(1);
        notifyUris.add(baseUri);

        final String tableName = TableOneContractSecondProcess.TABLE_NAME;
        final String mimeType = TableOneContractSecondProcess.MIMETYPE_ITEM;
        final boolean isIdUri = true;

        return new SqliteUriMatch(baseUri, notifyUris, EnumSet.allOf(Operation.class), tableName,
                mimeType, isIdUri
        );
    }
}
