/*
 * android-memento
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

package com.twofortyfouram.memento.internal;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;

@RunWith(AndroidJUnit4.class)
public final class BatchHelperTest {

    @Test
    @SmallTest
    public void mergeStacktraces_both_non_empty() {
        final Exception e1 = new Exception();
        final StackTraceElement element1 = new StackTraceElement("class_foo", "method_bar", "file_baz", 1);
        e1.setStackTrace(new StackTraceElement[] {element1});

        final Exception e2 = new Exception();
        final StackTraceElement element2 = new StackTraceElement("class_beep", "method_boop", "file_bop", 1);
        e2.setStackTrace(new StackTraceElement[] {element2});

        final StackTraceElement[] merged = BatchHelper.mergeStacktraces(e1, e2);
        assertThat(merged, arrayWithSize(2));

        assertThat(merged, arrayContaining(element1, element2));
    }
}
