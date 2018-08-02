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

package com.twofortyfouram.spackle;

import androidx.annotation.NonNull;

import com.twofortyfouram.spackle.internal.ClockImpl;

/**
 * Generic clock interface.  This adds a layer of indirection to shield the app implementation
 * from directly depending on system clock facilities, making automated tests easier.
 * Clients should declare a dependency on this interface and use {@link #getInstance()} to retrieve
 * a real instance based on system clock facilities.
 */
public interface Clock {

    /**
     * @return Singleton default instance of a clock based on the Android clock facilities.
     */
    @NonNull
    static Clock getInstance() {
        return ClockImpl.getInstance();
    }

    /**
     * @return The current wall time in epoch milliseconds.
     */
    long getWallTimeMillis();

    /**
     * @return The current real time in milliseconds.
     */
    long getRealTimeMillis();
}
