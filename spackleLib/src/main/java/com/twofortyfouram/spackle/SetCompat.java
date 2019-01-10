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

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArraySet;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;

/**
 * Compatibility class for creating Sets efficiently across different Android platform versions.
 */
@ThreadSafe
public final class SetCompat {

    /**
     * An arbitrary cutoff size to decide to switch between ArrayMap vs. HashMap.
     */
    @VisibleForTesting
    /*package*/ static final int ARRAY_SET_MAX_SIZE_CUTOFF_INCLUSIVE = 500;

    /**
     * Constructs a new Set initialized with {@code capacity}.  This may return different
     * underlying map implementations depending on the platform version.
     *
     * <p>While the implementation of this method could change, the intention is to return
     * {@link ArraySet} when possible, and {@link HashMap} otherwise.</p>
     *
     * @param capacity Capacity of the set.
     * @param <V>      Value type of the set.
     * @return A new map instance.
     */
    @NonNull
    public static final <V> Set<V> newSet(@IntRange(from = 0) final int capacity) {
        assertInRangeInclusive(capacity, 0, Integer.MAX_VALUE, "capacity"); //$NON-NLS-1$

        final Set<V> map;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.M)
                && ARRAY_SET_MAX_SIZE_CUTOFF_INCLUSIVE >= capacity) {
            map = newArraySet(capacity);
        } else {
            map = new HashSet<>(capacity);
        }

        return map;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.M)
    private static final <V> Set<V> newArraySet(@IntRange(from = 0) final int capacity) {
        return new ArraySet<>(capacity);
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private SetCompat() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
