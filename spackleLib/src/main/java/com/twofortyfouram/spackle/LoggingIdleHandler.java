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

import android.os.Debug;
import android.os.Looper;
import android.os.MessageQueue;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.internal.Constants;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Idle handler with logging to track how much CPU time Handler threads are using.
 * <p>
 * This class must be constructed on the thread that it is tracking.  The easiest way to add it to
 * a Handler would be to post a runnable to the Handler immediately after construction.  {@link
 * #getInitRunnable()} provides an easy way to do that.
 */
@NotThreadSafe
public final class LoggingIdleHandler implements MessageQueue.IdleHandler {

    /**
     * Map of thread name to approximate cumulative duration in nanoseconds
     * that thread has used the CPU.
     */
    @NonNull
    private static final ConcurrentHashMap<String, AtomicLong> sCpuCumulativeUsage
            = new ConcurrentHashMap<>();

    /**
     * Map of thread name to approximate cumulative idle counts, which provides an indication of
     * how often a thread is being tasked with work.
     */
    @NonNull
    private static final ConcurrentHashMap<String, AtomicLong> sCumulativeIdles
            = new ConcurrentHashMap<>();

    /**
     * Dumps cumulative CPU usage.
     *
     * @return A map of thread name and cumulative duration of thread time in nanos.
     */
    @NonNull
    public static Map<String, Long> dumpCpuUsageInNanos() {
        @NonNull final Map<String, Long> threadCumulativeUsageToReturn = new HashMap<>();

        /*
         * Note that the iterator does not lock the map.  The read is thread safe, but it is not
         * atomic.  In other words, between starting the loop and ending the loop, some cumulative
         * usages could be incremented. The results returned by this method are therefore
         * approximate.
         */
        for (@NonNull final Map.Entry<String, AtomicLong> entry : sCpuCumulativeUsage.entrySet()) {
            threadCumulativeUsageToReturn.put(entry.getKey(), entry.getValue().get());
        }

        return threadCumulativeUsageToReturn;
    }

    /**
     * Dumps cumulative idle counts.
     *
     * @return A map of thread name and cumulative number of times a thread went idle.
     */
    @NonNull
    public static Map<String, Long> dumpIdleCounts() {
        @NonNull final Map<String, Long> threadCumulativeUsageToReturn = new HashMap<>();

        /*
         * Note that the iterator does not lock the map.  The read is thread safe, but it is not
         * atomic.  In other words, between starting the loop and ending the loop, some cumulative
         * usages could be incremented. The results returned by this method are therefore
         * approximate.
         */
        for (@NonNull final Map.Entry<String, AtomicLong> entry : sCumulativeIdles.entrySet()) {
            threadCumulativeUsageToReturn.put(entry.getKey(), entry.getValue().get());
        }

        return threadCumulativeUsageToReturn;
    }

    /**
     * Reference to the thread's AtomicLong in {@link #sCpuCumulativeUsage}.  Avoids the map lookup each time it needs
     * to be updated.
     */
    @NonNull
    private final AtomicLong mNanosReference;

    /**
     * Reference to the thread's AtomicLong in {@link #sCumulativeIdles}.  Avoids the map lookup each time it needs
     * to be updated.
     */
    @NonNull
    private final AtomicLong mCountReference;

    private final long mInitCpuTimeNanos;

    private long mLastIdleCpuTimeNanos;

    public LoggingIdleHandler() {
        @Nullable final String threadName = Thread.currentThread().getName();

        @NonNull final AtomicLong nanosAtomicLong;
        {
            @NonNull final AtomicLong newAtomicLong = new AtomicLong(0);
            @Nullable final AtomicLong oldAtomicLong = sCpuCumulativeUsage.putIfAbsent(threadName, newAtomicLong);

            if (null == oldAtomicLong) {
                nanosAtomicLong = newAtomicLong;
            } else {
                nanosAtomicLong = oldAtomicLong;
            }
        }
        mNanosReference = nanosAtomicLong;

        @NonNull final AtomicLong countAtomicLong;
        {
            @NonNull final AtomicLong newAtomicLong = new AtomicLong(0);
            @Nullable final AtomicLong oldAtomicLong = sCumulativeIdles.putIfAbsent(threadName, newAtomicLong);

            if (null == oldAtomicLong) {
                countAtomicLong = newAtomicLong;
            } else {
                countAtomicLong = oldAtomicLong;
            }
        }
        mCountReference = countAtomicLong;

        mInitCpuTimeNanos = Debug.threadCpuTimeNanos();
        mLastIdleCpuTimeNanos = Debug.threadCpuTimeNanos();
    }

    @Override
    public boolean queueIdle() {
        mCountReference.getAndIncrement();

        final long currentCpuTimeNanos = Debug.threadCpuTimeNanos();

        final long cumulativeThreadTimeNanos = currentCpuTimeNanos - mInitCpuTimeNanos;
        final long cpuTimeSinceLastIdleNanos = currentCpuTimeNanos - mLastIdleCpuTimeNanos;

        mLastIdleCpuTimeNanos = currentCpuTimeNanos;

        if (Constants.IS_LOGGING_ENABLED) {
            Lumberjack
                    .v("Idling... count=%d CPU time: cumulative=%d [milliseconds], lastUnitOfWork=%d [milliseconds]",
                            //$NON-NLS-1$
                            mCountReference.get(),
                            TimeUnit.MILLISECONDS.convert(cumulativeThreadTimeNanos, TimeUnit.NANOSECONDS),
                            TimeUnit.MILLISECONDS.convert(cpuTimeSinceLastIdleNanos, TimeUnit.NANOSECONDS));
        }

        mNanosReference.set(cumulativeThreadTimeNanos);

        return true;
    }

    /**
     *
     * Note this Runnable is intelligent enough to avoid adding duplicate {@link LoggingIdleHandler} instances to the
     * same thread.  E.g. posting two Runnable instances to the same thread will only configure one
     * {@code LoggingIdleHandler}.
     *
     * @return Runnable to post to a Handler to initialize thread usage statistics.
     */
    @NonNull
    @AnyThread
    public static Runnable getInitRunnable() {
        return new InitRunnable();
    }

    @ThreadSafe
    private static final class InitRunnable implements Runnable {

        @NonNull
        private static final ThreadLocal<WeakReference<LoggingIdleHandler>> sLocalLoggingIdleHandler = new ThreadLocal<>();

        @Override
        public void run() {
            @Nullable final WeakReference<LoggingIdleHandler> weakReference = sLocalLoggingIdleHandler.get();

            if (null == weakReference) {
                setupIdleHandler();
            }
            else {
                @Nullable final LoggingIdleHandler existingIdleHandler = weakReference.get();

                if (null == existingIdleHandler) {
                    setupIdleHandler();
                }
            }
        }

        private void setupIdleHandler() {
            @NonNull final LoggingIdleHandler idleHandler = new LoggingIdleHandler();

            Looper.myQueue().addIdleHandler(idleHandler);

            sLocalLoggingIdleHandler.set(new WeakReference<>(idleHandler));
        }
    }

}
