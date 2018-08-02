/*
 * android-assertion
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

package com.twofortyfouram.assertion;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class AssertionsTest {

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_int_below() {
        Assertions.assertInRangeInclusive(-1, 0, 5, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertInRangeInclusive_int_in_range() {
        Assertions.assertInRangeInclusive(0, 0, 5, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(3, 0, 5, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(5, 0, 5, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_int_above() {
        Assertions.assertInRangeInclusive(6, 0, 5, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void assertInRangeInclusive_bad_range() {
        Assertions.assertInRangeInclusive(0, 5, 0, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_long_below() {
        Assertions.assertInRangeInclusive(-1L, 0L, 5L, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertInRangeInclusive_long_in_range() {
        Assertions.assertInRangeInclusive(0L, 0L, 5L, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(3L, 0L, 5L, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(5L, 0L, 5L, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_long_above() {
        Assertions.assertInRangeInclusive(6L, 0L, 5L, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void assertInRangeInclusive_long_bad_range() {
        Assertions.assertInRangeInclusive(0L, 5L, 0L, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_float_below() {
        Assertions.assertInRangeInclusive(-1f, 0f, 5f, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertInRangeInclusive_float_in_range() {
        Assertions.assertInRangeInclusive(0f, 0f, 5f, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(3f, 0f, 5f, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(5f, 0f, 5f, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_float_above() {
        Assertions.assertInRangeInclusive(6f, 0f, 5f, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void assertInRangeInclusive_float_bad_range() {
        Assertions.assertInRangeInclusive(0f, 5f, 0f, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_double_below() {
        Assertions.assertInRangeInclusive(-1d, 0d, 5d, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertInRangeInclusive_double_in_range() {
        Assertions.assertInRangeInclusive(0d, 0d, 5d, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(3d, 0d, 5d, "test");  //$NON-NLS-1$
        Assertions.assertInRangeInclusive(5d, 0d, 5d, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInRangeInclusive_double_above() {
        Assertions.assertInRangeInclusive(6d, 0d, 5d, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = IllegalArgumentException.class)
    public void assertInRangeInclusive_double_bad_range() {
        Assertions.assertInRangeInclusive(0d, 5d, 0d, "test");  //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNoNullElements_collection_non_empty() {
        final LinkedList<String> list = new LinkedList<>();
        list.add("test"); //$NON-NLS-1$

        Assertions.assertNoNullElements(list, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNoNullElements_collection_empty() {
        Assertions.assertNoNullElements(new LinkedList<String>(), "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_collection_null_collection() {
        Assertions.assertNoNullElements((Collection) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_collection_null_element() {
        final LinkedList<String> list = new LinkedList<>();
        list.add("test"); //$NON-NLS-1$
        list.add(null);

        Assertions.assertNoNullElements(list, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNoNullElements_array_non_empty() {
        final String[] array = new String[]{"foo"}; //$NON-NLS-1$

        Assertions.assertNoNullElements(array, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNoNullElements_array_empty() {
        final String[] array = new String[0];

        Assertions.assertNoNullElements(array, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_array_null_collection() {
        Assertions.assertNoNullElements((Object[]) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_array_null_element() {
        final String[] array = new String[]{"foo", null}; //$NON-NLS-1$

        Assertions.assertNoNullElements(array, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNoNullElements_map_non_empty() {
        Assertions
                .assertNoNullElements(Collections.singletonMap("key", "value"), "test"); //$NON-NLS
    }

    @SmallTest
    @Test
    public void assertNoNullElements_map_empty() {
        Assertions.assertNoNullElements(Collections.emptyMap(), "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_map_null_map() {
        Assertions.assertNoNullElements((Map) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_map_null_key() {
        Assertions
                .assertNoNullElements(Collections.singletonMap(null, "value"), "test"); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNoNullElements_map_null_value() {
        Assertions
                .assertNoNullElements(Collections.singletonMap("key", null), "test"); //$NON-NLS
    }

    @SmallTest
    @Test
    public void assertInSet() {
        Assertions.assertInSet("test", "test"); //$NON-NLS-1$ //$NON-NLS-2$
        Assertions.assertInSet("test", "foo", "test"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        Assertions.assertInSet("test", "test", "foo"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertInSet_empty() {
        Assertions.assertInSet("test", (Object[]) new String[]{}); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertInSet_null() {
        Assertions.assertInSet("test", null, "test"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test
    public void assertNotNull_not_null() {
        final String result = Assertions.assertNotNull("test", "test"); //$NON-NLS-1$//$NON-NLS-2$

        assertThat(result, notNullValue());
        assertThat(result, sameInstance("test"));
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotNull_null() {
        Assertions.assertNotNull(null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_empty_string() {
        Assertions.assertNotEmpty("", "test"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test
    public void assertNotEmpty_not_empty_string() {
        Assertions.assertNotEmpty("test", "test"); //$NON-NLS-1$//$NON-NLS-2$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_null_string() {
        Assertions.assertNotEmpty((String) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNotEmpty_not_empty_collection() {
        Assertions.assertNotEmpty(Collections.singleton("key"), "test"); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_empty_collection() {
        Assertions.assertNotEmpty(Collections.emptyList(), "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_null_collection() {
        Assertions.assertNotEmpty((Collection) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertNotEmpty_not_empty_map() {
        Assertions.assertNotEmpty(Collections.singletonMap("key", "value"), "test"); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_empty_map() {
        Assertions.assertNotEmpty(Collections.emptyMap(), "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void assertNotEmpty_null_map() {
        Assertions.assertNotEmpty((Map) null, "test"); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void assertIsMainThread_not_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        HandlerThread thread = null;
        try {
            thread = new HandlerThread("test thread", android.os.Process.THREAD_PRIORITY_DEFAULT);
            thread.start();
            new Handler(thread.getLooper()).post(() -> {
                try {
                    Assertions.assertIsMainThread();
                    Assert.fail();
                } catch (final AssertionError e) {
                    // Expected exception
                }

                latch.countDown();
            });

            assertThat(latch.await(250, TimeUnit.MILLISECONDS), is(true));
        } finally {
            if (null != thread) {
                thread.getLooper().quit();
            }
        }
    }

    @SmallTest
    @Test
    public void assertIsMainThread_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(() -> {
            Assertions.assertIsMainThread();

            latch.countDown();
        });

        assertThat(latch.await(1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS), is(true));
    }


    @SmallTest
    @Test
    public void assertIsNotMainThread_not_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        HandlerThread thread = null;
        try {
            thread = new HandlerThread("test thread", android.os.Process.THREAD_PRIORITY_DEFAULT);
            thread.start();
            new Handler(thread.getLooper()).post(() -> {

                Assertions.assertIsNotMainThread();

                latch.countDown();
            });

            assertThat(latch.await(250, TimeUnit.MILLISECONDS), is(true));
        } finally {
            if (null != thread) {
                thread.getLooper().quit();
            }
        }
    }

    @SmallTest
    @Test
    public void assertIsNotMainThread_on_main() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                Assertions.assertIsNotMainThread();
                Assert.fail();
            } catch (final AssertionError e) {
                // Expected exception
            }

            latch.countDown();
        });

        assertThat(latch.await(1 * DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS), is(true));
    }
}
