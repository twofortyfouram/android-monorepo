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

package com.twofortyfouram.memento.test.main_process.model;

import android.content.ContentValues;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.twofortyfouram.memento.test.main_process.contract.TestKeyValueColumns;

public final class TestKeyValueProviderParser {
    @NonNull
    public static ContentValues newContentValues(@NonNull final String key, @Nullable final String value) {
        @NonNull final ContentValues values = new ContentValues(2);
        values.put(TestKeyValueColumns.COLUMN_STRING_KEY, key);
        values.put(TestKeyValueColumns.COLUMN_STRING_VALUE, value);

        return values;

    }
}
