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

package com.twofortyfouram.memento.test;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.model.SqliteColumnBuilder;
import com.twofortyfouram.memento.model.SqliteStorageClass;
import com.twofortyfouram.memento.model.SqliteTableBuilder;


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
        final SqliteTableBuilder tableBuilder = new SqliteTableBuilder()
                .setName(TableOneContract.TABLE_NAME);
        tableBuilder.addColumn(new SqliteColumnBuilder().setName(TableOneContract._ID)
                .setType(SqliteStorageClass.INTEGER)
                .setAutoincrementPrimaryKey());
        tableBuilder.addColumn(new SqliteColumnBuilder()
                .setName(TableOneContract.COLUMN_STRING_COLUMN_ONE)
                .setType(SqliteStorageClass.TEXT).setConstraintNotNull());

        db.execSQL(tableBuilder.build());
    }
}
