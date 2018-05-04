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

import com.twofortyfouram.test.rule.UtcRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.twofortyfouram.spackle.TimeFormatter.TimeFormat.ISO_8601;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class TimeFormatterTest {

    @Rule
    public final UtcRule mUtcRule = new UtcRule();

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(TimeFormatter.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void formatTimeIso8601() {
        final long wallTimeMillis = 1485450137000L;

        final String expectedTimeUTC = "2017-01-26 17:02:17.000+0000"; //$NON-NLS

        assertThat(TimeFormatter.formatTime(ISO_8601, wallTimeMillis), is(expectedTimeUTC));
    }

    @SmallTest
    @Test
    public void parseTimeIso8601() {
        //this is not TimeZone dependant, because Date is compared using internal millis
        final String sourceString = "2017-01-26 18:02:17.000+0100"; //$NON-NLS
        final long sourceInMillis = 1485450137000L;

        assertThat(TimeFormatter.parseTime(ISO_8601, sourceString),
                is(new Date(sourceInMillis)));
    }
}
