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

package com.twofortyfouram.memento.test;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import net.jcip.annotations.Immutable;

@Immutable
public final class BaseColumnsCursorFixture {

    @NonNull
    public static Cursor newCountCursor(final int count) {
        @NonNull final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._COUNT}, 1);
        cursor.addRow(new Object[]{count});

        // Forces the result to be immutable and therefore thread-safe
        return new CursorWrapper(cursor);
    }
}
