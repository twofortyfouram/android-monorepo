/*
 * android-assertion
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

package com.twofortyfouram.assertion;

import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.jcip.annotations.ThreadSafe;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Runtime assertions.
 */
@ThreadSafe
public final class Assertions {

    /**
     * @param actual       Value to check whether it is in the range [ {@code minInclusive},
     *                     {@code maxInclusive}].
     * @param minInclusive Minimum value for {@code actual}.
     * @param maxInclusive Maximum value for {@code actual}.
     * @param name         Name of the value for human-readable exceptions.
     * @throws AssertionError If {@code actual} is outside the range [ {@code minInclusive},
     *                        {@code maxInclusive}].
     */
    public static void assertInRangeInclusive(final int actual, final int minInclusive,
            final int maxInclusive, @NonNull final String name) {
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("maxInclusive is not >= minInclusive"); //$NON-NLS-1$
        }

        if (actual < minInclusive || actual > maxInclusive) {
            throw new AssertionError(formatMessage(
                    "%s=%d is not in the range [%d, %d]", name, actual, minInclusive,
                    maxInclusive)); //$NON-NLS-1$
        }
    }

    /**
     * @param actual       Value to check whether it is in the range [ {@code minInclusive},
     *                     {@code maxInclusive}].
     * @param minInclusive Minimum value for {@code actual}.
     * @param maxInclusive Maximum value for {@code actual}.
     * @param name         Name of the value for human-readable exceptions.
     * @throws AssertionError If {@code actual} is outside the range [ {@code minInclusive},
     *                        {@code maxInclusive}].
     */
    public static void assertInRangeInclusive(final long actual, final long minInclusive,
            final long maxInclusive, @NonNull final String name) {
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("maxInclusive is not >= minInclusive"); //$NON-NLS-1$
        }

        if (actual < minInclusive || actual > maxInclusive) {
            throw new AssertionError(formatMessage(
                    "%s=%d is not in the range [%d, %d]", name, actual, minInclusive,
                    maxInclusive)); //$NON-NLS-1$
        }
    }

    /**
     * @param actual       Value to check whether it is in the range [ {@code minInclusive},
     *                     {@code maxInclusive}].
     * @param minInclusive Minimum value for {@code actual}.
     * @param maxInclusive Maximum value for {@code actual}.
     * @param name         Name of the value for human-readable exceptions.
     * @throws AssertionError If {@code actual} is outside the range [ {@code minInclusive},
     *                        {@code maxInclusive}].
     */
    public static void assertInRangeInclusive(final float actual, final float minInclusive,
            final float maxInclusive, @NonNull final String name) {
        if (Float.compare(minInclusive, maxInclusive) > 0) {
            throw new IllegalArgumentException("maxInclusive is not >= minInclusive"); //$NON-NLS-1$
        }

        if (0 > Float.compare(actual, minInclusive) || 0 < Float.compare(actual, maxInclusive)) {
            throw new AssertionError(formatMessage(
                    "%s=%f is not in the range [%f, %f]", name, actual, minInclusive,
                    maxInclusive)); //$NON-NLS-1$
        }
    }


    /**
     * @param actual       Value to check whether it is in the range [ {@code minInclusive},
     *                     {@code maxInclusive}].
     * @param minInclusive Minimum value for {@code actual}.
     * @param maxInclusive Maximum value for {@code actual}.
     * @param name         Name of the value for human-readable exceptions.
     * @throws AssertionError If {@code actual} is outside the range [ {@code minInclusive},
     *                        {@code maxInclusive}].
     */
    public static void assertInRangeInclusive(final double actual, final double minInclusive,
            final double maxInclusive, @NonNull final String name) {
        if (Double.compare(minInclusive, maxInclusive) > 0) {
            throw new IllegalArgumentException("maxInclusive is not >= minInclusive"); //$NON-NLS-1$
        }

        if (0 > Double.compare(actual, minInclusive) || 0 < Double.compare(actual, maxInclusive)) {
            throw new AssertionError(formatMessage(
                    "%s=%f is not in the range [%f, %f]", name, actual, minInclusive,
                    maxInclusive)); //$NON-NLS-1$
        }
    }

    /**
     * @param array Array to check for being null or containing null elements.
     * @param name  Name of the array for human-readable exceptions.
     * @throws AssertionError If {@code array} is null or empty.
     */
    public static void assertNoNullElements(@Nullable final Object[] array,
            @Nullable final String name) {
        assertNotNull(array, name);

        for (final Object o : array) {
            if (null == o) {
                throw new AssertionError(formatMessage(
                        "%s cannot contain null elements", name)); //$NON-NLS-1$
            }
        }
    }

    /**
     * @param collection Collection to check for being null or containing null elements.
     * @param name       Name of the Collection for human-readable exceptions.
     * @throws AssertionError If {@code collection} is null or empty.
     */
    public static void assertNoNullElements(@Nullable final Collection<?> collection,
            @Nullable final String name) {
        assertNotNull(collection, name);

        for (final Object o : collection) {
            if (null == o) {
                throw new AssertionError(formatMessage(
                        "%s cannot contain null elements", name)); //$NON-NLS-1$
            }
        }
    }

    /**
     * @param map  Map to check for being null or containing null keys or null values.
     * @param name Name of the Map for human-readable exceptions.
     * @throws AssertionError If {@code map} is null or empty.
     */
    public static void assertNoNullElements(@Nullable final Map<?, ?> map,
            @Nullable final String name) {
        assertNotNull(map, name);

        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            if (null == entry.getKey()) {
                throw new AssertionError(formatMessage(
                        "%s cannot contain null keys", name)); //$NON-NLS-1$
            }
            if (null == entry.getValue()) {
                throw new AssertionError(formatMessage(
                        "%s cannot contain null values", name)); //$NON-NLS-1$
            }
        }
    }

    /**
     * @param obj Object to check if it is within {@code set}.
     * @param set Allowed elements for {@code obj}. {@code set} must not be null, but may
     *            contain null elements.
     */
    public static void assertInSet(@Nullable final Object obj, @NonNull final Object... set) {
        assertNotNull(set, "set"); //$NON-NLS-1$

        boolean isInSet = false;
        for (final Object x : set) {
            if (null == x) {
                if (null == obj) {
                    isInSet = true;
                    break;
                }
            } else if (x.equals(obj)) {
                isInSet = true;
                break;
            }
        }

        if (!isInSet) {
            throw new AssertionError(
                    formatMessage("%s is not in set %s", obj, set)); //$NON-NLS-1$
        }
    }

    /**
     * @param object Object to check for being {@code null}.
     * @param name   Name of the object for human-readable exceptions.
     * @return {@code object} sanitized as being non-null.
     * @throws AssertionError If {@code object} is {@code null}.
     */
    @NonNull
    public static <T> T assertNotNull(@Nullable final T object, @NonNull final String name) {
        if (null == object) {
            throw new AssertionError(
                    formatMessage("%s cannot be null", name)); //$NON-NLS-1$
        }

        return object;
    }

    /**
     * @param string String to check for being {@code null} or empty.
     * @param name   Name of the String for human-readable exceptions.
     * @throws AssertionError If {@code string} is {@code null} or empty.
     */
    public static void assertNotEmpty(@Nullable final String string, @NonNull final String name) {
        // Don't use TextUtils.isEmpty(), in order to support unit tests with the mock android.jar
        if (null == string || 0 == string.length()) {
            throw new AssertionError(
                    formatMessage("%s cannot be null or empty", name)); //$NON-NLS-1$
        }
    }

    /**
     * @param collection Collection to check for being {@code null} or empty.
     * @param name       Name of the Collection for human-readable exceptions.
     * @throws AssertionError If {@code collection} is {@code null} or empty.
     */
    public static void assertNotEmpty(@Nullable final Collection<?> collection,
            @NonNull final String name) {
        assertNotNull(collection, name);

        if (collection.isEmpty()) {
            throw new AssertionError(
                    formatMessage("%s cannot be empty", name)); //$NON-NLS-1$
        }
    }

    /**
     * @param map  Map to check for being {@code null} or empty.
     * @param name Name of the Map for human-readable exceptions.
     * @throws AssertionError If {@code map} is {@code null} or empty.
     */
    public static void assertNotEmpty(@Nullable final Map<?, ?> map, @NonNull final String name) {
        assertNotNull(map, name);

        if (map.isEmpty()) {
            throw new AssertionError(
                    formatMessage("%s cannot be empty", name)); //$NON-NLS-1$
        }
    }

    /**
     * @throws AssertionError If the current thread is not the main application thread.
     */
    public static void assertIsMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new AssertionError("Current thread is not the main thread"); //$NON-NLS-1$
        }
    }

    /**
     * @throws AssertionError If the current thread is not the main application thread.
     */
    public static void assertIsNotMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new AssertionError("Current thread is the main thread"); //$NON-NLS-1$
        }
    }

    /**
     * Helper for formatting messages.
     *
     * @param msg  The format string.
     * @param args The format arguments.
     * @return A string formatted with the arguments
     */
    @NonNull
    private static String formatMessage(@NonNull final String msg, @NonNull final Object... args) {
        return String.format(Locale.US, msg, args);
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private Assertions() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
