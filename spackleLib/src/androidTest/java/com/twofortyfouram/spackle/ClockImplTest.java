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

package com.twofortyfouram.spackle;

import android.os.SystemClock;
import android.support.test.filters.FlakyTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class ClockImplTest {

    private static final int SLOP_MILLISECONDS = 100;

    @SmallTest
    @Test
    @FlakyTest
    // Test is flaky because system clock could be reset during a test.  Unlikely but possible.
    public void getWallTimeMillis() {
        final long expected = System.currentTimeMillis();
        final long actual = ClockImpl.getInstance().getWallTimeMillis();

        assertThat((double) actual, closeTo((double) expected, SLOP_MILLISECONDS));
    }

    @SmallTest
    @Test
    public void getRealtTimeMillis() {
        final long expected = SystemClock.elapsedRealtime();
        final long actual = ClockImpl.getInstance().getRealTimeMillis();

        assertThat((double) actual, closeTo((double) expected, SLOP_MILLISECONDS));
    }


    @SmallTest
    @Test
    public void getInstance_type() {
        assertThat(ClockImpl.getInstance(), instanceOf(ClockImpl.class));
    }

    @SmallTest
    @Test
    public void getInstance_non_null() {
        assertThat(ClockImpl.getInstance(), notNullValue());
    }

    @SmallTest
    @Test
    public void getInstance_same_instance() {
        assertThat(ClockImpl.getInstance(), sameInstance(ClockImpl.getInstance()));
    }

}
