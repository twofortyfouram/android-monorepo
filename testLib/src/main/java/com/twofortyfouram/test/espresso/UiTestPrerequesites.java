/*
 * android-test
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

package com.twofortyfouram.test.espresso;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.test.InstrumentationRegistry;

import net.jcip.annotations.ThreadSafe;

import org.junit.Before;

/**
 * Subclass this in UI integration tests, e.g. tests that rely on Espresso.  This verifies that
 * prerequisites necessary for reliable UI tests are set so that error messages make sense.
 */
@ThreadSafe
public class UiTestPrerequesites {

    @Before
    public void verifyScreenOn() {
        if (!isScreenOn()) {
            throw new AssertionError("Screen must be on for UI tests to run");//$NON-NLS
        }
    }

    private static boolean isScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return isScreenOnKitKat();
        }
        return isScreenOnLegacy();
    }

    @SuppressWarnings("deprecation")
    private static boolean isScreenOnLegacy() {
        final PowerManager powerService = (PowerManager) InstrumentationRegistry.getContext()
                .getSystemService(Context.POWER_SERVICE);

        return powerService.isScreenOn();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private static boolean isScreenOnKitKat() {

        final PowerManager powerService = (PowerManager) InstrumentationRegistry.getContext()
                .getSystemService(Context.POWER_SERVICE);

        return powerService.isInteractive();
    }
}
