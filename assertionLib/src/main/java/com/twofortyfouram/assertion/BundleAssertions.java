/*
 * android-assertion https://github.com/twofortyfouram/android-assertion
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

package com.twofortyfouram.assertion;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import net.jcip.annotations.ThreadSafe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * Runtime assertions for Bundle contents.
 */
@ThreadSafe
public final class BundleAssertions {

    /**
     * Asserts {@code bundle} contains a mapping for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping for.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey}.
     */
    public static void assertHasKey(@NonNull final Bundle bundle,
            @Nullable final String requiredKey)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        if (!bundle.containsKey(requiredKey)) {
            final String message = formatMessage("Required extra %s is missing", //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a mapping for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping for.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey}.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void assertHasKey(@NonNull final PersistableBundle bundle,
            @Nullable final String requiredKey)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        if (!bundle.containsKey(requiredKey)) {
            final String message = formatMessage("Required extra %s is missing", //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to a boolean value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasBoolean(@NonNull final Bundle bundle,
            @Nullable final String requiredKey) throws AssertionError {
        assertHasKey(bundle, requiredKey);

        if (bundle.getBoolean(requiredKey, true) != bundle.getBoolean(requiredKey, false)) {
            final String message = formatMessage(
                    "Extra %s appears to be the wrong type.  It must be a boolean", //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to a {@code byte[]} value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasByteArray(@NonNull final Bundle bundle,
            @Nullable final String requiredKey) throws AssertionError {
        assertHasKey(bundle, requiredKey);

        if (null == bundle.getByteArray(requiredKey)) {
            final String message = formatMessage(
                    "Extra %s appears to be the wrong type or null.  It must be a byte[]",
                    //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to a {@code String[]} value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasStringArray(@NonNull final Bundle bundle,
            @Nullable final String requiredKey) throws AssertionError {
        assertHasKey(bundle, requiredKey);

        if (null == bundle.getStringArray(requiredKey)) {
            final String message = formatMessage(
                    "Extra %s appears to be the wrong type or null.  It must be a String[]",
                    //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to an int value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasInt(@NonNull final Bundle bundle,
            @Nullable final String requiredKey)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        assertHasKey(bundle, requiredKey);

        if (bundle.getInt(requiredKey, Integer.MIN_VALUE) != bundle.getInt(requiredKey,
                Integer.MAX_VALUE)) {
            final String message = formatMessage(
                    "Extra %s appears to be the wrong type.  It must be an int", //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to an int value.
     * @param lowerBound  Inclusive lower bound for {@code requiredKey}'s value. Must be less than
     *                    or equal to {@code upperBound}.
     * @param upperBound  Inclusive upper bound for {@code requiredKey}'s value. Must be greater
     *                    than or equal to {@code lowerBound}.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasInt(@NonNull final Bundle bundle,
            @Nullable final String requiredKey, final int lowerBound, final int upperBound)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("upperBound is not >= lowerBound"); //$NON-NLS-1$
        }

        assertHasInt(bundle, requiredKey);

        final int value = bundle.getInt(requiredKey);
        Assertions.assertInRangeInclusive(value, lowerBound, upperBound, requiredKey);
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to a long value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasLong(@NonNull final Bundle bundle,
            @Nullable final String requiredKey)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        assertHasKey(bundle, requiredKey);

        if (bundle.getLong(requiredKey, Long.MIN_VALUE) != bundle.getLong(requiredKey,
                Long.MAX_VALUE)) {
            final String message = formatMessage(
                    "Extra %s appears to be the wrong type.  It must be a long", //$NON-NLS-1$
                    requiredKey);
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to an long value.
     * @param lowerBound  Inclusive lower bound for {@code requiredKey}'s value. Must be less than
     *                    or equal to {@code upperBound}.
     * @param upperBound  Inclusive upper bound for {@code requiredKey}'s value. Must be greater
     *                    than or equal to {@code lowerBound}.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasLong(@NonNull final Bundle bundle,
            @Nullable final String requiredKey, final long lowerBound, final long upperBound)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("upperBound is not >= lowerBound"); //$NON-NLS-1$
        }

        assertHasLong(bundle, requiredKey);

        final long value = bundle.getLong(requiredKey);
        Assertions.assertInRangeInclusive(value, lowerBound, upperBound, requiredKey);
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle        Bundle to check keys of.
     * @param requiredKey   Key that {@code bundle} must have mapping to a non-null Parcelable.
     * @param expectedClass Expected class type for the Parcelable {@code requiredKey} maps to.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    @SuppressLint("NewApi")
    public static void assertHasParcelable(@NonNull final Bundle bundle,
            @Nullable final String requiredKey, @NonNull Class<? extends Parcelable> expectedClass)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$
        Assertions.assertNotNull(expectedClass, "expectedClass");  //$NON-NLS-1$

        assertHasKey(bundle, requiredKey);

        final Parcelable p = bundle.getParcelable(requiredKey);

        /*
         * Because assertHasKey ensures that the key maps to a non-null value, we can be confident
         * that null coming back now means the key does not map to a Parcelable.
         */
        if (null == p) {
            throw new AssertionError(
                    formatMessage("Extra %s is not Parcelable", requiredKey));  //$NON-NLS-1$
        }

        if (p.getClass() != expectedClass) {
            throw new AssertionError(formatMessage("Extra %s is not an instance of %s", requiredKey,
                    expectedClass.getName()));
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     * <p>
     * Note: Detection of incorrect type mapping only occurs on API level 12 or greater.
     *
     * @param bundle      Bundle to check keys of.
     * @param requiredKey Key that {@code bundle} must have mapping to a String or {@code null}.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    @SuppressLint("NewApi")
    public static void assertHasString(@NonNull final Bundle bundle,
            @Nullable final String requiredKey) throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        assertHasKey(bundle, requiredKey);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            if (null != bundle.get(requiredKey)
                    && "foo".equals(bundle.getString(requiredKey, "foo")) && "bar".equals(bundle
                    .getString(requiredKey,
                            "bar"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            {
                final String message = formatMessage(
                        "Extra %s is the wrong type", requiredKey); //$NON-NLS-1$
                throw new AssertionError(message);
            }
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle        Bundle to check keys of.
     * @param requiredKey   Key that {@code bundle} must have mapping to a String value.
     * @param allowedValues Set of allowed values for {@code requiredKey}'s value. {@code null}
     *                      indicates that all values are allowed.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    public static void assertHasString(@NonNull final Bundle bundle,
            @Nullable final String requiredKey, @Nullable final String... allowedValues)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        assertHasString(bundle, requiredKey);

        if (null != allowedValues) {
            boolean isOk = false;
            final String value = bundle.getString(requiredKey);
            for (final String allowedValue : allowedValues) {
                if (null == value) {
                    if (null == allowedValue) {
                        isOk = true;
                        break;
                    }
                } else {
                    if (value.equals(allowedValue)) {
                        isOk = true;
                        break;
                    }
                }
            }
            if (!isOk) {
                final String message = formatMessage(
                        "Extra %s=%s is not in the set %s", requiredKey, value,//$NON-NLS-1$
                        Arrays.toString(allowedValues));
                throw new AssertionError(message);
            }
        }
    }

    /**
     * Asserts {@code bundle} contains a value of the correct type for {@code requiredKey}.
     *
     * @param bundle               Bundle to check keys of.
     * @param requiredKey          Key that {@code bundle} must have mapping to a String value.
     * @param isNullAllowed        If null is allowed for the value.
     * @param isEmptyStringAllowed If the empty string is allowed for the value.
     * @throws AssertionError If {@code bundle} doesn't contain {@code requiredKey} mapping to the
     *                        correct type.
     */
    @SuppressLint("NewApi")
    public static void assertHasString(@NonNull final Bundle bundle,
            @Nullable final String requiredKey, final boolean isNullAllowed,
            final boolean isEmptyStringAllowed) throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        assertHasString(bundle, requiredKey);

        final String value = bundle.getString(requiredKey);
        if (!isNullAllowed) {
            if (null == value) {
                throw new AssertionError(formatMessage(
                        "%s cannot map to null", requiredKey)); //$NON-NLS-1$
            }
        }
        if (!isEmptyStringAllowed) {
            if (null != value && 0 == value.length()) {
                throw new AssertionError(formatMessage(
                        "%s cannot map to empty string", requiredKey)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Asserts {@code bundle} contains exactly {@code count} keys.
     *
     * @param bundle        Bundle to check key count of.
     * @param expectedCount Expected count of keys in bundle. Must be greater than or equal to 0.
     * @throws AssertionError If {@code bundle} doesn't contain {@code expectedCount} keys.
     */
    public static void assertKeyCount(@NonNull final Bundle bundle, final int expectedCount)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$
        Assertions.assertInRangeInclusive(expectedCount, 0, Integer.MAX_VALUE,
                "expectedCount"); //$NON-NLS-1$

        if (expectedCount != bundle.keySet().size()) {
            final String message = formatMessage(
                    "bundle must contain %d keys, but currently contains %d keys: %s",
                    expectedCount, //$NON-NLS-1$
                    bundle.keySet().size(), bundle.keySet().toString());

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts {@code bundle} contains exactly {@code count} keys.
     *
     * @param bundle        Bundle to check key count of.
     * @param expectedCount Expected count of keys in bundle. Must be greater than or equal to 0.
     * @throws AssertionError If {@code bundle} doesn't contain {@code expectedCount} keys.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void assertKeyCount(@NonNull final PersistableBundle bundle, final int expectedCount)
            throws AssertionError {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$
        Assertions.assertInRangeInclusive(expectedCount, 0, Integer.MAX_VALUE,
                "expectedCount"); //$NON-NLS-1$

        if (expectedCount != bundle.keySet().size()) {
            final String message = formatMessage(
                    "bundle must contain %d keys, but currently contains %d keys: %s",
                    expectedCount, //$NON-NLS-1$
                    bundle.keySet().size(), bundle.keySet().toString());

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts whether that a Bundle does not contain private {@code Serializable} classes.
     *
     * @param bundle bundle to check.
     * @throws AssertionError if {@code bundle} contains a {@code Serializable} object that isn't
     *                        part of the Android platform.
     */
    public static void assertSerializable(@NonNull final Bundle bundle) {
        Assertions.assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        ObjectOutputStream objectOut = null; // needs to be closed
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); // closing has no effect
        try {
            try {
                objectOut = new ObjectOutputStream(byteOut);
            } catch (final IOException e) {
                throw new RuntimeException();
            }

            for (final String key : bundle.keySet()) {
                final Object value = bundle.get(key);

                if (value instanceof Bundle) {
                    // recursively check sub bundles
                    assertSerializable((Bundle) value);
                } else if (value instanceof Serializable) {
                    try {
                        ClassLoader.getSystemClassLoader().loadClass(value.getClass().getName());
                    } catch (final ClassNotFoundException e) {
                        throw new AssertionError(
                                formatMessage(
                                        "Object associated with key %s is not available to the Android ClassLoader",
                                        key)
                        ); //$NON-NLS-1$
                    }

                    try {
                        objectOut.writeObject(bundle.get(key));
                    } catch (final IOException e) {
                        throw new AssertionError(formatMessage(
                                "Object associated with key %s couldn't be serialized",
                                key)); //$NON-NLS-1$
                    }

                } else if (null == value) {
                    // null values are acceptable
                } else {
                    throw new AssertionError(
                            formatMessage(
                                    "Key \"%s\"'s value %s isn't Serializable.  Only primitives or objects implementing Serializable can be stored.  Parcelable is not stable for long-term storage.",
                                    key, bundle.get(key))
                    ); //$NON-NLS-1$
                }
            }
        } finally {
            if (null != objectOut) {
                try {
                    objectOut.close();
                } catch (final IOException e) {
                    throw new RuntimeException("Internal failure"); //$NON-NLS-1$
                }
            }
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
    private BundleAssertions() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
