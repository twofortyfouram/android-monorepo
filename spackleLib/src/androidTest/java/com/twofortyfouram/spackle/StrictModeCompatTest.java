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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressLint("NewApi")
@RunWith(AndroidJUnit4.class)
public final class StrictModeCompatTest {

    @SmallTest
    public void nonInstantiable() {
        assertThat(StrictModeCompat.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void setStrictMode_false() {
        ThreadPolicy threadPolicy = null;
        VmPolicy vmPolicy = null;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.GINGERBREAD)) {
            threadPolicy = StrictMode.getThreadPolicy();
            vmPolicy = StrictMode.getVmPolicy();
        }

        try {
            StrictModeCompat.setStrictMode(false);
        } finally {
            if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.GINGERBREAD)) {
                StrictMode.setThreadPolicy(threadPolicy);
                StrictMode.setVmPolicy(vmPolicy);
            }
        }
    }

    @SmallTest
    @Test
    public void setStrictMode_true() {
        ThreadPolicy threadPolicy = null;
        VmPolicy vmPolicy = null;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.GINGERBREAD)) {
            threadPolicy = StrictMode.getThreadPolicy();
            vmPolicy = StrictMode.getVmPolicy();
        }

        try {
            StrictModeCompat.setStrictMode(true);
        } finally {
            if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.GINGERBREAD)) {
                StrictMode.setThreadPolicy(threadPolicy);
                StrictMode.setVmPolicy(vmPolicy);
            }
        }
    }

    @SmallTest
    @Test
    public void noteSlowCall() {
        /*
         * The result of this method cannot be easily tested, however calling it at least ensures
         * that no exceptions are thrown.
         */
        StrictModeCompat.noteSlowCall("test slow call"); //$NON-NLS-1$
    }
}
