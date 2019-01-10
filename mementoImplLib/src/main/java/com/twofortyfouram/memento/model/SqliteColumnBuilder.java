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

import net.jcip.annotations.NotThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Builds SQL statements to create new database columns. The intention of this class is to avoid
 * common SQL syntax errors when building the SQL statements by hand. This class is not intended to
 * handle all possible column configurations, but instead is intended for most everyday use cases.
 * <p>
 * At a minimum, these two calls must be made: {@link #setName(String)} and
 * {@link #setType(SqliteStorageClass)}.
 * <p>
 * Additionally, constraints may be added to the column. Not all constraints are compatible with
 * each other, although this class does not enforce those limitations. Those errors will usually
 * only become evident when trying to execute the resulting SQL statement.
 */
@NotThreadSafe
public final class SqliteColumnBuilder {

    /**
     * Name of the column.
     */
    @Nullable
    private String mColumnName = null;

    /**
     * Type of the column.
     */
    @Nullable
    private SqliteStorageClass mType = null;

    /**
     * True if null is not allowed. False if null is allowed.
     */
    private boolean mIsNotNull = false;

    /**
     * True if empty is not allowed. False if empty is allowed.
     */
    private boolean mIsNotEmpty = false;

    /**
     * True if the column must be unique. False if the column is not unique.
     */
    private boolean mIsUnique = false;

    /**
     * True if the column is an autoincrement primary key.
     */
    private boolean mIsAutoIncrementPrimaryKey = false;

    /**
     * Table for building a foreign key constraint.
     */
    @Nullable
    private String mForeignTable = null;

    /**
     * Column for building a foreign key constraint.
     */
    @Nullable
    private String mForeignColumn = null;

    /**
     * Whether deletion of foreign key is cascaded.
     */
    private boolean mIsCascadeDelete = false;

    /**
     * Lower bound constraint.
     */
    @Nullable
    private Long mLowerBound = null;

    /**
     * Upper bound constraint.
     */
    @Nullable
    private Long mUpperBound = null;

    /**
     * Set of elements constraint.
     */
    @Nullable
    private String[] mConstraintSet = null;

    /**
     * Sets the column to be an autoincrement primary key.
     *
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setAutoincrementPrimaryKey() {
        mIsAutoIncrementPrimaryKey = true;

        return this;
    }

    /**
     * @param name Name to set for the column.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setName(@NonNull final String name) {
        assertNotNull(name, "name"); //$NON-NLS-1$
        mColumnName = name;

        return this;
    }

    /**
     * @param type Type to set for the column.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setType(@NonNull final SqliteStorageClass type) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        mType = type;

        return this;
    }

    /**
     * Sets the constraint that the column must not be null.
     * <p>
     * By default, null is allowed.
     *
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setConstraintNotNull() {
        mIsNotNull = true;

        return this;
    }

    /**
     * Sets the constraint that the column must not be empty ''.
     * <p>
     * By default, empty is allowed.
     *
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setConstraintNotEmpty() {
        mIsNotEmpty = true;

        return this;
    }

    /**
     * Sets the constraint that the column must be unique.
     * <p>
     * By default, columns have no uniqueness constraint.
     *
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setConstraintUnique() {
        mIsUnique = true;

        return this;
    }

    /**
     * @param foreignTable    Table that contains the foreign key.
     * @param foreignColumn   Column in {@code foreignTable} that contains the key.
     * @param isCascadeDelete Whether deletions are cascaded.  Note: Normally this is not
     *                        recommended,
     *                        because content change notifications will not be sent correctly.
     *                        Although the database knows about cascade, the ContentProvider layer
     *                        does not.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setForeignKey(@NonNull final String foreignTable,
            @NonNull final String foreignColumn, final boolean isCascadeDelete) {
        assertNotEmpty(foreignTable, "foreignTable"); //$NON-NLS-1$
        assertNotEmpty(foreignColumn, "foreignColumn"); //$NON-NLS-1$

        mForeignTable = foreignTable;
        mForeignColumn = foreignColumn;
        mIsCascadeDelete = isCascadeDelete;

        return this;
    }

    /**
     * Sets a bounding constraint on integer values.
     *
     * @param lowerBoundInclusive Lower bound on the range.
     * @param upperBoundInclusive Upper bound on the range
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setConstraintRange(final long lowerBoundInclusive,
            final long upperBoundInclusive) {
        assertInRangeInclusive(upperBoundInclusive, lowerBoundInclusive, Long.MAX_VALUE,
                "upperBoundInclusive"); //$NON-NLS-1$

        mLowerBound = lowerBoundInclusive;
        mUpperBound = upperBoundInclusive;

        return this;
    }

    /**
     * Sets a constraint for a column to be in a set of values.
     *
     * @param args A set of allowed values for the column to contain.
     * @return The builder for chained calls.
     */
    @NonNull
    public SqliteColumnBuilder setConstraintSet(@NonNull final String... args) {
        assertNotNull(args, "args"); //$NON-NLS-1$

        @NonNull final String[] arrayCopy = new String[args.length];
        System.arraycopy(args, 0, arrayCopy, 0, args.length);

        mConstraintSet = arrayCopy;

        return this;
    }

    /**
     * @return SQL to create the column.
     * @throws IllegalStateException If a column name or type has not been set.
     */
    @NonNull
    public String build() {
        if (null == mColumnName) {
            throw new IllegalStateException("column name must be set"); //$NON-NLS-1$
        }

        if (null == mType) {
            throw new IllegalStateException("type must be set"); //$NON-NLS-1$
        }

        @NonNull final StringBuilder builder = new StringBuilder();

        builder.append(mColumnName);
        builder.append(" "); //$NON-NLS-1$
        builder.append(mType);

        if (mIsAutoIncrementPrimaryKey) {
            builder.append(" PRIMARY KEY AUTOINCREMENT"); //$NON-NLS-1$
        }

        if (mIsNotNull) {
            builder.append(" NOT NULL"); //$NON-NLS-1$
        }

        if (mIsUnique) {
            builder.append(" UNIQUE"); //$NON-NLS-1$
        }

        if (null != mForeignTable && null != mForeignColumn) {
            builder.append(" REFERENCES "); //$NON-NLS-1$
            builder.append(mForeignTable);
            builder.append("("); //$NON-NLS-1$
            builder.append(mForeignColumn);
            builder.append(")"); //$NON-NLS-1$

            if (mIsCascadeDelete) {
                builder.append(" ON DELETE CASCADE"); //$NON-NLS
            }
        }

        if (mIsNotEmpty) {
            builder.append(String.format(Locale.US,
                    " CHECK(%s != '')", mColumnName));  //$NON-NLS-1$
        }

        if (null != mLowerBound && null != mUpperBound) {
            builder.append(String.format(Locale.US,
                    " CHECK(%s >= %d AND %s <= %d)", mColumnName, mLowerBound, //$NON-NLS-1$
                    mColumnName, mUpperBound));
        }

        if (null != mConstraintSet) {
            builder.append(buildCheckSet(mColumnName, mConstraintSet));
        }

        return builder.toString();
    }

    @NonNull
    private static String buildCheckSet(@NonNull final String columnName,
            @NonNull final String[] set) {
        final int length = set.length;
        @NonNull final StringBuilder csv = new StringBuilder();
        for (int x = 0; x < length; x++) {
            csv.append("\"");
            csv.append(set[x]);
            csv.append("\"");
            if (x < length - 1) {
                csv.append(", "); //$NON-NLS-1$
            }
        }

        return String
                .format(Locale.US, " CHECK(%s IN(%s))", columnName, csv.toString()); //$NON-NLS-1$
    }

}
