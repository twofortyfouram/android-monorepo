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

package com.twofortyfouram.memento.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.twofortyfouram.annotation.NonNullElt;

import net.jcip.annotations.NotThreadSafe;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Builds SQL statements to alter existing tables. The intention of this class is to avoid
 * common SQL syntax errors when building the SQL statements by hand. This class is not intended to
 * handle all possible table configurations, but instead is intended for most everyday use cases.
 * <p>
 * At a minimum, {@link #setExistingTableName(String)} must be called prior to calling {@link #build()}.
 * <p>
 * Although SQLite does not allow multiple columns with the same name, this class will not enforce
 * this limitation when calling {@link #addColumn(SqliteColumnBuilder)} or {@link #build()}.
 */
@NotThreadSafe
public final class SqliteTableEditor {

    /**
     * The name of the table.
     */
    @Nullable
    private String mExistingTableName = null;

    /**
     * List of column builders.
     */
    @NonNull
    @Size(min = 0)
    private final List<@NonNullElt SqliteColumnBuilder> mColumnsToAdd = new LinkedList<>();

    /**
     * Sets the name of the table.
     *
     * @param tableName Name of the table.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteTableEditor setExistingTableName(@NonNull final String tableName) {
        assertNotNull(tableName, "tableName"); //$NON-NLS-1$
        mExistingTableName = tableName;

        return this;
    }

    /**
     * Adds a column to the table.
     * <p>
     * When the final SQL statement is built, columns will be created in the order they were added
     * via this method.
     *
     * Note that many constraints cannot be to table when it is modified.
     *
     * @param columnBuilder Column to add.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteTableEditor addColumn(@NonNull final SqliteColumnBuilder columnBuilder) {
        assertNotNull(columnBuilder, "columnBuilder"); //$NON-NLS-1$

        mColumnsToAdd.add(columnBuilder);

        return this;
    }

    /**
     * @return The SQL statement to create the table.
     * @throws IllegalStateException If {@link #setExistingTableName(String)} has not been called.
     */
    @NonNull
    public String build() {
        if (null == mExistingTableName) {
            throw new IllegalStateException("table name has not been set"); //$NON-NLS-1$
        }

        if (mColumnsToAdd.isEmpty()) {
            throw new IllegalStateException("no columns to add"); //$NON-NLS
        }

        @NonNull final String result = String.format(Locale.US,
                "ALTER TABLE %s ADD %s", mExistingTableName, getColumnStatements()); //$NON-NLS-1$

        return result;
    }

    @NonNull
    private String getColumnStatements() {
        @NonNull final StringBuilder builder = new StringBuilder();

        @NonNull final Iterator<SqliteColumnBuilder> iterator = mColumnsToAdd.iterator();
        while (iterator.hasNext()) {
            @NonNull final SqliteColumnBuilder columnBuilder = iterator.next();
            builder.append(columnBuilder.build());

            if (iterator.hasNext()) {
                builder.append(", "); //$NON-NLS-1$
            }
        }

        return builder.toString();
    }
}
