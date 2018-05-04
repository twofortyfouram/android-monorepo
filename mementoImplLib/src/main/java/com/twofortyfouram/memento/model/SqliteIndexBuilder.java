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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.jcip.annotations.NotThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Builds SQL statements to create new database indices. The intention of this class is to avoid
 * common SQL syntax errors when building the SQL statements by hand. This class is not intended to
 * handle all possible index configurations, but instead is intended for most everyday use cases.
 * <p>
 * At a minimum, {@link #setTableName(String)} and {@link #setColumnName(String)} must be called
 * prior to calling {@link #build()}.
 */
@NotThreadSafe
public final class SqliteIndexBuilder {

    /**
     * The name of the table.
     */
    @Nullable
    private String mTableName = null;

    /**
     * The name of the column.
     */
    @Nullable
    private String mColumnName = null;

    /**
     * Sets the name of the table.
     *
     * @param tableName Name of the table.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteIndexBuilder setTableName(@NonNull final String tableName) {
        assertNotNull(tableName, "tableName"); //$NON-NLS-1$
        mTableName = tableName;

        return this;
    }

    /**
     * Sets the name of the column.
     *
     * @param columnName Name of the column.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteIndexBuilder setColumnName(@NonNull final String columnName) {
        assertNotNull(columnName, "columnName"); //$NON-NLS-1$
        mColumnName = columnName;

        return this;
    }

    /**
     * @return The SQL statement to create the index. The name of the index will be
     * tablename_columnname_index.
     * @throws IllegalStateException If {@link #setTableName(String)} or
     *                               {@link #setColumnName(String)} have not been called.
     */
    @NonNull
    public String build() {
        if (null == mTableName) {
            throw new IllegalStateException("table name has not been set"); //$NON-NLS-1$
        }
        if (null == mColumnName) {
            throw new IllegalStateException("column name has not been set"); //$NON-NLS-1$
        }

        return String
                .format(Locale.US,
                        "CREATE INDEX %s_%s_index ON %s(%s)", //NON-NLS
                        mTableName, mColumnName, mTableName,
                        mColumnName);
    }
}
