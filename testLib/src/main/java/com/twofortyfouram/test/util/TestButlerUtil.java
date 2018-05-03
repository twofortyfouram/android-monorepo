/*
 * android-test https://github.com/twofortyfouram/android-test
 * Copyright (C) 2014–2017 two forty four a.m. LLC
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

package com.twofortyfouram.test.util;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import net.jcip.annotations.ThreadSafe;

/**
 * Utility class for Test Butler.  This class is safe to use even if Test Butler is not a
 * dependency of the client using this library.
 */
@ThreadSafe
public final class TestButlerUtil {

    @NonNull
    private static final String TEST_BUTLER_PACKAGE_NAME = "com.linkedin.android.testbutler";
    //$NON-NLS

    /**
     * @return True if TestButler is available.  This enables certain tests to disable themselves
     * on environments that don't support TestButler.
     */
    public static boolean isTestButlerAvailable() {
        final PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        try {
            // Expected to throw if TestButler is not installed.
            pm.getPackageInfo(TEST_BUTLER_PACKAGE_NAME, 0);
            return true;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private TestButlerUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
