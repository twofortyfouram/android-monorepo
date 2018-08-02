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

import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

/**
 * Idle handler with logging to track how much CPU time Handler threads are using.
 *
 * This class must be constructed on the thread that it is tracking.  The easiest way to add it to
 * a Handler would be to post a runnable to the Handler immediately after construction.  {@link
 * #getInitRunnable()} provides an easy way to do that.
 */
@NotThreadSafe
public final class LoggingIdleHandler implements MessageQueue.IdleHandler {

    private final long mInitCpuTimeNanos;

    private long mLastIdleCpuTimeNanos;

    public LoggingIdleHandler() {
        mInitCpuTimeNanos = Debug.threadCpuTimeNanos();
        mLastIdleCpuTimeNanos = Debug.threadCpuTimeNanos();
    }

    @Override
    public boolean queueIdle() {
        final long currentCpuTimeNanos = Debug.threadCpuTimeNanos();

        final long cumulativeThreadTimeNanos = currentCpuTimeNanos - mInitCpuTimeNanos;
        final long cpuTimeSinceLastIdleNanos = currentCpuTimeNanos - mLastIdleCpuTimeNanos;

        mLastIdleCpuTimeNanos = currentCpuTimeNanos;

        Lumberjack
                .v("Idling... CPU time: cumulative=%d [milliseconds], lastUnitOfWork=%d [milliseconds]",
                        //$NON-NLS-1$
                        cumulativeThreadTimeNanos / 1000000,
                        cpuTimeSinceLastIdleNanos / 1000000);

        return true;
    }

    @NonNull
    @AnyThread
    public static Runnable getInitRunnable() {
        return new InitRunnable();
    }

    @ThreadSafe
    private static final class InitRunnable implements Runnable {

        @Override
        public void run() {
            final LoggingIdleHandler idleHandler = new LoggingIdleHandler();

            Looper.myQueue().addIdleHandler(new LoggingIdleHandler());
        }
    }
}
