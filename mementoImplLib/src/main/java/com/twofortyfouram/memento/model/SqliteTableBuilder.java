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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Builds SQL statements to create new database tables. The intention of this class is to avoid
 * common SQL syntax errors when building the SQL statements by hand. This class is not intended to
 * handle all possible table configurations, but instead is intended for most everyday use cases.
 * <p>
 * At a minimum, {@link #setName(String)} must be called prior to calling {@link #build()}.
 * <p>
 * Although SQLite does not allow multiple columns with the same name, this class will not enforce
 * this limitation when calling {@link #addColumn(SqliteColumnBuilder)} or {@link #build()}.
 */
@NotThreadSafe
public final class SqliteTableBuilder {

    /**
     * The name of the table.
     */
    @Nullable
    private String mTableName = null;

    /**
     * List of column builders.
     */
    @NonNull
    private final List<SqliteColumnBuilder> mColumns = new LinkedList<>();

    /**
     * Sets the name of the table.
     *
     * @param tableName Name of the table.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteTableBuilder setName(@NonNull final String tableName) {
        assertNotNull(tableName, "tableName"); //$NON-NLS-1$
        mTableName = tableName;

        return this;
    }

    /**
     * Adds a column to the table.
     * <p>
     * When the final SQL statement is built, columns will be created in the order they were added
     * via this method.
     *
     * @param columnBuilder Column to add.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteTableBuilder addColumn(@NonNull final SqliteColumnBuilder columnBuilder) {
        assertNotNull(columnBuilder, "columnBuilder"); //$NON-NLS-1$

        mColumns.add(columnBuilder);

        return this;
    }

    /**
     * @return The SQL statement to create the table.
     * @throws IllegalStateException If {@link #setName(String)} has not been called.
     */
    @NonNull
    public String build() {
        if (null == mTableName) {
            throw new IllegalStateException("table name has not been set"); //$NON-NLS-1$
        }

        final String result;
        if (mColumns.isEmpty()) {
            result = String.format(Locale.US, "CREATE TABLE %s", mTableName); //$NON-NLS-1$
        } else {
            result = String.format(Locale.US,
                    "CREATE TABLE %s (%s)", mTableName, getColumnStatements()); //$NON-NLS-1$
        }

        return result;
    }

    @NonNull
    private String getColumnStatements() {
        final StringBuilder builder = new StringBuilder();

        final Iterator<SqliteColumnBuilder> iterator = mColumns.iterator();
        while (iterator.hasNext()) {
            final SqliteColumnBuilder columnBuilder = iterator.next();
            builder.append(columnBuilder.build());

            if (iterator.hasNext()) {
                builder.append(", "); //$NON-NLS-1$
            }
        }

        return builder.toString();
    }
}
