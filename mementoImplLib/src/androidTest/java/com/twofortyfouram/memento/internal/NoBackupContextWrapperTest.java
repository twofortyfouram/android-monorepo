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

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class NoBackupContextWrapperTest {
    @Test
    @SmallTest
    public void getApplicationContext_no_breakout() {
        @NonNull final NoBackupContextWrapper wrapper = new NoBackupContextWrapper(ApplicationProvider.getApplicationContext());
        assertThat(wrapper.getApplicationContext(), sameInstance(wrapper));
    }

    @Test
    @SmallTest
    public void getBaseContext_no_breakout() {
        @NonNull final NoBackupContextWrapper wrapper = new NoBackupContextWrapper(ApplicationProvider.getApplicationContext());
        assertThat(wrapper.getBaseContext(), sameInstance(wrapper));
    }

    @Test
    @SmallTest
    public void getDatabasePath() {
        @NonNull final NoBackupContextWrapper wrapper = new NoBackupContextWrapper(ApplicationProvider.getApplicationContext());
        @NonNull final File actualDatabasePath = wrapper.getDatabasePath("foo");
        @NonNull final File expectedDatabasePath = new File(ApplicationProvider.getApplicationContext().getNoBackupFilesDir(), "foo");

        assertThat(actualDatabasePath, is(expectedDatabasePath));
    }
}
