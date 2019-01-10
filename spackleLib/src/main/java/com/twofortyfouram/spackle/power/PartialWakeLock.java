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
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.Size;
import androidx.annotation.VisibleForTesting;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.internal.Constants;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Manages partial {@code WakeLock}s.
 * <p>
 * Users of this class must have the permission
 * {@link android.Manifest.permission#WAKE_LOCK}.
 */
/*
 * Android Lint warnings for this class are useless.
 */
@SuppressLint("Wakelock")
@ThreadSafe
public final class PartialWakeLock {

    /**
     * Map of WakeLock name to approximate cumulative duration in milliseconds
     * that lock has been held.
     */
    @NonNull
    private static final ConcurrentHashMap<String, AtomicLong> sWakeLockCumulativeUsage
            = new ConcurrentHashMap<>();

    /**
     * Map of WakeLock name to approximate cumulative counts of when the lock was acquired.
     */
    @NonNull
    private static final ConcurrentHashMap<String, AtomicLong> sWakeLockCumulativeCounts
            = new ConcurrentHashMap<>();

    /**
     * References to outstanding WakeLocks.  Useful to see if any are dangling.
     */
    @NonNull
    private static final Map<PartialWakeLock, Void> sWakeLockReferences
            = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Tags of WakeLocks that were garbage collected while still held.
     */
    @NonNull
    private static final Set<String> sGarbageCollegedLocks = new ConcurrentSkipListSet<>();

    private static final long LEAKED_WAKELOCK_DURATION_MILLIS = 10 * DateUtils.SECOND_IN_MILLIS;

    /**
     * The {@code WakeLock} tag.
     */
    @NonNull
    private final String mLockName;

    /**
     * Flag indicating whether {@link #mWakeLock} is reference counted.
     */
    private final boolean mIsReferenceCounted;

    /**
     * {@code WakeLock} encapsulated by this class.
     */
    @NonNull
    private final PowerManager.WakeLock mWakeLock;

    /**
     * Reference count for the number of times {@link #mWakeLock} has been
     * obtained.
     */
    private int mReferenceCount = 0;

    /**
     * Realtime when {@link #mWakeLock} was acquired.
     */
    private long mAcquiredRealtimeMillis = 0;

    @NonNull
    private final AtomicLong mCumulativeUsage;

    @NonNull
    private final AtomicLong mCumulativeCount;

    /**
     * Dumps cumulative WakeLock usage from this class and {@link PartialWakeLockForService}.
     * This is useful to debug WakeLock usage.
     *
     * @return A map of WakeLock name and cumulative duration in milliseconds that the lock was
     * held.
     */
    @NonNull
    public static Map<String, Long> dumpWakeLockUsageInMillis() {
        final Map<String, Long> wakeLockCumulativeUsageToReturn = new HashMap<>();

        /*
         * Note that the iterator does not lock the map.  The read is thread safe, but it is not
         * atomic.  In other words, between starting the loop and ending the loop, some cumulative
         * usages could be incremented. The results returned by this method are therefore
         * approximate.
         */
        for (@NonNull final Entry<String, AtomicLong> entry : sWakeLockCumulativeUsage.entrySet()) {
            wakeLockCumulativeUsageToReturn.put(entry.getKey(), entry.getValue().get());
        }

        return wakeLockCumulativeUsageToReturn;
    }

    /**
     * Dumps wakelocks that are currently held.  Might help detect locks that are dangling.
     *
     * @return A map of WakeLock name and cumulative duration in milliseconds that the lock was
     * held (if there are multiple dangling with the same tag).
     */
    @NonNull
    @Size(min = 0)
    public static Map<String, Long> dumpActivelyLeakedWakelocks() {
        @NonNull final Map<String, Long> wakeLockCumulativeUsageToReturn = new HashMap<>();

        /*
         * Note that the iterator does not lock the map.  The read is thread safe, but it is not
         * atomic.
         */
        for (@NonNull final PartialWakeLock wakeLock : sWakeLockReferences.keySet()) {
            synchronized (wakeLock.mWakeLock) {
                if (wakeLock.isHeld()) {
                    @NonNull final String tag = wakeLock.mLockName;
                    final long heldDurationMillis = SystemClock.elapsedRealtime() - wakeLock.mAcquiredRealtimeMillis;
                    if (LEAKED_WAKELOCK_DURATION_MILLIS < heldDurationMillis) {
                        if (wakeLockCumulativeUsageToReturn.containsKey(tag)) {
                            wakeLockCumulativeUsageToReturn.put(tag, wakeLockCumulativeUsageToReturn.get(tag) + heldDurationMillis);
                        }
                        else {
                            wakeLockCumulativeUsageToReturn.put(tag, heldDurationMillis);
                        }
                    }
                }
            }
        }

        return wakeLockCumulativeUsageToReturn;
    }

    /**
     * Dumps wakelocks that were garbage collected while still held.
     *
     * @return A set of WakeLock tags.
     */
    @NonNull
    @Size(min = 0)
    public static Set<String> dumpGarbageCollectedLeakedWakeLocks() {
        @NonNull final Set<String> wakeLockTagsToReturn = new HashSet<>(sGarbageCollegedLocks);

        return wakeLockTagsToReturn;
    }

    /**
     * Dumps cumulative WakeLock counts from this class and {@link PartialWakeLockForService}.
     * This is useful to debug WakeLock usage.
     *
     * @return A map of WakeLock name and cumulative number of times the lock was acquired by an
     * {@link #acquireLock()} or {@link #acquireLockIfNotHeld()} call.
     */
    @NonNull
    @Size(min = 0)
    public static Map<String, Long> dumpWakeLockCounts() {
        @NonNull final Map<String, Long> wakeLockCumulativeCountsToReturn = new HashMap<>();

        /*
         * Note that the iterator does not lock the map.  The read is thread safe, but it is not
         * atomic.  In other words, between starting the loop and ending the loop, some cumulative
         * usages could be incremented. The results returned by this method are therefore
         * approximate.
         */
        for (@NonNull final Entry<String, AtomicLong> entry : sWakeLockCumulativeCounts.entrySet()) {
            wakeLockCumulativeCountsToReturn.put(entry.getKey(), entry.getValue().get());
        }

        return wakeLockCumulativeCountsToReturn;
    }

    /**
     * Constructs a new {@code PartialWakeLock}.
     *
     * <p>It is recommended that
     * applications use a finite number of values for {@code lockName}, as an unbounded
     * number of names would create a memory leak.  For example, consider
     * a background service.  Using the name "my_service_lock" every time the service is started
     * would be better than "my_service_lock_%d" where %d is incremented
     * every time the service starts.  Internally this class maintains a historical count of lock
     * durations to enable {@link #dumpWakeLockUsageInMillis()}, so creating an unbounded number of tags
     * would grow linearly in memory usage.</p>
     * <p>It is also recommended to use a hard coded value for the lock name, as opposed to
     * one generated dynamically.  While dynamic names (for example based on class name) are
     * better obfuscated, they don't work well with Android Vitals reports.</p>
     *
     * @param context            Application context.
     * @param lockName           a tag for identifying the lock.
     * @param isReferenceCounted true if the lock is reference counted. False if
     *                           the lock is not reference counted.
     * @return A new {@link PartialWakeLock}.
     */
    @NonNull
    public static PartialWakeLock newInstance(@NonNull final Context context,
                                              @NonNull final String lockName, final boolean isReferenceCounted) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(lockName, "lockName"); //$NON-NLS-1$

        @NonNull final AtomicLong durationAtomicLong;
        {
            @NonNull final AtomicLong newAtomicLong = new AtomicLong(0);
            @Nullable final AtomicLong oldAtomicLong = sWakeLockCumulativeUsage.putIfAbsent(lockName, newAtomicLong);

            if (null == oldAtomicLong) {
                durationAtomicLong = newAtomicLong;
            } else {
                durationAtomicLong = oldAtomicLong;
            }
        }

        @NonNull final AtomicLong countAtomicLong;
        {
            @NonNull final AtomicLong newAtomicLong = new AtomicLong(0);
            @Nullable final AtomicLong oldAtomicLong = sWakeLockCumulativeCounts.putIfAbsent(lockName, newAtomicLong);

            if (null == oldAtomicLong) {
                countAtomicLong = newAtomicLong;
            } else {
                countAtomicLong = oldAtomicLong;
            }
        }

        @NonNull final PartialWakeLock partialWakeLock = new PartialWakeLock(context, lockName, isReferenceCounted, durationAtomicLong, countAtomicLong);
        sWakeLockReferences.put(partialWakeLock, null);

        return partialWakeLock;
    }

    private PartialWakeLock(@NonNull final Context context, @NonNull final String lockName,
                            final boolean isReferenceCounted, @NonNull final AtomicLong cumulativeUsageMillis, @NonNull final AtomicLong cumulativeAcquireCount) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(lockName, "lockName"); //$NON-NLS-1$
        assertNotNull(cumulativeUsageMillis, "cumulativeUsageMillis"); //$NON-NLS-1$
        assertNotNull(cumulativeAcquireCount, "cumulativeAcquireCount"); //$NON-NLS-1$

        mLockName = lockName;
        mIsReferenceCounted = isReferenceCounted;

        final PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, mLockName);
        mWakeLock.setReferenceCounted(isReferenceCounted);

        mCumulativeUsage = cumulativeUsageMillis;
        mCumulativeCount = cumulativeAcquireCount;
    }

    /**
     * Acquire a partial {@code WakeLock}.
     * <p>
     * This method may be called multiple times. If the lock is reference
     * counted, then each call to this method needs to be balanced by
     * a call to {@link #releaseLock()}. Otherwise if the lock is not reference
     * counted, then multiple calls have no effect.
     */
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    @SuppressLint("WakelockTimeout")
    public void acquireLock() {
        synchronized (mWakeLock) {
            final boolean isHeld = isHeld();

            if (!isHeld) {
                mAcquiredRealtimeMillis = SystemClock.elapsedRealtime();
            }

            if (mIsReferenceCounted || !isHeld) {
                mReferenceCount++;
            }

            mWakeLock.acquire();
            mCumulativeCount.incrementAndGet();

            if (Constants.IS_LOGGING_ENABLED) {
                Lumberjack.v("%s", this); //$NON-NLS-1$
            }
        }
    }

    /**
     * Similar to {@link #acquireLock()}, but only acquires a lock if
     * {@link #isHeld()} returns false.
     */
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void acquireLockIfNotHeld() {
        synchronized (mWakeLock) {
            if (!mWakeLock.isHeld()) {
                acquireLock();
            }
        }
    }

    /**
     * Release a {@code WakeLock} previously acquired. This method should
     * balance a call to {@link #acquireLock()}.
     *
     * @throws IllegalStateException if the {@code WakeLockManager} is
     *                               underlocked.
     */
    public void releaseLock() {
        synchronized (mWakeLock) {
            if (isHeld()) {
                mReferenceCount--;
                mWakeLock.release();

                if (Constants.IS_LOGGING_ENABLED) {
                    Lumberjack.v("%s", this); //$NON-NLS-1$
                }

                if (!isHeld()) {
                    //noinspection AccessToStaticFieldLockedOnInstance
                    mCumulativeUsage.addAndGet(getHeldDurationMillis());
                    mAcquiredRealtimeMillis = 0;
                }
            } else {
                throw new IllegalStateException(Lumberjack.formatMessage(
                        "Lock \"%s\" was not held", mLockName)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Like {@link #releaseLock()} but only releases if {@link #isHeld()}
     * returns true. This method will not throw exceptions for being
     * underlocked.
     */
    public void releaseLockIfHeld() {
        synchronized (mWakeLock) {
            if (isHeld()) {
                releaseLock();
            }
        }
    }

    /**
     * Determine whether a {@code WakeLock} is held.
     *
     * @return {@code true} if a lock is held. Otherwise returns {@code false}.
     */
    public boolean isHeld() {
        synchronized (mWakeLock) {
            return mWakeLock.isHeld();
        }
    }

    /**
     * @return The number of references held for this lock. If the lock is not
     * reference counted, then the maximum value this method will return
     * is 1.
     */
    @VisibleForTesting
    /* package */int getReferenceCount() {
        synchronized (mWakeLock) {
            return mReferenceCount;
        }
    }

    /**
     * @return The duration the lock has been held, or 0 if the lock is not
     * held.
     */
    private long getHeldDurationMillis() {
        synchronized (mWakeLock) {
            final long acquiredRealtimeMillis = mAcquiredRealtimeMillis;

            final long durationMillis;
            if (0 == acquiredRealtimeMillis) {
                durationMillis = 0;
            } else {
                durationMillis = SystemClock.elapsedRealtime() - acquiredRealtimeMillis;
            }

            return durationMillis;
        }
    }

    @Override
    public String toString() {
        return String
                .format(Locale.US,
                        "PartialWakeLock [mLockName=%s, mIsReferenceCounted=%s, mReferenceCount=%s, durationHeldMillis=%d, mWakeLock=%s]",
                        //$NON-NLS-1$
                        mLockName, mIsReferenceCounted, mReferenceCount, getHeldDurationMillis(),
                        mWakeLock);
    }

    @Override
    protected void finalize() throws Throwable {
        synchronized (mWakeLock) {
            if (isHeld()) {
                sGarbageCollegedLocks.add(mLockName);
            }
        }

        super.finalize();
    }
}
