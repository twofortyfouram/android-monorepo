/*
 * android-assertion
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


package com.twofortyfouram.assertion;

import android.database.Cursor;
import android.support.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
public final class CursorAssertions {

    /**
     * Checks the cursor is at a valid position for reading data.
     *
     * @throws AssertionError if the position is before or after the last position.
     */
    public static void assertCursorPositionValid(@NonNull final Cursor cursor) {
        assertNotNull(cursor, "cursor"); //$NON-NLS

        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            throw new AssertionError(String.format(Locale.US, "Cursor is at position %d",
                    cursor.getPosition())); //$NON-NLS-1$
        }
    }

    /**
     * Checks the cursor is open.
     *
     * @throws AssertionError if the cursor is closed.
     */
    public static void assertCursorOpen(@NonNull final Cursor cursor) {
        assertNotNull(cursor, "cursor"); //$NON-NLS

        if (cursor.isClosed()) {
            throw new AssertionError("Cursor is closed"); //$NON-NLS-1$
        }
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private CursorAssertions() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
