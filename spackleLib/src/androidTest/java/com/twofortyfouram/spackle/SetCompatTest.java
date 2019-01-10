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

import android.os.Build;
import android.util.ArraySet;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public final class SetCompatTest {

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(SetCompat.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void newSet_new_instance() {
        assertThat(SetCompat.newSet(0), not(sameInstance(SetCompat.newSet(0))));
    }

    @Test
    @SmallTest
    public void newSet_zero() {
        final Set<String> set = SetCompat.newSet(0);

        assertThat(set, notNullValue());
        assertThat(set, hasSize(0));
    }

    @Test
    @SmallTest
    public void newSet_one() {
        final Set<String> set = SetCompat.newSet(1);

        assertThat(set, notNullValue());
        assertThat(set, hasSize(0));
    }

    @Test
    @SmallTest
    public void newSet_mutable() {
        final Set<String> set = SetCompat.newSet(1);

        set.add("test_key"); //$NON-NLS
        // the set doesn't throw UnsupportedOperationException!

        assertThat(set, hasSize(1));
        assertThat(set, contains("test_key")); //$NON-NLS
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    public void newSet_ArrayMap() {
        assertThat(SetCompat.newSet(0), instanceOf(ArraySet.class));
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    public void newSet_size_limit() {
        assertThat(SetCompat.newSet(SetCompat.ARRAY_SET_MAX_SIZE_CUTOFF_INCLUSIVE + 1),
                instanceOf(HashSet.class));
    }
}
