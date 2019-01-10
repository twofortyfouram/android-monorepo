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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class SqliteTableEditorTest {

    @SmallTest
    @Test
    public void setTableName_recycle() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        assertThat(builder.setExistingTableName("test_table"), sameInstance(builder)); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void addColumn_and_build() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        builder.setExistingTableName("test_table"); //$NON-NLS-1$
        builder.addColumn(new SqliteColumnBuilder()
                .setName("test_column").setType(SqliteStorageClass.INTEGER)); //$NON-NLS-1$

        assertThat(builder.build(),
                is("ALTER TABLE test_table ADD test_column INTEGER")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void addColumn_multiple_and_build() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        builder.setExistingTableName("test_table"); //$NON-NLS-1$
        builder.addColumn(new SqliteColumnBuilder()
                .setName("test_column").setType(SqliteStorageClass.INTEGER)); //$NON-NLS-1$
        builder.addColumn(new SqliteColumnBuilder()
                .setName("test_column2").setType(SqliteStorageClass.BLOB)); //$NON-NLS-1$

        assertThat(builder.build(),
                is("ALTER TABLE test_table ADD test_column INTEGER, test_column2 BLOB")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void addColumn_recycle() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        assertThat(builder.addColumn(new SqliteColumnBuilder()
                        .setName("test_column").setType(SqliteStorageClass.INTEGER)), //$NON-NLS-1$
                sameInstance(builder));
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_nothing_to_modify() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        builder.setExistingTableName("foo");

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_no_name() {
        @NonNull final SqliteTableEditor builder = new SqliteTableEditor();

        builder.addColumn(new SqliteColumnBuilder().setName("bar"));

        builder.build();
    }
}
