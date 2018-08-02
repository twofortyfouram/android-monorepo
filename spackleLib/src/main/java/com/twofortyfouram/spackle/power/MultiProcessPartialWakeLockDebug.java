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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.annotation.MultiProcessSafe;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.bundle.BundleScrubber;
import com.twofortyfouram.spackle.internal.SpackleInternalPermission;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * For debugging only.
 * <p>This class collects power usage across multiple processes using {@link PartialWakeLock}.  If
 * an application only has a single process, then this is not necessary and calling {@link
 * PartialWakeLock#dumpWakeLockUsage()} is sufficient.</p>
 */
@MultiProcessSafe
@ThreadSafe
@Incubating
public final class MultiProcessPartialWakeLockDebug {

    /**
     * Ordered Intent action to collect power usage across processes.
     *
     * @see #EXTRA_SERIALIZABLE_POWER_USAGE_MAP
     */
    @NonNull
    private static final String ACTION_POWER_USAGE
            = "com.twofortyfouram.spackle.intent.action.POWER_USAGE"; //$NON-NLS

    /**
     * Type: {@code HashMap<String, Long>}.
     * <p>
     * Required extra for {@link #ACTION_POWER_USAGE}.
     * </p>
     */
    @NonNull
    private static final String EXTRA_SERIALIZABLE_POWER_USAGE_MAP
            = "com.twofortyfouram.intent.extra.SERIALIZABLE_MAP_POWER_USAGE"; //$NON-NLS

    @NonNull
    private static final Object INTRINSIC_LOCK = new Object();

    /*
     * Guards against multiple registrations.  While not perfect, this is simple enough.
     */
    @GuardedBy("INTRINSIC_LOCK")
    private static boolean sIsRegistered = false;

    /**
     * Call once from within {@link android.app.Application#onCreate()}.  This enables power usage
     * to be collected across all processes within the package.
     *
     * @param applicationContext Application context.
     */
    public static void register(@NonNull final Context applicationContext) {
        assertNotNull(applicationContext, "applicationContext"); //$NON-NLS

        //noinspection SynchronizationOnStaticField
        synchronized (INTRINSIC_LOCK) {
            if (!sIsRegistered) {
                sIsRegistered = true;

                @NonNull final IntentFilter filter = new IntentFilter(ACTION_POWER_USAGE);
                @NonNull final String permission = SpackleInternalPermission
                        .getSpackleInternalPermission(applicationContext);
                @NonNull final Handler handler = new Handler(
                        HandlerThreadFactory
                                .newHandlerThread("power_usage_receiver", //$NON-NLS
                                        HandlerThreadFactory.ThreadPriority.DEFAULT)
                                .getLooper());

                applicationContext
                        .registerReceiver(new PowerManagementReceiver(), filter, permission,
                                handler);

            } else {
                throw new IllegalStateException("already registered"); //$NON-NLS
            }
        }

    }

    /**
     * @param applicationContext Application context.
     * @return A mapping of WakeLock tag to duration held in milliseconds.
     */
    @NonNull
    @Slow(Slow.Speed.MILLISECONDS)
    public static Map<String, Long> dumpPartialWakeLockUsage(
            @NonNull final Context applicationContext) {
        assertNotNull(applicationContext, "applicationContext"); //$NON-NLS

        StrictMode.noteSlowCall("dumpPartialWakeLockUsage"); //$NON-NLS

        //noinspection SynchronizationOnStaticField
        synchronized (INTRINSIC_LOCK) {
            if (!sIsRegistered) {
                throw new IllegalStateException("call register(Context) first"); //$NON-NLS
            }
        }

        final AtomicReference<Map<String, Long>> ref = new AtomicReference<>();

        final CountDownLatch latch = new CountDownLatch(1);
        final HandlerThread thread = HandlerThreadFactory
                .newHandlerThread("power_usage_ordered_result_receiver",  //$NON-NLS
                        HandlerThreadFactory.ThreadPriority.DEFAULT);
        try {
            @NonNull final Handler handler = new Handler(thread.getLooper());

            // This is not intended to be sent frequently so set FLAG_RECEIVER_FOREGROUND to improve
            // reliability
            // https://issuetracker.google.com/issues/63049704
            @NonNull final Intent intent = new Intent(ACTION_POWER_USAGE)
                    .setPackage(applicationContext.getPackageName())
                    .setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            @NonNull final String permission = SpackleInternalPermission
                    .getSpackleInternalPermission(applicationContext);

            applicationContext.sendOrderedBroadcast(
                    intent,
                    permission,
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(@NonNull final Context context,
                                @NonNull final Intent intent) {
                            @Nullable final Bundle resultExtras = getResultExtras(false);

                            @NonNull Map<String, Long> existingMap = Collections.emptyMap();
                            if (null != resultExtras) {
                                @Nullable final Serializable serializable
                                        = (Serializable) resultExtras
                                        .getSerializable(EXTRA_SERIALIZABLE_POWER_USAGE_MAP);

                                if (null != serializable) {
                                    try {
                                        existingMap = (HashMap<String, Long>) serializable;
                                    } catch (final ClassCastException e) {
                                        existingMap = Collections.emptyMap();
                                    }
                                }
                            }

                            ref.set(Collections.unmodifiableMap(new HashMap<>(existingMap)));
                            latch.countDown();
                        }
                    }, handler, 0, null, null);

            latch.await();
        } catch (final InterruptedException e) {
            Lumberjack.w("Interrupted", e); //$NON-NLS-1$
        } finally {
            thread.getLooper().quit();
        }

        return ref.get();
    }

    @ThreadSafe
    private static final class PowerManagementReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
            if (BundleScrubber.scrub(intent)) {
                return;
            }

            Lumberjack.v("Received %s", intent); //$NON-NLS

            // The receiver in each process will accumulate the total power usage.
            HashMap<String, Long> existingMap;
            {
                @Nullable final Bundle prevResultExtras = getResultExtras(false);
                if (BundleScrubber.scrub(prevResultExtras)) {
                    Lumberjack.w("Extras were bad"); //$NON-NLS
                }

                if (null == prevResultExtras) {
                    existingMap = new HashMap<>();
                } else {
                    try {
                        existingMap = getFromPrevious(prevResultExtras);

                        // Remove null keys, in case a malicious receiver tries to crash us with them.
                        existingMap.remove(null);
                    } catch (final ClassCastException e) {
                        // A malicious process attempted to crash us!
                        existingMap = new HashMap<>();
                    }
                }
            }

            @NonNull final Map<String, Long> wakeLockUsage = PartialWakeLock.dumpWakeLockUsage();

            for (final Map.Entry<String, Long> entry : wakeLockUsage.entrySet()) {
                final String key = entry.getKey();
                final long value = entry.getValue();

                if (existingMap.containsKey(entry.getKey())) {
                    final Long existingMillis = existingMap.get(key);
                    if (null != existingMillis) {
                        existingMap.put(key, value + existingMap.get(key));
                    }
                } else {
                    existingMap.put(key, value);
                }
            }

            @NonNull final Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_SERIALIZABLE_POWER_USAGE_MAP, existingMap);

            setResultExtras(bundle);
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, Long> getFromPrevious(final Bundle prevResultExtras) {
            return (HashMap<String, Long>) prevResultExtras
                    .getSerializable(EXTRA_SERIALIZABLE_POWER_USAGE_MAP);
        }
    }
}
