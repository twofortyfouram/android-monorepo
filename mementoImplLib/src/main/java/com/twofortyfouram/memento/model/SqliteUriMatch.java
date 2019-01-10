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

package com.twofortyfouram.memento.model;

import android.net.Uri;
import androidx.annotation.NonNull;

import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Encapsulates details about a Uri match.
 */
@Immutable
public final class SqliteUriMatch {

    /**
     * Base Uri of the match.
     */
    @NonNull
    private final Uri mBaseUri;

    /**
     * List of Uris to notify when a change occurs.
     * <p>This has been wrapped in a call to {@link Collections#unmodifiableList(java.util.List)}.</p>
     */
    @NonNull
    private final List<Uri> mNotifyUris;

    /**
     * Collection of permitted operations.
     */
    @NonNull
    private final EnumSet<Operation> mAllowedOperations;

    /**
     * True if the URI matched a specific record in a table. (E.g. suffixed with
     * an ID). Otherwise false.
     */
    private final boolean mIsIdUri;

    /**
     * Mime type of the Uri.
     */
    @NonNull
    private final String mMimeType;

    /**
     * Name of the table.
     */
    @NonNull
    private final String mTableName;

    /**
     * @param baseUri           The base Uri of the match.
     * @param notifyUris        List of Uris to notify when a change is made for this match.
     * @param allowedOperations The set of operations permitted for this match.  Usually all
     *                          operations are allowed.  In certain circumstances such as for
     *                          a table that is a view, then perhaps only query is allowed.
     * @param tableName         Name of the table matched.
     * @param mimeType          The mime type of the match.
     * @param isIdUri           True if the URI matched a specific record in a table.
     *                          (E.g. suffixed with an ID).
     */
    public SqliteUriMatch(@NonNull final Uri baseUri, @NonNull final Collection<Uri> notifyUris,
            @NonNull final EnumSet<Operation> allowedOperations, @NonNull final String tableName,
            @NonNull final String mimeType,
            final boolean isIdUri) {
        assertNotNull(baseUri, "baseUri"); //$NON-NLS-1$
        assertNotNull(notifyUris, "notifyUris"); //$NON-NLS-1$
        assertNotNull(tableName, "tableName"); //$NON-NLS-1$
        assertNotNull(mimeType, "mimeType"); //$NON-NLS-1$

        mBaseUri = baseUri;
        mNotifyUris = Collections.unmodifiableList(new ArrayList<>(notifyUris));
        mAllowedOperations = EnumSet.copyOf(allowedOperations);
        mIsIdUri = isIdUri;
        mMimeType = mimeType;
        mTableName = tableName;
    }

    /**
     * @return The base Uri of the match.
     */
    @NonNull
    public Uri getBaseUri() {
        return mBaseUri;
    }

    /**
     * @return The Uris to notify when a change occurs.  A single Uri may notify multiple Uris of
     * changes, especially if several other URIs are dependent on the base uri.  The returned list will be wrapped in
     * a call to {@link Collections#unmodifiableList(java.util.List)}}.
     */
    @NonNull
    public List<Uri> getNotifyUris() {
        //noinspection ReturnOfCollectionOrArrayField
        return mNotifyUris;
    }

    /**
     * @param operationToCheck Operation to compare against the set of allowed operations.
     * @return True if {@code operationToCheck} is permitted for the match.
     */
    public boolean isOperationAllowed(@NonNull final Operation operationToCheck) {
        return mAllowedOperations.contains(operationToCheck);
    }

    /**
     * @return True if the URI matched a specific record in a table. (E.g.
     * suffixed with an ID). Otherwise false.
     */
    public boolean isIdUri() {
        return mIsIdUri;
    }

    /**
     * @return Mimetype of the item.
     */
    @NonNull
    public String getMimeType() {
        return mMimeType;
    }

    /**
     * @return The name of the table.  Technically this could be a view name as well.
     */
    @NonNull
    public String getTableName() {
        return mTableName;
    }
}
