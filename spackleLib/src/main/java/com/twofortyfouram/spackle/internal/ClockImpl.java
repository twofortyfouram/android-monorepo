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

package com.twofortyfouram.spackle.internal;


import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.twofortyfouram.spackle.Clock;

import net.jcip.annotations.ThreadSafe;

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ThreadSafe
public final class ClockImpl implements Clock {

    @NonNull
    private static final Clock CLOCK = new ClockImpl();

    @NonNull
    public static Clock getInstance() {
        return CLOCK;
    }

    /**
     * @return {@link System#currentTimeMillis()}
     */
    @Override
    public long getWallTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * @return {@link SystemClock#elapsedRealtime()}.
     */
    @Override
    public long getRealTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    /**
     * Private constructor to prevent direct instantiation.  Clients should use {@link
     * Clock#getInstance()}.
     */
    private ClockImpl() {

    }

}
