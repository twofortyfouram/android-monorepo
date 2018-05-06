/*
 * android-plugin-host-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.util;

import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.spackle.bundle.BundleComparer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests serialization and de-serialization of {@code Bundle} objects.
 */
@RunWith(AndroidJUnit4.class)
public final class BinaryBundleSerializerTest {

    @SmallTest
    @Test(expected = AssertionError.class)
    public void serializeNull() throws BundleSerializer.BundleSerializationException {
        new BinaryBundleSerializer().serialize(null);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void deserializeNull() throws BundleSerializer.BundleSerializationException {
        new BinaryBundleSerializer().deserialize(null);
    }

    @SmallTest
    @Test
    public void booleans() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putBoolean("com.test.extra_false", false); //$NON-NLS-1$
        inputBundle.putBoolean("com.test.extra_true", true); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void booleanArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putBooleanArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putBooleanArray("com.test.extra_empty", new boolean[0]); //$NON-NLS-1$
        inputBundle.putBooleanArray("com.test.extra", new boolean[]{false, true}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void bytes() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putByte("com.test.extra_min", Byte.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putByte("com.test.extra_max", Byte.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void byteArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putByteArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putByteArray("com.test.extra_empty", new byte[0]); //$NON-NLS-1$
        inputBundle.putByteArray("com.test.extra", new byte[]{10}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void shorts() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putShort("com.test.extra_min", Short.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putShort("com.test.extra_max", Short.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void shortArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putShortArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putShortArray("com.test.extra_empty", new short[0]); //$NON-NLS-1$
        inputBundle.putShortArray(
                "com.test.extra_min", new short[]{Short.MIN_VALUE, Short.MAX_VALUE}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void ints() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putInt("com.test.extra_min", Integer.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putInt("com.test.extra_one", 1); //$NON-NLS-1$
        inputBundle.putInt("com.test.extra_two", 2); //$NON-NLS-1$
        inputBundle.putInt("com.test.extra_max", Integer.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void intArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putIntArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putIntArray("com.test.extra_empty", new int[0]); //$NON-NLS-1$
        inputBundle.putIntArray(
                "com.test.extra",
                new int[]{Integer.MIN_VALUE, 0, 1, 2, 3, Integer.MAX_VALUE}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void integerArrayList() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putIntegerArrayList("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putIntegerArrayList("com.test.extra_empty",
                new ArrayList<>()); //$NON-NLS-1$

        final ArrayList<Integer> random = new ArrayList<>();
        random.add(Integer.MIN_VALUE);
        random.add(Integer.MAX_VALUE);
        inputBundle.putIntegerArrayList("com.test.extra_random", random); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void longs() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putLong("com.test.extra_min", Long.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putLong("com.test.extra_one", 1); //$NON-NLS-1$
        inputBundle.putLong("com.test.extra_two", 2); //$NON-NLS-1$
        inputBundle.putLong("com.test.extra_max", Long.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void longArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putLongArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putLongArray("com.test.extra_empty", new long[0]); //$NON-NLS-1$
        inputBundle.putLongArray(
                "com.test.extra",
                new long[]{Long.MIN_VALUE, 0, 1, 2, 3, Long.MAX_VALUE}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void doubles() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putDouble("com.test.extra_min", Double.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putDouble("com.test.extra_one", 1); //$NON-NLS-1$
        inputBundle.putDouble("com.test.extra_two", 2); //$NON-NLS-1$
        inputBundle.putDouble("com.test.extra_max", Double.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void doubleArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putDoubleArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putDoubleArray("com.test.extra_empty", new double[0]); //$NON-NLS-1$
        inputBundle.putDoubleArray(
                "com.test.extra",
                new double[]{Double.MIN_VALUE, 0, 1, 2, 3, Double.MAX_VALUE}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    /**
     * Tests storing float types
     *
     * @throws BundleSerializer.BundleSerializationException if the test fails
     */
    @SmallTest
    public void floats() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putFloat("com.test.extra_min", Float.MIN_VALUE); //$NON-NLS-1$
        inputBundle.putFloat("com.test.extra_one", 1); //$NON-NLS-1$
        inputBundle.putFloat("com.test.extra_two", 2); //$NON-NLS-1$
        inputBundle.putFloat("com.test.extra_max", Float.MAX_VALUE); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void floatArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putFloatArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putFloatArray("com.test.extra_empty", new float[0]); //$NON-NLS-1$
        inputBundle.putFloatArray(
                "com.test.extra",
                new float[]{Float.MIN_VALUE, 0, 1, 2, 3, Float.MAX_VALUE}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void chars() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putChar("com.test.extra_min", Character.MIN_LOW_SURROGATE); //$NON-NLS-1$
        inputBundle.putChar("com.test.extra_max", Character.MAX_HIGH_SURROGATE); //$NON-NLS-1$
        inputBundle.putChar("com.test.extra_random", 'a'); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void charArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putCharArray("com.test.null", null); //$NON-NLS-1$
        inputBundle.putCharArray("com.test.extra_empty", new char[0]); //$NON-NLS-1$
        inputBundle.putCharArray("com.test.extra", new char[]{'a', 'b', 'c'}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void charSequence() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putCharSequence("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putCharSequence("com.test.extra_empty", ""); //$NON-NLS-1$ //$NON-NLS-2$
        inputBundle.putCharSequence("com.test.extra_random",
                "I am a test sequence!"); //$NON-NLS-1$ //$NON-NLS-2$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void charSequenceArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putCharSequenceArray("com.test.null", null); //$NON-NLS-1$
        inputBundle
                .putCharSequenceArray("com.test.extra_empty", new CharSequence[]{}); //$NON-NLS-1$
        inputBundle
                .putCharSequenceArray(
                        "com.test.extra", new CharSequence[]{
                                Resources.getSystem().getString(
                                        android.R.string.cancel)}); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void charSequenceArrayList() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putCharSequenceArrayList("com.test.null", null); //$NON-NLS-1$
        inputBundle.putCharSequenceArrayList("com.test.extra_empty",
                new ArrayList<>()); //$NON-NLS-1$

        final ArrayList<CharSequence> list = new ArrayList<>();
        list.add(Resources.getSystem().getString(android.R.string.cancel));
        inputBundle.putCharSequenceArrayList("com.test.extra", list); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void string() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putString("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putString("com.test.extra_empty", ""); //$NON-NLS-1$ //$NON-NLS-2$
        inputBundle.putString("com.test.extra_random",
                "I am a test string!"); //$NON-NLS-1$ //$NON-NLS-2$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void stringArray() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putStringArray("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putStringArray("com.test.extra_empty", new String[0]); //$NON-NLS-1$
        inputBundle.putStringArray("com.test.extra_array",
                new String[]{"foo", "bar"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void stringArrayList() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putStringArrayList("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle
                .putStringArrayList("com.test.extra_empty", new ArrayList<>()); //$NON-NLS-1$

        final ArrayList<String> random = new ArrayList<>();
        random.add("foo"); //$NON-NLS-1$
        random.add("bar"); //$NON-NLS-1$
        inputBundle.putStringArrayList("com.test.extra_random", random); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test(expected = BundleSerializer.BundleSerializationException.class)
    public void testParcelable() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putParcelable("com.test.parcelable",
                new Location("foo")); //$NON-NLS-1$ //$NON-NLS-2$

        new BinaryBundleSerializer().serialize(inputBundle);
    }

    @SmallTest
    @Test
    public void bundles() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putBundle("com.test.extra_null", null); //$NON-NLS-1$
        inputBundle.putBundle("com.test.extra_empty", new Bundle()); //$NON-NLS-1$

        final Bundle random = new Bundle();
        random.putInt("a", 1); //$NON-NLS-1$
        random.putLong("b", 2); //$NON-NLS-1$
        random.putBoolean("c", false); //$NON-NLS-1$
        inputBundle.putBundle("com.test.extra_random", random); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }

    @SmallTest
    @Test
    public void nullKey() throws BundleSerializer.BundleSerializationException {
        final Bundle inputBundle = new Bundle();
        inputBundle.putString(null, "foo"); //$NON-NLS-1$

        final Bundle resultBundle = new BinaryBundleSerializer()
                .deserialize(new BinaryBundleSerializer()
                .serialize(inputBundle));

        assertThat(BundleComparer.areBundlesEqual(inputBundle, resultBundle), is(true));
    }
}
