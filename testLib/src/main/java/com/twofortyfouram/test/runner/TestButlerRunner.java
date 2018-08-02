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

package com.twofortyfouram.test.runner;


import android.os.Bundle;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnitRunner;

import com.linkedin.android.testbutler.TestButler;

/**
 * Runner to enable TestButler.
 */
public final class TestButlerRunner extends AndroidJUnitRunner {

    @Override
    public void onStart() {
        // Ordering is important; must come before super
        TestButler.setup(InstrumentationRegistry.getTargetContext());

        super.onStart();
    }

    @Override
    public void finish(final int resultCode, final Bundle results) {
        TestButler.teardown(InstrumentationRegistry.getTargetContext());

        super.finish(resultCode, results);
    }
}
