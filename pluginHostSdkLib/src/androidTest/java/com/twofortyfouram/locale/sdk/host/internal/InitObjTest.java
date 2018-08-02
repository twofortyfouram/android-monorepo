/*
 * android-plugin-host-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.internal;


import android.os.Handler;
import android.os.HandlerThread;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.spackle.HandlerThreadFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class InitObjTest {

    @SmallTest
    @Test
    public void getHandler() {
        final HandlerThread thread = HandlerThreadFactory.newHandlerThread("Testy McTesterson",
                HandlerThreadFactory.ThreadPriority.BACKGROUND);
        try {
            final Handler handler = new Handler(thread.getLooper());
            final CountDownLatch latch = new CountDownLatch(1);
            final PluginRegistryHandlerCallback.InitObj obj
                    = new PluginRegistryHandlerCallback.InitObj(handler, latch);

            assertThat(obj.getHandler(), is(handler));
        } finally {
            thread.getLooper().quit();
        }
    }

    @SmallTest
    @Test
    public void getLatch() {
        final HandlerThread thread = HandlerThreadFactory.newHandlerThread("Testy McTesterson",
                HandlerThreadFactory.ThreadPriority.BACKGROUND);
        try {
            final Handler handler = new Handler(thread.getLooper());
            final CountDownLatch latch = new CountDownLatch(1);
            final PluginRegistryHandlerCallback.InitObj obj
                    = new PluginRegistryHandlerCallback.InitObj(
                    handler, latch);

            assertThat(obj.getCountDownLatch(), is(latch));
        } finally {
            thread.getLooper().quit();
        }
    }

}
