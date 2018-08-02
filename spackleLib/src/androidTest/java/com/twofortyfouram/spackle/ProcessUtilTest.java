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

import androidx.annotation.NonNull;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class ProcessUtilTest {

    @NonNull
    /*package*/ static final String TEST_PACKAGE_PROCESS = "com.twofortyfouram.spackle.test";
            //$NON-NLS

    @SmallTest
    @Test
    public void getProcessName() {

        assertThat(ProcessUtil.getProcessName(InstrumentationRegistry.getContext()),
                is(TEST_PACKAGE_PROCESS));
    }

    @SmallTest
    @Test
    public void getProcessName_not_empty() {
        final String processName = ProcessUtil.getProcessName(InstrumentationRegistry.getContext());

        assertThat(processName, not(isEmptyString()));
    }

    @SmallTest
    @Test
    public void getProcessName_not_null() {
        final String processName = ProcessUtil.getProcessName(InstrumentationRegistry.getContext());

        assertThat(processName, notNullValue());
    }

    @SmallTest
    @Test
    public void getProcessName_same_object() {
        assertThat(ProcessUtil.getProcessName(InstrumentationRegistry.getContext()),
                sameInstance(
                        ProcessUtil.getProcessName(InstrumentationRegistry.getContext())));
    }

    @SmallTest
    @Test
    public void searchForProcessName() {
        assertThat(ProcessUtil.searchForProcessName(InstrumentationRegistry.getContext()),
                is(TEST_PACKAGE_PROCESS));
    }

    @SmallTest
    @Test
    public void setProcessName() {
        final String expectedProcessName = "bork"; //$NON-NLS

        final String before = ProcessUtil.getProcessName(InstrumentationRegistry.getContext());
        try {
            ProcessUtil.setProcessName(expectedProcessName);
            assertThat(ProcessUtil.getProcessName(InstrumentationRegistry.getContext()),
                    is(expectedProcessName));
        } finally {
            ProcessUtil.setProcessName(before);
        }
    }
}