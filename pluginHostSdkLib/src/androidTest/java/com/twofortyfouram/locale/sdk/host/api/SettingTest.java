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

package com.twofortyfouram.locale.sdk.host.api;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginConfigurationFixture;
import com.twofortyfouram.spackle.Clock;
import com.twofortyfouram.test.context.ReceiverContextWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public final class SettingTest {

    @SmallTest
    @Test
    public void fire_intent() {
        final ReceiverContextWrapper context = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        final Setting setting = new Setting(context, Clock.getInstance(),
                new Plugin(PluginType.SETTING,
                        "foo", "bar", "baz", 1, PluginConfigurationFixture
                        .newPluginConfiguration()
                )); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$//$NON-NLS-2$

        setting.fire(new PluginInstanceData(PluginType.SETTING,
                Plugin.generateRegistryName("foo", "bar"),//$NON-NLS-1$//$NON-NLS-2$
                bundle, "")); //$NON-NLS-1$
        setting.destroy();

        final Collection<ReceiverContextWrapper.SentIntent> intents = context
                .getAndClearSentIntents();
        assertThat(intents.size(), is(2));

        for (final ReceiverContextWrapper.SentIntent sentIntent : intents) {
            assertThat(sentIntent.getIsSticky(), is(false));
            assertThat(sentIntent.getPermission(), nullValue());

            final Intent intent = sentIntent.getIntent();

            assertThat(intent.getAction(),
                    is(LocalePluginIntent.ACTION_FIRE_SETTING));
            assertThat(intent.getComponent(),
                    is(new ComponentName("foo", "baz"))); //$NON-NLS-1$ //$NON-NLS-2$
            assertThat(intent.getExtras(), notNullValue());
            assertThat(intent.getExtras().size(), is(1));
            assertThat(intent.hasExtra(LocalePluginIntent.EXTRA_BUNDLE),
                    is(true));

            final Bundle extraBundle = intent
                    .getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE);

            assertThat(extraBundle.size(), is(1));
            assertThat(extraBundle.get("test_key"),
                    is("test_value")); //$NON-NLS-1$//$NON-NLS-2$
        }
    }
}
