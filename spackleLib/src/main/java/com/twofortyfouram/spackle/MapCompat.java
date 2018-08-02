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
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.ArrayMap;

import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;

/**
 * Compatibility class for creating Maps efficiently across different Android platform versions.
 */
@ThreadSafe
public final class MapCompat {

    /**
     * An arbitrary cutoff size to decide to switch between ArrayMap vs. HashMap.
     */
    @VisibleForTesting
    /*package*/ static final int ARRAY_MAP_MAX_SIZE_CUTOFF_INCLUSIVE = 500;

    /**
     * Constructs a new Map initialized with {@code capacity}.  This may return different
     * underlying map implementations depending on the platform version.
     *
     * <p>While the implementation of this method could change, the intention is to return
     * {@link android.util.ArrayMap} when possible, and {@link HashMap} otherwise.</p>
     *
     * @param capacity Capacity of the map.
     * @param <K>      Key type of the map.
     * @param <V>      Value type of the map.
     * @return A new map instance.
     */
    @NonNull
    public static final <K, V> Map<K, V> newMap(@IntRange(from = 0) final int capacity) {
        assertInRangeInclusive(capacity, 0, Integer.MAX_VALUE, "capacity"); //$NON-NLS-1$

        final Map<K, V> map;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.KITKAT)
                && ARRAY_MAP_MAX_SIZE_CUTOFF_INCLUSIVE >= capacity) {
            map = newArrayMap(capacity);
        } else {
            map = new HashMap<>(capacity);
        }

        return map;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static final <K, V> Map<K, V> newArrayMap(@IntRange(from = 0) final int capacity) {
        return new ArrayMap<>(capacity);
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private MapCompat() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
