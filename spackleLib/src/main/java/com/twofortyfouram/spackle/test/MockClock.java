/*
 * android-spackle
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

package com.twofortyfouram.spackle.test;

import com.twofortyfouram.spackle.Clock;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class MockClock implements Clock {

    public static final long MOCK_CLOCK_DEFAULT_REAL_TIME_MILLIS = 123456789;

    public static final long MOCK_CLOCK_DEFAULT_WALL_TIME_MILLIS = 987654321;

    private long mWallTimeMillis;

    private long mRealTimeMillis;

    public MockClock() {
        mWallTimeMillis = MOCK_CLOCK_DEFAULT_WALL_TIME_MILLIS;
        mRealTimeMillis = MOCK_CLOCK_DEFAULT_REAL_TIME_MILLIS;

    }

    public MockClock(final long wallTimeMillis, final long realTimeMillis) {
        mWallTimeMillis = wallTimeMillis;
        mRealTimeMillis = realTimeMillis;
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
