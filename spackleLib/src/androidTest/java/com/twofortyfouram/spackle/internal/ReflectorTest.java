/*
 * android-spackle https://github.com/twofortyfouram/android-spackle
 * Copyright (C) 2009–2017 two forty four a.m. LLC
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

package com.twofortyfouram.spackle.internal;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public final class ReflectorTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(Reflector.class, notInstantiable());
    }

    @SmallTest
    @Test
    @SuppressWarnings("rawtypes")
    public void invokeStatic_class_object() {
        final String result1 = Reflector.tryInvokeStatic(Boolean.class,
                "toString", new Class[]{Boolean.TYPE}, new Object[]{Boolean.TRUE}); //$NON-NLS-1$
        assertEquals(Boolean.toString(Boolean.TRUE), result1);

        final String result2 = Reflector.tryInvokeStatic(Boolean.class,
                "toString", new Class[]{Boolean.TYPE}, new Object[]{Boolean.FALSE}); //$NON-NLS-1$

        assertEquals(Boolean.toString(Boolean.FALSE), result2);
    }

    @SmallTest
    @Test
    @SuppressWarnings("rawtypes")
    public void invokeStatic_class_name() {
        final String result1 = Reflector.tryInvokeStatic(Boolean.class.getName(),
                "toString", new Class[]{Boolean.TYPE}, new Object[]{Boolean.TRUE}); //$NON-NLS-1$
        assertEquals(Boolean.toString(Boolean.TRUE), result1);

        final String result2 = Reflector.tryInvokeStatic(Boolean.class.getName(),
                "toString", new Class[]{Boolean.TYPE}, new Object[]{Boolean.FALSE}); //$NON-NLS-1$

        assertEquals(Boolean.toString(Boolean.FALSE), result2);
    }

    @SmallTest
    @Test
    @SuppressWarnings("rawtypes")
    public void invokeInstance() {
        final String result = Reflector
                .tryInvokeInstance(Boolean.TRUE, "toString", null, null); //$NON-NLS-1$

        assertEquals(Boolean.TRUE.toString(), result);
    }
}
