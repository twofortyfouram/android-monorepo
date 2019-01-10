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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class SqliteLatestViewBuilderTest {

    @SmallTest
    @Test
    public void setAllAndBuild() {
        @NonNull final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        builder.setViewName("latest_things_view");
        builder.setFromTableName("things");
        builder.setKeyColumnName("key");
        builder.setVersionColumnName("_ID");

        final String actual = builder.build();

        final String expected = "CREATE VIEW latest_things_view AS SELECT * FROM things WHERE _ID IN (SELECT MAX(_ID) FROM things GROUP BY key)";

        assertThat(actual, is(expected));
    }

    @SmallTest
    @Test
    public void setViewName_builder() {
        @NonNull final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        final SqliteLatestViewBuilder builder2 = builder.setViewName("latest_things_view");

        assertThat(builder2, notNullValue());
        assertThat(builder2, sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setFromTableName_builder() {
        @NonNull final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        final SqliteLatestViewBuilder builder2 = builder.setFromTableName("things");

        assertThat(builder2, notNullValue());
        assertThat(builder2, sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setKeyColumnName_builder() {
        @NonNull final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        final SqliteLatestViewBuilder builder2 = builder.setKeyColumnName("key");

        assertThat(builder2, notNullValue());
        assertThat(builder2, sameInstance(builder));
    }

    @SmallTest
    @Test
    public void setVersionColumnName_builder() {
        @NonNull final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        final SqliteLatestViewBuilder builder2 = builder.setVersionColumnName("_ID");

        assertThat(builder2, notNullValue());
        assertThat(builder2, sameInstance(builder));
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid() {
        final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_missing_view_name() {
        final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();
        builder.setFromTableName("things");
        builder.setKeyColumnName("key");
        builder.setVersionColumnName("_ID");

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_missing_table_name() {
        final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();
        builder.setViewName("latest_things_view");
        builder.setKeyColumnName("key");
        builder.setVersionColumnName("_ID");

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_missing_key_column() {
        final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();
        builder.setViewName("latest_things_view");
        builder.setFromTableName("things");
        builder.setVersionColumnName("_ID");

        builder.build();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void testBuild_invalid_missing_version_column() {
        final SqliteLatestViewBuilder builder = new SqliteLatestViewBuilder();
        builder.setViewName("latest_things_view");
        builder.setFromTableName("things");
        builder.setKeyColumnName("key");

        builder.build();
    }
}