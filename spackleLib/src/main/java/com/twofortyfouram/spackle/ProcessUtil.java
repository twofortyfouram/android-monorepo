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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.List;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.log.Lumberjack.formatMessage;

@ThreadSafe
public final class ProcessUtil {

    /**
     * Intrinsic lock to synchronize {@link #sProcessName}.
     */
    @NonNull
    private static final Object INITIALIZATION_INTRINSIC_LOCK = new Object();

    /**
     * Name of the current process.
     */
    @Nullable
    @GuardedBy("INITIALIZATION_INTRINSIC_LOCK")
    private static volatile String sProcessName = null;

    /**
     * @param context Application context.
     * @return name of the current process.
     */
    @NonNull
    public static String getProcessName(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        final Context applicationContext = ContextUtil.cleanContext(context);

        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd
         * edition page 283.
         */
        String processName = sProcessName;
        if (null == processName) {
            synchronized (INITIALIZATION_INTRINSIC_LOCK) {
                processName = sProcessName;
                if (null == processName) {
                    final ActivityManager activityManager = (ActivityManager) applicationContext
                            .getSystemService(Context.ACTIVITY_SERVICE);

                    String temp = null;

                    // Search through running apps
                    {
                        /*
                         * List of running processes shouldn't be null, but crash reports have shown
                         * this can happen.
                         */
                        final List<RunningAppProcessInfo> runningApps = activityManager
                                .getRunningAppProcesses();
                        if (null != runningApps) {
                            for (final RunningAppProcessInfo processInfo : runningApps) {
                                if (android.os.Process.myPid() == processInfo.pid) {
                                    temp = processInfo.processName;
                                }
                            }
                        }
                    }

                    // Search through running services
                    {
                        /*
                         * List of running services shouldn't be null, but crash reports have shown
                         * this can happen.
                         */
                        final List<ActivityManager.RunningServiceInfo> runningServiceInfos
                                = activityManager
                                .getRunningServices(
                                        Integer.MAX_VALUE);
                        if (null != runningServiceInfos) {
                            if (null == temp) {
                                for (ActivityManager.RunningServiceInfo runningService : runningServiceInfos) {
                                    if (android.os.Process.myPid() == runningService.pid) {
                                        temp = runningService.process;
                                    }
                                }
                            }
                        }
                    }

                    sProcessName = processName = temp;
                }
            }
        }

        if (null == processName) {
            throw new RuntimeException(formatMessage("Process with pid %s not found",
                    android.os.Process.myPid())); //$NON-NLS-1$
        }

        return processName;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be
     *                                       instantiated.
     */
    private ProcessUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
