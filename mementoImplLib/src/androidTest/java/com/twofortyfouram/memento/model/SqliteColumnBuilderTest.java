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

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.model.SqliteColumnBuilder;
import com.twofortyfouram.memento.model.SqliteStorageClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class SqliteColumnBuilderTest {

    @SmallTest
    @Test
    public void setName_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setName("test_column"), sameInstance(builder)); //$NON-NLS
    }

    @SmallTest
    @Test
    public void setType_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setType(SqliteStorageClass.INTEGER), sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setUnique_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setConstraintUnique(), sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setNonNull_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setConstraintNotNull(), sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setForeignKey_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setForeignKey("foreign_table", "foreign_column", false),//$NON-NLS
                sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setRange_recycle() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        assertThat(builder.setConstraintRange(0, 1), sameInstance(builder));
    }

    @SmallTest
    @Test
    public void buildBasic_blob() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.BLOB);

        assertThat(builder.build(), is("test_column BLOB")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_integer() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);

        assertThat(builder.build(), is("test_column INTEGER")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_null() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.NULL);

        assertThat(builder.build(), is("test_column NULL")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_real() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.REAL);

        assertThat(builder.build(), is("test_column REAL")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_replace() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.REAL);

        builder.setName("test_column_two"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.TEXT);

        assertThat(builder.build(), is("test_column_two TEXT")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_unique() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setConstraintUnique();

        assertThat(builder.build(), is("test_column INTEGER UNIQUE")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_not_null() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setConstraintNotNull();

        assertThat(builder.build(), is("test_column INTEGER NOT NULL")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_primary_key() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setAutoincrementPrimaryKey();

        assertThat(builder.build(),
                is("test_column INTEGER PRIMARY KEY AUTOINCREMENT")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_foreign_key_no_cascade() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setForeignKey("foreign_table",//$NON-NLS
                "foreign_column", false); //$NON-NLS

        assertThat(builder.build(),
                is("test_column INTEGER REFERENCES foreign_table(foreign_column)")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_foreign_key_yes_cascade() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setForeignKey("foreign_table",//$NON-NLS
                "foreign_column", true); //$NON-NLS

        assertThat(builder.build(),
                is("test_column INTEGER REFERENCES foreign_table(foreign_column) ON DELETE CASCADE")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_range() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setConstraintRange(52, 77);

        assertThat(builder.build(),
                is("test_column INTEGER CHECK(test_column >= 52 AND test_column <= 77)")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void buildBasic_set() {
        final SqliteColumnBuilder builder = new SqliteColumnBuilder();

        builder.setName("test_column"); //$NON-NLS-1$
        builder.setType(SqliteStorageClass.INTEGER);
        builder.setConstraintSet("0", "1"); //$NON-NLS-1$//$NON-NLS-2$

        assertThat(builder.build(),
                is("test_column INTEGER CHECK(test_column IN(\"0\", \"1\"))")); //$NON-NLS-1$
    }
}
