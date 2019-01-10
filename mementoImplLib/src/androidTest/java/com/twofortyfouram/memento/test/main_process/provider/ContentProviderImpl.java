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

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.twofortyfouram.memento.model.SqliteUriMatcher;
import com.twofortyfouram.memento.provider.MementoContentProvider;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Simple concrete implementation of {@link MementoContentProvider} for testing.
 */
@ThreadSafe
public final class ContentProviderImpl extends MementoContentProvider {

    @NonNull
    private static final String DB_FILE_NAME = "com.twofortyfouram.memento.debug.sqlite3";
    //$NON-NLS-1$

    @NonNull
    @Override
    public SqliteUriMatcher newSqliteUriMatcher() {
        return new SqliteUriMatcherImpl(getContext());
    }

    @NonNull
    @Override
    public SupportSQLiteOpenHelper newSqliteOpenHelper() {
        @NonNull final SupportSQLiteOpenHelper.Configuration.Builder config
                = SupportSQLiteOpenHelper.Configuration
                .builder(getContext());

        config.name(DB_FILE_NAME);

        config.callback(new CallbackImpl());

        return new FrameworkSQLiteOpenHelperFactory().create(config.build());
    }
}
