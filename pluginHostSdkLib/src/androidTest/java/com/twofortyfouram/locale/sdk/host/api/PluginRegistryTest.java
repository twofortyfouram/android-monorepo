/*
 * android-plugin-sdk-for-locale
 * https://github.com/twofortyfouram/android-plugin-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.ThreadUtil;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class PluginRegistryTest {

    public static final long REGISTRY_LOAD_WAIT_MILLIS = 15 * DateUtils.SECOND_IN_MILLIS;

    @SmallTest
    @Test
    public void getInstance_non_null() {
        final PluginRegistry registrySingleton = PluginRegistry
                .getInstance(InstrumentationRegistry.getContext());

        assertThat(registrySingleton, notNullValue());
    }

    @SmallTest
    @Test
    public void getInstance_cached() {
        final PluginRegistry registrySingleton = PluginRegistry
                .getInstance(InstrumentationRegistry.getContext());

        assertThat(PluginRegistry.getInstance(InstrumentationRegistry.getContext()),
                sameInstance(registrySingleton));
    }

    /*
     * If this test hangs, then there is a bug with getInstance() initializing the registry.
     */
    @MediumTest
    @Test(timeout = REGISTRY_LOAD_WAIT_MILLIS)
    public void getInstance_initialized() {
        final PluginRegistry registrySingleton = PluginRegistry
                .getInstance(InstrumentationRegistry.getContext());

        assertThat(registrySingleton, notNullValue());
        assertThat(PluginRegistry.getInstance(InstrumentationRegistry.getContext()),
                sameInstance(registrySingleton));

        registrySingleton.blockUntilLoaded();
    }

    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    public void destroy_singleton() {
        final PluginRegistry registrySingleton = PluginRegistry
                .getInstance(InstrumentationRegistry.getContext());

        registrySingleton.destroy();
    }

    @MediumTest
    @Test
    public void getPluginMap_conditions() {
        final PluginRegistry registry = new PluginRegistry(InstrumentationRegistry.getContext(),
                getIntentAction());

        try {
            registry.init();

            final Map<String, Plugin> conditions = registry.getPluginMap(PluginType.CONDITION);
            assertThat(conditions, notNullValue());
        } finally {
            registry.destroy();
        }
    }

    @MediumTest
    @Test
    public void getPluginMap_settings() {
        final PluginRegistry registry = new PluginRegistry(InstrumentationRegistry.getContext(),
                getIntentAction());

        try {
            registry.init();

            final Map<String, Plugin> settings = registry.getPluginMap(PluginType.SETTING);
            assertThat(settings, notNullValue());
        } finally {
            registry.destroy();
        }
    }

    @SmallTest
    @Test
    public void peekPluginMap_non_blocking() {
        final PluginRegistry registry = new PluginRegistry(InstrumentationRegistry.getContext(),
                getIntentAction());

        try {
            assertThat(registry.peekPluginMap(PluginType.CONDITION), nullValue());
            assertThat(registry.peekPluginMap(PluginType.SETTING), nullValue());

            registry.init();
        } finally {
            registry.destroy();
        }
    }

    @MediumTest
    @Test
    public void peekPluginMap_after_blocking() {
        final PluginRegistry registry = new PluginRegistry(InstrumentationRegistry.getContext(),
                getIntentAction());

        try {
            registry.init();
            registry.blockUntilLoaded();

            assertThat(registry.peekPluginMap(PluginType.CONDITION), notNullValue());
            assertThat(registry.peekPluginMap(PluginType.SETTING), notNullValue());
        } finally {
            registry.destroy();
        }
    }

    @MediumTest
    @Test
    public void loadCompleteNotification() {
        final String intentAction = getIntentAction();

        final PluginRegistry registry = new PluginRegistry(InstrumentationRegistry.getContext(),
                intentAction);

        final HandlerThread backgroundThread = ThreadUtil.newHandlerThread(intentAction,
                ThreadUtil.ThreadPriority.DEFAULT);
        try {
            final CountDownLatch latch = new CountDownLatch(1);

            final BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    Lumberjack.v("Received %s", intent); //$NON-NLS-1$
                    latch.countDown();
                }
            };

            InstrumentationRegistry.getContext()
                    .registerReceiver(receiver, new IntentFilter(intentAction), null,
                            new Handler(backgroundThread.getLooper()));

            registry.init();

            try {
                assertThat(latch.await(REGISTRY_LOAD_WAIT_MILLIS, TimeUnit.MILLISECONDS), is(true));
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

            InstrumentationRegistry.getContext().unregisterReceiver(receiver);
        } finally {
            registry.destroy();
            backgroundThread.quit();
        }
    }

    @NonNull
    private static String getIntentAction() {
        return String
                .format(Locale.US, "com.twofortyfouram.locale.intent.%s",
                        UUID.randomUUID()); //$NON-NLS-1$
    }
}
