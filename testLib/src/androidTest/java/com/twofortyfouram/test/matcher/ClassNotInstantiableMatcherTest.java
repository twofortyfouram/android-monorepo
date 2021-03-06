/*
 * android-test
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

package com.twofortyfouram.test.matcher;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.InstantiableClass;
import com.twofortyfouram.test.NonInstantiableClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public final class ClassNotInstantiableMatcherTest {

    @Test
    @SmallTest
    public void non_instantiable() {
        assertThat(NonInstantiableClass.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void instantiable() {
        assertThat(InstantiableClass.class, not(notInstantiable()));
    }
}
