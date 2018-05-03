/*
 * android-test https://github.com/twofortyfouram/android-test
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

package com.twofortyfouram.test.matcher;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.AccidentallyMutableClass;
import com.twofortyfouram.test.ImmutableClassWithFields;
import com.twofortyfouram.test.ImmutableClassWithoutFields;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public final class ClassImmutableMatcherTest {

    @SmallTest
    @Test
    public void hasFinalFields_with_fields_true() {
        assertThat(ClassImmutableMatcher.getNonFinalFields(ImmutableClassWithFields.class),
                empty());
    }

    @SmallTest
    @Test
    public void hasFinalFields_without_fields_true() {
        assertThat(ClassImmutableMatcher.getNonFinalFields(ImmutableClassWithoutFields.class),
                empty());
    }

    @SmallTest
    @Test
    public void hasFinalFields_accidentally_mutable() {
        assertThat(ClassImmutableMatcher.getNonFinalFields(AccidentallyMutableClass.class),
                hasSize(1));
    }

    @Test
    @SmallTest
    public void immutable() {
        assertThat(ImmutableClassWithFields.class, ClassImmutableMatcher.immutable());
    }

    @Test
    @SmallTest
    public void not_immutable() {
        assertThat(AccidentallyMutableClass.class, not(ClassImmutableMatcher.immutable()));
    }

}
