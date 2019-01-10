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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import net.jcip.annotations.NotThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Helper to build a view from a table with multiple history elements, selecting for the most recent of each unique item.
 *
 * For example, imagine a table like:
 *
 * _ID    KEY    VALUE
 * 1      FOO    BAR
 * 2      FOO    BAZ
 *
 * The view created by this class would return the value for FOO which has the largest _ID.  The _ID column is considered
 * the "version" column in this example.
 */
@NotThreadSafe
public final class SqliteLatestViewBuilder {

    @Nullable
    private String mViewName = null;

    @Nullable
    private String mParentTableName = null;

    @Nullable
    private String mVersionColumnName = null;

    @Nullable
    private String mKeyColumnName = null;

    /**
     * Sets the name of the view.
     *
     * @param tableName Name of the view.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteLatestViewBuilder setViewName(@NonNull @Size(min = 1) final String tableName) {
        assertNotNull(tableName, "tableName"); //$NON-NLS-1$

        mViewName = tableName;

        return this;
    }

    /**
     * Sets the name of the parent table from which the view is generated.
     *
     * @param fromTableName Name of the table this view is built from.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteLatestViewBuilder setFromTableName(@NonNull @Size(min = 1) final String fromTableName) {
        assertNotNull(fromTableName, "fromTableName"); //$NON-NLS-1$

        mParentTableName = fromTableName;

        return this;
    }

    /**
     * Sets the column in the {@code fromTable} that identifies the version of the row.  Often this is _ID, although it
     * could be an epoch timestamp.  It needs to be a numeric value.
     *
     * @param versionColumnName Name of the version column.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteLatestViewBuilder setVersionColumnName(@NonNull @Size(min = 1) final String versionColumnName) {
        assertNotNull(versionColumnName, "versionColumnName"); //$NON-NLS

        mVersionColumnName = versionColumnName;

        return this;
    }

    /**
     * Sets the column in the {@code fromTable} that identifies the key of the row.
     *
     * @param keyColumnName Name of the version column.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteLatestViewBuilder setKeyColumnName(@NonNull @Size(min = 1) final String keyColumnName) {
        assertNotNull(keyColumnName, "keyColumnName"); //$NON-NLS

        mKeyColumnName = keyColumnName;

        return this;
    }

    /**
     * @return The SQL statement to create the view.
     * @throws IllegalStateException If {@link #setViewName(String)}, {@link #setFromTableName(String)}, {@link #setVersionColumnName(String)}, and {@link #setKeyColumnName(String)} have not been called.
     */
    @NonNull
    public String build() {
        if (null == mViewName) {
            throw new IllegalStateException("view name has not been set"); //$NON-NLS-1$
        }
        if (null == mParentTableName) {
            throw new IllegalStateException("parent table name has not been set"); //$NON-NLS-1$
        }
        if (null == mKeyColumnName) {
            throw new IllegalStateException("key column name has not been set"); //$NON-NLS-1$
        }
        if (null == mVersionColumnName) {
            throw new IllegalStateException("version column name has not been set"); //$NON-NLS-1$
        }

        @NonNull final String sqlFormat = "CREATE VIEW %1$s AS SELECT * FROM %2$s WHERE %3$s IN (SELECT MAX(%3$s) FROM %2$s GROUP BY %4$s)"; //$NON-NLS

        return String.format(Locale.US, sqlFormat, mViewName, mParentTableName, mVersionColumnName, mKeyColumnName);
    }
}
