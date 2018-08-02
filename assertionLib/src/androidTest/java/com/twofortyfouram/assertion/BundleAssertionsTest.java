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


import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.test.SomeSerializable;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;


@RunWith(AndroidJUnit4.class)
public final class BundleAssertionsTest {

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasKey_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasKey(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasKey_valid_int() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 1); //$NON-NLS-1$

        BundleAssertions.assertHasKey(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasKey_valid_null() {
        final Bundle bundle = new Bundle();
        bundle.putString(null, null);

        BundleAssertions.assertHasKey(bundle, null);
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasKey_persistable_missing() {
        final PersistableBundle bundle = new PersistableBundle();

        BundleAssertions.assertHasKey(bundle, "test_key"); //$NON-NLS-1$
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test
    public void hasKey_persistable_valid_int() {
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("test_key", 1);

        BundleAssertions.assertHasKey(bundle, "test_key"); //$NON-NLS-1$
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test
    public void hasKey_persistable_valid_null() {
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putString(null, null);

        BundleAssertions.assertHasKey(bundle, null);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasBoolean_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasBoolean(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasBoolean_wrong_type() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasBoolean(bundle, "test_key"); //$NON-NLS-1$
    }


    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasBoolean_wrong_type_non_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$
        BundleAssertions.assertHasBoolean(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasBoolean_valid() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("test_key", true); //$NON-NLS-1$

        BundleAssertions.assertHasBoolean(bundle, "test_key"); //$NON-NLS-1$

        bundle.putBoolean("test_key", false); //$NON-NLS-1$
        BundleAssertions.assertHasBoolean(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasByteArray_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasByteArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasByteArray_wrong_type_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasByteArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasByteArray_wrong_type_non_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasByteArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasByteArray_valid() {
        final Bundle bundle = new Bundle();
        bundle.putByteArray("test_key", new byte[0]); //$NON-NLS-1$

        BundleAssertions.assertHasByteArray(bundle, "test_key"); //$NON-NLS-1$
    }


    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasStringArray_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasStringArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasStringArray_wrong_type_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasStringArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasStringArray_wrong_type_non_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasStringArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasStringArray_valid() {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("test_key", new String[0]); //$NON-NLS-1$

        BundleAssertions.assertHasStringArray(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasInt(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_missing_range() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasInt(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_wrong_type() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key"); //$NON-NLS-1$
    }


    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_wrong_type_null_range() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }


    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_wrong_type_nonnull() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasInt(bundle, "test_key"); //$NON-NLS-1$

    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_wrong_type_nonull_range() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasInt(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasInt_valid() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 52); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasInt_range_valid() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 52); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key", 52, 52); //$NON-NLS-1$
        BundleAssertions.assertHasInt(bundle, "test_key", 52, 100); //$NON-NLS-1$
        BundleAssertions.assertHasInt(bundle, "test_key", -100, 52); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_range_above() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 17); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key", 0, 15); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasInt_range_below() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 17); //$NON-NLS-1$

        BundleAssertions.assertHasInt(bundle, "test_key", 27, 502); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void hasInt_range_bad() {
        BundleAssertions.assertHasInt(new Bundle(), "test_key", 2, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasLong(bundle, "test_key"); //$NON-NLS-1$

    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_missing_range() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasLong(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_wrong_type_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_wrong_type_range() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_wrong_type_non_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasLong(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_wrong_type_non_null_range() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "foo"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasLong(bundle, "test_key", 0, 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasLong_valid() {
        final Bundle bundle = new Bundle();
        bundle.putLong("test_key", Long.MAX_VALUE); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasLong_range_valid() {
        final Bundle bundle = new Bundle();
        bundle.putLong("test_key", Long.MAX_VALUE - 1); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key", Long.MAX_VALUE - 1,
                Long.MAX_VALUE); //$NON-NLS-1$
        BundleAssertions.assertHasLong(bundle, "test_key", 0, Long.MAX_VALUE); //$NON-NLS-1$
        BundleAssertions.assertHasLong(bundle, "test_key", 0, Long.MAX_VALUE - 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_range_above() {
        final Bundle bundle = new Bundle();
        bundle.putLong("test_key", Long.MAX_VALUE); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key", 0, Long.MAX_VALUE - 1); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasLong_range_below() {
        final Bundle bundle = new Bundle();
        bundle.putLong("test_key", Long.MIN_VALUE); //$NON-NLS-1$

        BundleAssertions.assertHasLong(bundle, "test_key", 0, Long.MAX_VALUE); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void hasLong_range_bad() {
        BundleAssertions.assertHasLong(new Bundle(), "test_key", Long.MAX_VALUE,
                Long.MIN_VALUE); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasParcelable_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasParcelable(bundle, "test_key", Location.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasParcelable_null() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasParcelable(bundle, "test_key", Location.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasParcelable_not_parcelable() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$

        BundleAssertions.assertHasParcelable(bundle, "test_key", Location.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasSerializable_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasSerializable(bundle, "test_key", Integer.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasSerializable_null() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasSerializable(bundle, "test_key", Integer.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasParcelable_not_serializable() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("test_key", new Location(LocationManager.PASSIVE_PROVIDER)); //$NON-NLS-1$

        BundleAssertions.assertHasSerializable(bundle, "test_key", Integer.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasParcelable_wrong_type() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("test_key", new Address(Locale.US)); //$NON-NLS-1$

        BundleAssertions.assertHasParcelable(bundle, "test_key", Location.class); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_missing() {
        final Bundle bundle = new Bundle();

        BundleAssertions.assertHasString(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void hasString_wrong_type() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 5); //$NON-NLS-1$

        BundleAssertions.assertHasString(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_valid() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key"); //$NON-NLS-1$

        bundle.putString("test_key", null); //$NON-NLS-1$
        BundleAssertions.assertHasString(bundle, "test_key"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_allowed_values_valid() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key",
                "test_value"); //$NON-NLS-1$ //$NON-NLS-2$
        BundleAssertions.assertHasString(bundle, "test_key",
                "bork", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_allowed_values_case_sensitive() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key",
                "TEST_VALUE"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_allowed_values_missing() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key",
                "bork"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    public void hasString_allowed_values_null() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasString(bundle, "test_key", new String[]{null}); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_allowed_values_empty_string() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", ""); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key",
                ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_allowed_values_empty_array() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key", new String[0]); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_allowed_values_null_array() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key", (String[]) null); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_null_valid() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasString(bundle, "test_key", true, true); //$NON-NLS-1$
        BundleAssertions.assertHasString(bundle, "test_key", true, false); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_null_not_allowed_empty_not_allowed() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasString(bundle, "test_key", false, false); //$NON-NLS-1$
    }


    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_null_not_allowed_empty_allowed() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", null); //$NON-NLS-1$

        BundleAssertions.assertHasString(bundle, "test_key", false, true); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void hasString_empty_valid() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", ""); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key", true, true); //$NON-NLS-1$
        BundleAssertions.assertHasString(bundle, "test_key", false, true); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_empty_not_null_not_allowed_empty_not_allowed() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", ""); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key", false, false); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void hasString_empty_invalid_null_allowed_empty_not_allowed() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", ""); //$NON-NLS-1$ //$NON-NLS-2$

        BundleAssertions.assertHasString(bundle, "test_key", true, false); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertKeyCount_valid() {
        BundleAssertions.assertKeyCount(new Bundle(), 0);

        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 1); //$NON-NLS-1$

        BundleAssertions.assertKeyCount(bundle, 1);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_invalid_empty() {
        BundleAssertions.assertKeyCount(new Bundle(), 1);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_invalid_non_empty() {
        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 1); //$NON-NLS-1$
        BundleAssertions.assertKeyCount(bundle, 0);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_bad_parameters() {
        BundleAssertions.assertKeyCount(new Bundle(), -1);
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test
    public void assertKeyCount_persistable_valid() {
        BundleAssertions.assertKeyCount(new PersistableBundle(), 0);

        final Bundle bundle = new Bundle();
        bundle.putInt("test_key", 1); //$NON-NLS-1$

        BundleAssertions.assertKeyCount(bundle, 1);
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_persistable_invalid_empty() {
        BundleAssertions.assertKeyCount(new PersistableBundle(), 1);
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_persistable_invalid_non_empty() {
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("test_key", 1); //$NON-NLS-1$
        BundleAssertions.assertKeyCount(bundle, 0);
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertKeyCount_persistable_bad_parameters() {
        BundleAssertions.assertKeyCount(new PersistableBundle(), -1);
    }

    @SmallTest
    @Test
    public void assertSerializable_empty() {
        BundleAssertions.assertSerializable(new Bundle());
    }

    @SmallTest
    @Test
    public void assertSerializable_recursive() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("bundle", new Bundle()); //$NON-NLS-1$

        BundleAssertions.assertSerializable(bundle);
    }

    @SmallTest
    @Test
    public void assertSerializable_null_key() {
        final Bundle bundle = new Bundle();
        bundle.putString(null, "foo"); //$NON-NLS-1$

        BundleAssertions.assertSerializable(bundle);
    }

    @SmallTest
    @Test
    public void assertSerializable_null_value() {
        final Bundle bundle = new Bundle();
        bundle.putString("foo", null); //$NON-NLS-1$

        BundleAssertions.assertSerializable(bundle);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertSerializable_custom_serializable() {
        final Bundle bundle = new Bundle();
        bundle.putSerializable("foo", new SomeSerializable()); //$NON-NLS-1$

        BundleAssertions.assertSerializable(bundle);
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertSerializable_parcelable() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("foo", new Location(LocationManager.GPS_PROVIDER)); //$NON-NLS-1$

        BundleAssertions.assertSerializable(bundle);
    }
}
