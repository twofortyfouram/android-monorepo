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

package com.twofortyfouram.spackle.power;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.VisibleForTesting;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Manages a partial WakeLock for {@link android.app.Service} in a thread-safe
 * manner. The recommended way to use this class is as follows:
 * <ol>
 * <li>Create a private static final instance of {@link PartialWakeLockForService} within the
 * {@link android.app.Service} subclass that needs a WakeLock.</li>
 * <li>Create a public static method for the Service class that will encapsulate calling
 * {@link #beforeStartingService(Context)} and {@link Context#startService(Intent)}.</li>
 * <li>Within the Service implementation, call {@link #beforeDoingWork(Context)}, and
 * {@link #afterDoingWork(Context)} at the appropriate times.
 * </ol>
 * <p>
 * This class handles scenarios where {@link #beforeStartingService(Context)} isn't always called
 * (for example, the Service is restarted due to {@link android.app.Service#START_STICKY}).
 * <p>
 * Clients of this class must have the permission {@link android.Manifest.permission#WAKE_LOCK}.
 * <p>
 * Warning: Use caution with WakeLocks and multiple processes.  If starting a service
 * in another process, it would be best to send a broadcast to a BroadcastReceiver in the same
 * process as the Service, then let the BroadcastReceiver acquire the WakeLock.
 */
@ThreadSafe
public final class PartialWakeLockForService {

    /**
     * Intrinsic lock for {@link #mWakeLock}.
     */
    @NonNull
    private final Object mIntrinsicLock = new Object();

    /**
     * Name of the WakeLock.
     */
    @NonNull
    private final String mWakeLockName;

    /**
     * Lazily initialized WakeLock to carry over from
     * {@link android.app.Service#startService(android.content.Intent)}.
     */
    @Nullable
    @GuardedBy("mIntrinsicLock")
    private volatile PartialWakeLock mWakeLock = null;

    /**
     * Construct a new WakeLock helper.
     *
     * @param name Name of the WakeLock, used for debugging. Cannot be the empty string.
     */
    public PartialWakeLockForService(@NonNull final String name) {
        assertNotEmpty(name, "name"); //$NON-NLS-1$

        mWakeLockName = name;
    }

    /**
     * @param context Application context.
     * @return WakeLock used by this class instance. Normally this method will not return null
     * unless {@code context} is null and this method has never been called with a non-null
     * context.
     */
    @NonNull
    @VisibleForTesting
    /* package */PartialWakeLock getWakeLock(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        /*
         * Double-checked idiom for lazy initialization, Effective Java 2nd edition page 283.
         */
        @SuppressWarnings("FieldAccessNotGuarded")
        @Nullable PartialWakeLock wakeLock = mWakeLock;
        if (null == wakeLock) {
            synchronized (mIntrinsicLock) {
                wakeLock = mWakeLock;
                if (null == wakeLock) {
                    mWakeLock = wakeLock = PartialWakeLock
                            .newInstance(context, mWakeLockName, true);
                }
            }
        }

        return wakeLock;
    }

    /**
     * Call to issue immediately before calling
     * {@link android.app.Service#startService(android.content.Intent)}.
     *
     * @param context Application context.
     */
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void beforeStartingService(@NonNull final Context context) {
        getWakeLock(context).acquireLock();
    }

    /**
     * Call to issue within prior to starting the service's work, typically in either
     * {@link android.app.Service#onStartCommand(android.content.Intent, int, int)} or
     * {@link android.app.IntentService#onHandleIntent(Intent)}.
     *
     * @param context Application context.
     */
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void beforeDoingWork(@NonNull final Context context) {
        /*
         * The WakeLock might not be held if Android scheduled a restart of a crashed or killed
         * sticky service, so reacquire if not held. There is a slight race condition with this
         * behavior, but the risk is fairly small. The race condition could occur if the Service
         * receives two start requests at the same time: once is by the application itself, and
         * another is Android's restart of the sticky service. If the app itself starts the service
         * first and calls beforeStartingService(Context), then when Android restarts the service
         * this method may already see the lock is held and won't acquire another one. If that
         * happens, then one of the two start requests to the service will not actually have a
         * WakeLock. The risk of this causing problems seems fairly low. If each run of the service
         * does the same thing, then there is really no risk since one instance of the service will
         * have a WakeLock. On the other hand, if each Intent is processed differently (e.g.
         * different action, extras, etc.) then that could cause more problems.
         *
         * In the future, the one solution would be to identify when the service is started from
         * Android versus from the application itself.
         */
        getWakeLock(context).acquireLockIfNotHeld();
    }

    /**
     * Call to issue after the service completes its work.
     * <p>
     * This method MUST always be called, otherwise locks obtained previously by
     * {@link #beforeStartingService(Context)} or {@link #beforeDoingWork(Context)} may never be
     * released. Typically, this should be placed in a {@code finally} block within
     * {@link android.app.Service#onStartCommand(android.content.Intent, int, int)} or
     * {@link android.app.IntentService#onHandleIntent(Intent)}.
     *
     * @param context Application context.
     */
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void afterDoingWork(@NonNull final Context context) {
        getWakeLock(context).releaseLockIfHeld();
    }

    @Override
    public String toString() {
        // mWakeLock may be null
        //noinspection FieldAccessNotGuarded
        return String.format(Locale.US,
                "PartialWakeLockForService [mWakeLockName=%s, mWakeLock=%s]", //$NON-NLS-1$
                mWakeLockName, mWakeLock);
    }

}
