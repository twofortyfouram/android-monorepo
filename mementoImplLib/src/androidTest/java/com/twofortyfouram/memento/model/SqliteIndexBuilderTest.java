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

import androidx.test.filters.SmallTest;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
public final class SqliteIndexBuilderTest {

    @SmallTest
    @Test
    public void setTableAndColumn_once_and_build() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();

        builder.setTableName("some_table"); //$NON-NLS-1$
        builder.setColumnName("some_column"); //$NON-NLS-1$

        assertThat(builder.build(),
                is("CREATE INDEX some_table_some_column_index ON some_table(some_column)")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void testSetTableAndColumn_replace_and_build() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();

        builder.setTableName("some_table"); //$NON-NLS-1$
        builder.setColumnName("some_column"); //$NON-NLS-1$

        builder.setTableName("another_table"); //$NON-NLS-1$
        builder.setColumnName("another_column"); //$NON-NLS-1$

        assertThat(builder.build(), is(
                "CREATE INDEX another_table_another_column_index ON another_table(another_column)"
        )); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void setTable_recycle() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();

        assertThat(builder.setTableName("some_table"), Matchers.sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setColumn_recycle() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();

        assertThat(builder.setColumnName("some_column"), Matchers.sameInstance(builder));
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void build_invalid_missing_table_and_column() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void build_invalid_missing_column() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();
        builder.setTableName("some_table");

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void build_invalid_missing_table() {
        final SqliteIndexBuilder builder = new SqliteIndexBuilder();
        builder.setColumnName("some_column");

        builder.build();
    }
}
