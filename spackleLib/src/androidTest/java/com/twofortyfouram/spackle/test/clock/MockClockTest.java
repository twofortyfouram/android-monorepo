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

package com.twofortyfouram.spackle.test.clock;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.spackle.test.fixture.MockClockFixture.MOCK_CLOCK_REAL_TIME_MILLIS;
import static com.twofortyfouram.spackle.test.fixture.MockClockFixture.MOCK_CLOCK_WALL_TIME_MILLIS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class MockClockTest {

    @SmallTest
    @Test
    public void newInstance_withoutParameters() {
        final MockClock mockClock = new MockClock();

        assertThat(mockClock.getWallTimeMillis(), is(MOCK_CLOCK_WALL_TIME_MILLIS));
        assertThat(mockClock.getRealTimeMillis(), is(MOCK_CLOCK_REAL_TIME_MILLIS));
    }

    @SmallTest
    @Test
    public void newInstance_withParameters() {
        final MockClock mockClock = new MockClock(MOCK_CLOCK_WALL_TIME_MILLIS,
                MOCK_CLOCK_REAL_TIME_MILLIS);

        assertThat(mockClock.getWallTimeMillis(), is(MOCK_CLOCK_WALL_TIME_MILLIS));
        assertThat(mockClock.getRealTimeMillis(), is(MOCK_CLOCK_REAL_TIME_MILLIS));
    }

    @SmallTest
    @Test
    public void setRealTimeMillis() {
        final long newRealTimeMillisValue = MOCK_CLOCK_REAL_TIME_MILLIS + 1;

        final MockClock mockClock = new MockClock();
        mockClock.setRealTimeMillis(newRealTimeMillisValue);

        assertThat(mockClock.getRealTimeMillis(), is(newRealTimeMillisValue));
    }

    @SmallTest
    @Test
    public void setWallTimeMillis() {
        final long newWallTimeMillisValue = MOCK_CLOCK_WALL_TIME_MILLIS + 1;

        final MockClock mockClock = new MockClock();
        mockClock.setWallTimeMillis(newWallTimeMillisValue);

        assertThat(mockClock.getWallTimeMillis(), is(newWallTimeMillisValue));
    }

}