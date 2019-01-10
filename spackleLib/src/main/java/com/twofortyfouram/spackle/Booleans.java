/*
 * android-spackle
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

package com.twofortyfouram.spackle;

import net.jcip.annotations.Immutable;

/**
 * Helper to deal with conversion of boolean to integer representations, often required for SQLite or Parcelable.
 */
@Immutable
public class Booleans {
    public static final int TRUE = 1;

    public static final int FALSE = 0;

    public static boolean fromInt(final int toConvert) {
        return FALSE != toConvert;
    }

    public static int toInt(final boolean toConvert) {
        return toConvert ? TRUE : FALSE;
    }

    private Booleans() {
        throw new UnsupportedOperationException("This class cannot be instantiated");      //$NON-NLS-1$
    }
}
