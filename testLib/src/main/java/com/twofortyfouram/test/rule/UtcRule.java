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

package com.twofortyfouram.test.rule;

import android.support.annotation.NonNull;

import net.jcip.annotations.Immutable;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.TimeZone;

/**
 * Rule to temporarily switch the time zone to UTC and restore it afterwards.
 */
@Immutable
public final class UtcRule extends TestWatcher {

    /*
     * Note there is a possible race condition if something else on the system changes the time zone
     * after starting() is called.  The probability of that happening is relatively low during
     * tests.
     */

    @NonNull
    private final TimeZone mOrigDefault = TimeZone.getDefault();

    @Override
    protected void starting(@NonNull final Description description) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); //$NON-NLS
    }

    @Override
    protected void finished(@NonNull final Description description) {
        TimeZone.setDefault(mOrigDefault);
    }
}
