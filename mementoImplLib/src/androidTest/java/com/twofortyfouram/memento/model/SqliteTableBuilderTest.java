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
import com.twofortyfouram.memento.model.SqliteTableBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class SqliteTableBuilderTest {

    @SmallTest
    @Test
    public void setTableName_once_and_build() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        builder.setName("test_table"); //$NON-NLS-1$

        assertThat(builder.build(), is("CREATE TABLE test_table")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void testSetTableName_replace_and_build() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        builder.setName("test_table"); //$NON-NLS-1$
        builder.setName("test_table_two"); //$NON-NLS-1$

        assertThat(builder.build(), is("CREATE TABLE test_table_two")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void setTableName_recycle() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        assertThat(builder.setName("test_table"), sameInstance(builder)); //$NON-NLS-1$
    }


    @SmallTest
    @Test
    public void setColumn_and_build() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        builder.setName("test_table"); //$NON-NLS-1$
        builder.addColumn(new SqliteColumnBuilder()
                .setName("test_column").setType(SqliteStorageClass.INTEGER)); //$NON-NLS-1$

        assertThat(builder.build(),
                is("CREATE TABLE test_table (test_column INTEGER)")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void setColumn_recycle() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        assertThat(builder.addColumn(new SqliteColumnBuilder()
                        .setName("test_column").setType(SqliteStorageClass.INTEGER)), //$NON-NLS-1$
                sameInstance(builder));
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid() {
        final SqliteTableBuilder builder = new SqliteTableBuilder();

        builder.build();
    }
}
