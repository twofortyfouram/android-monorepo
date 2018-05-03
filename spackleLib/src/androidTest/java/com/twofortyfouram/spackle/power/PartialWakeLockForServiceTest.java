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

package com.twofortyfouram.spackle.power;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class PartialWakeLockForServiceTest {

    @SmallTest
    @Test
    public void testConstructor_good() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        assertThat(helper.getWakeLock(getContext()).isHeld(), is(false));
    }

    @SmallTest
    @Test
    public void testGetWakeLock() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        final PartialWakeLock wakeLock = helper.getWakeLock(getContext());
        assertThat(wakeLock, notNullValue());

        assertThat(wakeLock, sameInstance(helper.getWakeLock(getContext())));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void testBeforeStartingService() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        helper.beforeStartingService(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(1));

        helper.beforeStartingService(getContext());
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(2));
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void testOnStartCommand() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        helper.beforeDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(1));

        helper.beforeDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(1));
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void afterDoingWork_no_start() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        helper.afterDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void afterDoingWork_with_onStartCommand() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        helper.beforeDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));

        helper.afterDoingWork(getContext());
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void afterDoingWork_full() {
        final PartialWakeLockForService helper = new PartialWakeLockForService("test_lock");

        assertThat(helper.getWakeLock(getContext()).isHeld(), is(false));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(0));

        helper.beforeStartingService(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(1));

        helper.beforeDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(true));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(1));

        helper.afterDoingWork(getContext());
        assertThat(helper.getWakeLock(getContext()).isHeld(), is(false));
        assertThat(helper.getWakeLock(getContext()).getReferenceCount(), is(0));
    }
}
