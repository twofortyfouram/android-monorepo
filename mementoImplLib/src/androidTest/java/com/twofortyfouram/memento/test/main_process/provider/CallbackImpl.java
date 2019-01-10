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

package com.twofortyfouram.memento.test.main_process.provider;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import android.database.Cursor;
import androidx.annotation.NonNull;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.model.SqliteColumnBuilder;
import com.twofortyfouram.memento.model.SqliteLatestViewBuilder;
import com.twofortyfouram.memento.model.SqliteStorageClass;
import com.twofortyfouram.memento.model.SqliteTableBuilder;
import com.twofortyfouram.memento.test.main_process.contract.TestKeyValueColumns;
import com.twofortyfouram.memento.test.main_process.contract.KeyValueContract;
import com.twofortyfouram.memento.test.main_process.contract.LatestKeyValueContractView;
import com.twofortyfouram.memento.test.main_process.contract.TestTableOneContract;


public final class CallbackImpl extends SupportSQLiteOpenHelper.Callback {

    private static final int DB_VERSION = 1;

    CallbackImpl() {
        super(DB_VERSION);
    }

    @Override
    public void onCreate(final SupportSQLiteDatabase db) {
        try (final Cursor cursor = db.query("select sqlite_version()")) { //$NON-NLS
            Lumberjack.v("SQLite library version is: %s", cursor); //$NON-NLS
        }

        createTableOne(db);
        createKeyValueTable(db);
        createLatestKeyValueView(db);
    }

    @Override
    public void onUpgrade(@NonNull final SupportSQLiteDatabase db, @NonNull final int oldVersion,
            final int newVersion) {

    }

    @Override
    public void onConfigure(@NonNull final SupportSQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    private static void createTableOne(@NonNull final SupportSQLiteDatabase db) {
        @NonNull final SqliteTableBuilder tableBuilder = new SqliteTableBuilder()
                .setName(TestTableOneContract.TABLE_NAME);
        tableBuilder.addColumn(new SqliteColumnBuilder().setName(TestTableOneContract._ID)
                .setType(SqliteStorageClass.INTEGER)
                .setAutoincrementPrimaryKey());
        tableBuilder.addColumn(new SqliteColumnBuilder()
                .setName(TestTableOneContract.COLUMN_STRING_COLUMN_ONE)
                .setType(SqliteStorageClass.TEXT).setConstraintNotNull());

        db.execSQL(tableBuilder.build());
    }

    private static void createKeyValueTable(@NonNull final SupportSQLiteDatabase db) {
        @NonNull final SqliteTableBuilder tableBuilder = new SqliteTableBuilder()
                .setName(KeyValueContract.TABLE_NAME);
        tableBuilder.addColumn(new SqliteColumnBuilder().setName(KeyValueContract._ID)
                .setType(SqliteStorageClass.INTEGER)
                .setAutoincrementPrimaryKey());
        tableBuilder.addColumn(new SqliteColumnBuilder()
                .setName(KeyValueContract.COLUMN_STRING_KEY)
                .setType(SqliteStorageClass.TEXT).setConstraintNotNull());
        tableBuilder.addColumn(new SqliteColumnBuilder()
                .setName(KeyValueContract.COLUMN_STRING_VALUE)
                .setType(SqliteStorageClass.TEXT));

        db.execSQL(tableBuilder.build());
    }

    private static void createLatestKeyValueView(@NonNull final SupportSQLiteDatabase db) {
        @NonNull final SqliteLatestViewBuilder viewBuilder = new SqliteLatestViewBuilder()
                .setViewName(LatestKeyValueContractView.VIEW_NAME);
        viewBuilder.setFromTableName(KeyValueContract.TABLE_NAME);
        viewBuilder.setVersionColumnName(TestKeyValueColumns._ID);
        viewBuilder.setKeyColumnName(TestKeyValueColumns.COLUMN_STRING_KEY);

        db.execSQL(viewBuilder.build());
    }
}
