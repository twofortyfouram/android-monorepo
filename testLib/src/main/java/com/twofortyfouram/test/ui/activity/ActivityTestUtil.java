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

package com.twofortyfouram.test.ui.activity;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.jcip.annotations.ThreadSafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Utility class to facilitate Activity testing.
 *
 * This can now be accomplished with {@link android.support.test.rule.ActivityTestRule}.
 */
@Deprecated
@ThreadSafe
public final class ActivityTestUtil {

    /**
     * Gets the Activity result code, syncing automatically if not called on the UI thread.
     *
     * @param instrumentation Instrumentation to handle threading.
     * @param activity        Activity whose result code is to be obtained.
     * @return Result code of the Activity.
     */
    public static int getActivityResultCodeSync(@NonNull final Instrumentation instrumentation,
            @NonNull final Activity activity) {
        assertNotNull(instrumentation, "instrumentation"); //$NON-NLS-1$
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        //noinspection ObjectEquality
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return getActivityResultCode(activity);
        } else {
            final AtomicInteger resultCode = new AtomicInteger(Activity.RESULT_CANCELED);

            instrumentation.runOnMainSync(new Runnable() {
                @MainThread
                @Override
                public void run() {
                    resultCode.set(getActivityResultCode(activity));
                }
            });
            instrumentation.waitForIdleSync();

            return resultCode.get();
        }
    }

    /**
     * @param activity Activity whose result code is to be obtained.
     * @return Result code of the Activity.
     */
    public static int getActivityResultCode(@NonNull final Activity activity) {
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        /*
         * This is a hack to obtain the Activity result code. There is no official way to check this using the Android
         * testing frameworks, so accessing the internals of the Activity object is the only way. This could
         * break on newer versions of Android.
         */

        try {
            final Field resultCodeField = Activity.class
                    .getDeclaredField("mResultCode"); //$NON-NLS-1$
            resultCodeField.setAccessible(true);
            return (Integer) resultCodeField.get(activity);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets the Activity result Intent, syncing automatically if not called on the UI thread.
     *
     * @param instrumentation Instrumentation to handle threading.
     * @param activity        Activity whose result code is to be obtained.
     * @return Result code of the Activity.
     */
    public static Intent getActivityResultDataSync(@NonNull final Instrumentation instrumentation,
            @NonNull final Activity activity) {
        assertNotNull(instrumentation, "instrumentation"); //$NON-NLS-1$
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        //noinspection ObjectEquality
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return getActivityResultData(activity);
        } else {
            final AtomicReference<Intent> resultIntent = new AtomicReference<>();

            instrumentation.runOnMainSync(new Runnable() {
                @MainThread
                @Override
                public void run() {
                    resultIntent.set(getActivityResultData(activity));
                }
            });
            instrumentation.waitForIdleSync();

            return resultIntent.get();
        }
    }

    /**
     * Helper to get the Activity result Intent.
     *
     * @param activity Activity whose result Intent is to be obtained.
     * @return Result Intent of the Activity.
     */
    @Nullable
    private static Intent getActivityResultData(@NonNull final Activity activity) {
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        /*
         * This is a hack to obtain the Activity result data. There is no official way to check this using the Android
         * testing frameworks, so accessing the internals of the Activity object is the only way. This could
         * break on newer versions of Android.
         */

        try {
            final Field resultIntentField = Activity.class
                    .getDeclaredField("mResultData"); //$NON-NLS-1$
            resultIntentField.setAccessible(true);
            return ((Intent) resultIntentField.get(activity));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a runnable on the main thread. This method works even if the current thread is
     * already the main thread.
     *
     * @param instrumentation to handle threading.
     * @param runnable        to execute.
     */
    /*package*/
    static void autoSyncRunnable(@NonNull final Instrumentation instrumentation,
            @NonNull final Runnable runnable) {
        //noinspection ObjectEquality
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            instrumentation.runOnMainSync(runnable);
            instrumentation.waitForIdleSync();
        }
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private ActivityTestUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
