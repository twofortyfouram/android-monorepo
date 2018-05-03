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

package com.twofortyfouram.spackle;


import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.spackle.TimeFormatter.TimeFormat.ISO_8601;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class TimeFormatterTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(TimeFormatter.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void formatTimeIso8601() {
        final long wallTimeMillis = 1485450137000L;

        // Need to match on substring to avoid time zone issues with tests
        assertThat(TimeFormatter.formatTime(ISO_8601, wallTimeMillis),
                containsString("2017-01-26")); //$NON-NLS
        assertThat(TimeFormatter.formatTime(ISO_8601, wallTimeMillis),
                containsString("02:17.000")); //$NON-NLS
    }
}
