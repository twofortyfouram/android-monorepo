/*
 * android-assertion https://github.com/twofortyfouram/android-assertion
 * Copyright (C) 2014–2017 two forty four a.m. LLC
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
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class CursorAssertionsTest {

    private static final String[] COLUMNS = new String[0];

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertCursorPositionValid_before() {

        try (final Cursor cursor = new MatrixCursor(COLUMNS)) {
            CursorAssertions.assertCursorPositionValid(cursor);
        }
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertCursorOpen() {

        Cursor cursor = null;
        try {
            cursor = new MatrixCursor(COLUMNS);
        } finally {
            if (null != cursor) {
                cursor.close();
                CursorAssertions.assertCursorOpen(cursor);
            }
        }
    }

}
