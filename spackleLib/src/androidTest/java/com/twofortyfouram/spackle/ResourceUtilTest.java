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

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.spackle.test.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class ResourceUtilTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(ResourceUtil.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void getPositionForIdInArray() {
        assertThat(ResourceUtil.getPositionForIdInArray(getContext(), R.array
                        .com_twofortyfouram_spackle_test_array,
                R.string.com_twofortyfouram_spackle_test_string_key_1), is(0));

        assertThat(ResourceUtil.getPositionForIdInArray(getContext(), R.array
                        .com_twofortyfouram_spackle_test_array,
                R.string.com_twofortyfouram_spackle_test_string_key_2), is(1));

        assertThat(ResourceUtil.getPositionForIdInArray(getContext(), R.array
                        .com_twofortyfouram_spackle_test_array,
                R.string.com_twofortyfouram_spackle_test_string_key_3), is(2));
    }

    @SmallTest
    @Test
    public void resourceIdForPositionInArray() {
        assertThat(ResourceUtil.getResourceIdForPositionInArray(getContext(), R.array
                        .com_twofortyfouram_spackle_test_array, 0),
                is(R.string.com_twofortyfouram_spackle_test_string_key_1));

        assertThat(ResourceUtil.getResourceIdForPositionInArray(getContext(),
                R.array.com_twofortyfouram_spackle_test_array, 1),
                is(R.string.com_twofortyfouram_spackle_test_string_key_2));

        assertThat(ResourceUtil.getResourceIdForPositionInArray(getContext(),
                R.array.com_twofortyfouram_spackle_test_array, 2),
                is(R.string.com_twofortyfouram_spackle_test_string_key_3));
    }
}
