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

import com.twofortyfouram.spackle.IClock;
import com.twofortyfouram.spackle.test.fixture.MockClockFixture;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class MockClock implements IClock {

    private long mWallTimeMillis;

    private long mRealTimeMillis;

    public MockClock() {
        mWallTimeMillis = MockClockFixture.MOCK_CLOCK_WALL_TIME_MILLIS;
        mRealTimeMillis = MockClockFixture.MOCK_CLOCK_REAL_TIME_MILLIS;
    }

    public MockClock(final long wallTimeMillis, final long realTimeMillis) {
        mRealTimeMillis = realTimeMillis;
        mWallTimeMillis = wallTimeMillis;
    }

    @Override
    public long getWallTimeMillis() {
        return mWallTimeMillis;
    }

    @Override
    public long getRealTimeMillis() {
        return mRealTimeMillis;
    }

    public void setWallTimeMillis(final long wallTimeMillis) {
        mWallTimeMillis = wallTimeMillis;
    }

    public void setRealTimeMillis(final long realTimeMillis) {
        mRealTimeMillis = realTimeMillis;
    }
}
