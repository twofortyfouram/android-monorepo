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

import android.content.Context;
import android.content.ContextWrapper;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class ContextUtilTest {

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(ContextUtil.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void cleanContext() {
        final Context cleanableContext = new CleanableContext(getContext());

        assertThat(cleanableContext, not(sameInstance(ContextUtil.cleanContext
                (cleanableContext))));
        assertThat(cleanableContext.getApplicationContext(),
                sameInstance(ContextUtil.cleanContext(cleanableContext)));
    }

    @Test(expected = AssertionError.class)
    @SmallTest
    public void cleanContext_null() {
        ContextUtil.cleanContext(null);
    }

    private static final class CleanableContext extends ContextWrapper {

        public CleanableContext(final Context base) {
            super(base);
        }

    }
}
