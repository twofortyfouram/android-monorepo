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

package com.twofortyfouram.spackle.power;

import android.Manifest;
import androidx.annotation.RequiresPermission;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public final class PartialWakeLockTest {

    @SmallTest
    @Test
    public void constructor_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock.newInstance(getContext(), "test_lock",
                false);

        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    public void constructor_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        assertThat(manager.isHeld(), is(false));
        assertThat(manager.getReferenceCount(), is(0));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void acquire_single_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", false);

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void acquire_single_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void acquire_multiple_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", false);

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void acquire_multiple_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(2));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(2));
        assertThat(manager.isHeld(), is(true));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(3));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(2));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void underlock_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", false);

        manager.releaseLock();
    }

    @SmallTest
    @Test(expected = IllegalStateException.class)
    public void underlock_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        manager.releaseLock();
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void releaseIfHeld_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", false);

        manager.releaseLockIfHeld();

        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        manager.releaseLockIfHeld();
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void testReleaseIfHeld_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        manager.releaseLockIfHeld();

        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        manager.acquireLock();
        assertThat(manager.getReferenceCount(), is(2));
        manager.releaseLockIfHeld();
        assertThat(manager.getReferenceCount(), is(1));
        manager.releaseLockIfHeld();
        assertThat(manager.getReferenceCount(), is(0));
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void testAcquireIfNotHeld_not_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", false);

        manager.acquireLockIfNotHeld();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        manager.acquireLockIfNotHeld();
        assertThat(manager.getReferenceCount(), is(1));

        manager.releaseLock();
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void testAcquireIfNotHeld_reference_counted() {
        final PartialWakeLock manager = PartialWakeLock
                .newInstance(getContext(), "test_lock", true);

        manager.acquireLockIfNotHeld();
        assertThat(manager.getReferenceCount(), is(1));
        assertThat(manager.isHeld(), is(true));

        manager.releaseLock();
        assertThat(manager.isHeld(), is(false));

        manager.acquireLock();
        manager.acquireLockIfNotHeld();
        assertThat(manager.getReferenceCount(), is(1));

        manager.releaseLock();
        assertThat(manager.isHeld(), is(false));
    }

    @SmallTest
    @Test
    public void testToString() {
        assertThat(PartialWakeLock.newInstance(getContext(), "test_lock", false).toString(),
                notNullValue());
    }
}
