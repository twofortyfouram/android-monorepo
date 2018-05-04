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

import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.spackle.HandlerThreadFactory.ThreadPriority;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotSame;

@RunWith(AndroidJUnit4.class)
public final class HandlerThreadFactoryTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(HandlerThreadFactory.class, notInstantiable());
    }

    @MediumTest
    @Test
    public void newHandlerThread() {

        for (final ThreadPriority priority : ThreadPriority.values()) {
            final String threadName = String
                    .format(Locale.US, "%s-%s", "test", priority); //$NON-NLS-1$

            HandlerThread thread = null;
            try {
                thread = HandlerThreadFactory.newHandlerThread(threadName, priority);

                assertThat(thread, notNullValue());
                assertThat(thread.getName(), is(threadName));
                assertTrue(thread.isAlive());
                assertThat(thread.getLooper(), notNullValue());
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

                assertThat(android.os.Process.getThreadPriority(thread.getThreadId()),
                        is(priority.getPriority()));
            } finally {
                if (null != thread) {
                    thread.getLooper().quit();
                }
            }
        }
    }

    @SmallTest
    @Test
    public void newHandlerThread_new_instance() {
        final String threadName = "test_thread_name"; //$NON-NLS

        HandlerThread thread1 = null;
        HandlerThread thread2 = null;
        try {
            thread1 = HandlerThreadFactory.newHandlerThread(threadName, ThreadPriority.DEFAULT);
            thread2 = HandlerThreadFactory.newHandlerThread(threadName, ThreadPriority.DEFAULT);

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
    public void newHandlerThread_bad_parameter_empty_name() {
        HandlerThreadFactory.newHandlerThread("", ThreadPriority.DEFAULT); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void newHandlerThread_bad_parameter_null_name() {
        HandlerThreadFactory.newHandlerThread(null, ThreadPriority.DEFAULT);
    }
}
