/*
 * android-spackle https://github.com/twofortyfouram/android-spackle
 * Copyright (C) 2009–2017 two forty four a.m. LLC
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

package com.twofortyfouram.spackle.test.fixture;

import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.TESTS)
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
public final class MockClockFixture {

    public static final long MOCK_CLOCK_REAL_TIME_MILLIS = 123456789;

    public static final long MOCK_CLOCK_WALL_TIME_MILLIS = 987654321;

    private MockClockFixture() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
