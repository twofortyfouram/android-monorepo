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
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.ArrayMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class MapCompatTest {

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(MapCompat.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void newMap_new_instance() {
        assertThat(MapCompat.newMap(0), not(sameInstance(MapCompat.newMap(0))));
    }

    @Test
    @SmallTest
    public void newMap_zero() {
        final Map<String, Integer> map = MapCompat.newMap(0);

        assertThat(map, notNullValue());
        assertThat(map.size(), is(0));
    }

    @Test
    @SmallTest
    public void newMap_one() {
        final Map<String, Integer> map = MapCompat.newMap(1);

        assertThat(map, notNullValue());
        assertThat(map.size(), is(0));
    }

    @Test
    @SmallTest
    public void newMap_mutable() {
        final Map<String, Integer> map = MapCompat.newMap(1);

        map.put("test_key", 1);
        // the map doesn't throw UnsupportedOperationException!

        assertThat(map.size(), is(1));
        assertThat(map.get("test_key"), is(1));
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.KITKAT)
    public void newMap_ArrayMap() {
        assertThat(MapCompat.newMap(0), instanceOf(ArrayMap.class));
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.KITKAT)
    public void newMap_size_limit() {
        assertThat(MapCompat.newMap(MapCompat.ARRAY_MAP_MAX_SIZE_CUTOFF_INCLUSIVE + 1),
                instanceOf(HashMap.class));
    }
}
