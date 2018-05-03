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

package com.twofortyfouram.spackle.bundle;

import android.os.Bundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class BundlePrinterTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(BundlePrinter.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void toString_null_bundle() {
        assertThat(BundlePrinter.toString(null), is("null")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void toString_empty_bundle() {
        assertThat(BundlePrinter.toString(new Bundle()), is("empty")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void toString_null_key() {
        final Bundle bundle = new Bundle();
        bundle.putString(null, "test"); //$NON-NLS-1$
        assertThat(BundlePrinter.toString(bundle), is("{null = test}"));
    }

    @SmallTest
    @Test
    public void toString_null_value() {
        final Bundle bundle = new Bundle();
        bundle.putString("test", null); //$NON-NLS-1$
        assertThat(BundlePrinter.toString(bundle), is("{test = null}"));
    }

    @SmallTest
    @Test
    public void toString_recursive() {
        final Bundle bundle1 = new Bundle();
        final Bundle bundle2 = new Bundle();
        bundle1.putBundle("bundle2", bundle2); //$NON-NLS-1$
        bundle2.putString("test", "test"); //$NON-NLS-1$//$NON-NLS-2$

        assertThat(BundlePrinter.toString(bundle1), is("{bundle2 = {test = test}}")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void toString_primitive_array() {
        final Bundle bundle1 = new Bundle();
        bundle1.putIntArray("test", new int[]{1, 2, 3}); //$NON-NLS-1$
        assertThat(BundlePrinter.toString(bundle1), is("{test = [1, 2, 3]}")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void toString_object_array() {
        final Bundle bundle1 = new Bundle();
        bundle1.putStringArray("test", new String[]{"a", "b",
                "c"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertThat(BundlePrinter.toString(bundle1), is("{test = [a, b, c]}")); //$NON-NLS-1$
    }
}
