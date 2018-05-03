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

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.twofortyfouram.spackle.ThreadUtil.ThreadPriority;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class ThreadUtilTest extends TestCase {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(ThreadUtil.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void isMainThread_not_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        HandlerThread thread = null;
        try {
            thread = ThreadUtil.newHandlerThread("not_main", ThreadPriority.DEFAULT);
            new Handler(thread.getLooper()).post(new Runnable() {

                @Override
                public void run() {
                    assertFalse(ThreadUtil.isMainThread());

                    latch.countDown();
                }

            });

            assertTrue(latch.await(1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS));
        } finally {
            if (null != thread) {
                thread.getLooper().quit();
            }
        }
    }

    @SmallTest
    @Test
    public void isMainThread_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                assertTrue(ThreadUtil.isMainThread());

                latch.countDown();
            }

        });

        assertTrue(latch.await(1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS));
    }

    @MediumTest
    @Test
    public void getHandlerThread() {

        for (final ThreadPriority priority : ThreadPriority.values()) {
            final String threadName = String
                    .format(Locale.US, "%s-%s", "gethandlerthread", priority); //$NON-NLS-1$

            HandlerThread thread = null;
            try {
                thread = ThreadUtil.newHandlerThread(threadName, priority);

                assertNotNull(thread);
                assertEquals(threadName, thread.getName());
                assertTrue(thread.isAlive());
                assertNotNull(thread.getLooper());
                assertNotSame(
                        "Should not be main thread", Looper.getMainLooper(),
                        thread.getLooper()); //$NON-NLS-1$
                assertNotSame("Should not be current thread", Looper.myLooper(),
                        thread.getLooper()); //$NON-NLS-1$

                /*
                 * There appears to be a race condition before thread priorities are set, so a
                 * slight delay is needed before thread priority can be read.
                 */
                SystemClock.sleep(100);

                assertEquals(priority.getPriority(),
                        android.os.Process.getThreadPriority(thread.getThreadId()));
            } finally {
                if (null != thread) {
                    thread.getLooper().quit();
                }
            }
        }
    }

    @SmallTest
    @Test
    public void getHandlerThread_new_instance() {
        final String threadName = "new_instance";

        HandlerThread thread1 = null;
        HandlerThread thread2 = null;
        try {
            thread1 = ThreadUtil.newHandlerThread(threadName, ThreadPriority.DEFAULT);
            thread2 = ThreadUtil.newHandlerThread(threadName, ThreadPriority.DEFAULT);

            assertNotSame(thread1, thread2);
        } finally {
            if (null != thread1) {
                thread1.getLooper().quit();
            }

            if (null != thread2) {
                thread2.getLooper().quit();
            }
        }
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void getHandlerThread_bad_parameter_empty_name() {
        ThreadUtil.newHandlerThread("", ThreadPriority.DEFAULT); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void getHandlerThread_bad_parameter_null_name() {
        ThreadUtil.newHandlerThread(null, ThreadPriority.DEFAULT);
    }
}
