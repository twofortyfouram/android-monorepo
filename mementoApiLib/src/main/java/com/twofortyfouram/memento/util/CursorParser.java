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

package com.twofortyfouram.memento.util;

import android.database.Cursor;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface CursorParser<T> {
    /**
     * Extracts an object from a Cursor.  This method assumes that the Cursor contains all the columns of the
     * contract and that the Cursor is positioned to a row that is ready to be read. This method will not mutate
     * the Cursor or move the Cursor position.
     *
     * @param cursor Cursor from a query to a contract this parser can handle.
     * @return a new Object.
     * @throws AssertionError If the cursor is closed or the cursor is out of range.
     */
    @NonNull
    @AnyThread
    T newObject(@NonNull final Cursor cursor);
}
