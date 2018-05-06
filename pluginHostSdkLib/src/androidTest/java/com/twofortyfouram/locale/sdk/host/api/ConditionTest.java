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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.locale.sdk.host.test.condition.bundle.PluginJsonValues;
import com.twofortyfouram.locale.sdk.host.test.fixture.DebugPluginFixture;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginConfigurationFixture;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.Clock;
import com.twofortyfouram.test.context.ReceiverContextWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasMyPackageName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
public final class ConditionTest {

    @SuppressLint("NewApi")
    @SmallTest
    @Test
    public void getQueryIntent() throws JSONException {
        final Plugin plugin = DebugPluginFixture.getDebugPluginCondition();
        final Bundle bundle = newBundle(PluginJsonValues
                .generateJson(InstrumentationRegistry.getContext(),
                        LocalePluginIntent.RESULT_CONDITION_SATISFIED));

        final Intent intent = Condition.newQueryIntent(plugin, bundle);
        assertThat(intent, notNullValue());

        final int expectedFlags;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB_MR1)) {
            expectedFlags = Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_INCLUDE_STOPPED_PACKAGES;
        } else {
            expectedFlags = Intent.FLAG_FROM_BACKGROUND;
        }
        assertThat(intent.getFlags(), is(expectedFlags));

        assertThat(intent.getAction(),
                is(LocalePluginIntent.ACTION_QUERY_CONDITION));
        assertThat(intent.getComponent(), hasMyPackageName());
        assertThat(intent.getComponent(),
                hasClassName(plugin.getReceiverClassName()));

        assertThat(intent.getData(), nullValue());
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.JELLY_BEAN)) {
            assertThat(intent.getClipData(), nullValue());
        }
        assertThat(intent.getExtras().size(), is(1));

        final Bundle extraBundle = intent
                .getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE);
        assertThat(extraBundle, notNullValue());

        final String jsonString = extraBundle.getString(LocalePluginIntent.EXTRA_STRING_JSON);
        assertThat(jsonString, notNullValue());

        JSONObject jsonObject = new JSONObject(jsonString);

        PluginJsonValues.isJsonValid(jsonObject);
    }

    @SmallTest
    @Test
    public void query_intent() {
        final ReceiverContextWrapper context = new ReceiverContextWrapper(
                InstrumentationRegistry.getContext());

        final Condition condition = new Condition(context, Clock.getInstance(), new Plugin(
                PluginType.CONDITION, "foo", "bar", "baz",
                1, PluginConfigurationFixture
                .newPluginConfiguration()
        )); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        try {
            final Bundle bundle = new Bundle();
            bundle.putString("test_key", "test_value"); //$NON-NLS-1$//$NON-NLS-2$

            assertThat(condition.query(getPluginInstanceData(bundle),
                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN),
                    is(LocalePluginIntent.RESULT_CONDITION_UNKNOWN)

            );
        } finally {
            condition.destroy();
        }

        final Collection<ReceiverContextWrapper.SentIntent> intents = context
                .getAndClearSentIntents();
        assertThat(intents.size(), is(1));

        for (final ReceiverContextWrapper.SentIntent sentIntent : intents) {
            assertThat(sentIntent.getIsSticky(), is(false));
            assertThat(sentIntent.getPermission(), nullValue());
            assertThat(sentIntent.getIsOrdered(), is(true));

            // Don't worry about the action, extras, etc.
        }
    }

    @MediumTest
    @Test
    public void query_satisfied() {
        assertQuerySatisfiedWithState(
                LocalePluginIntent.RESULT_CONDITION_SATISFIED);
    }

    @MediumTest
    @Test
    public void query_unsatisfied() {
        assertQuerySatisfiedWithState(
                LocalePluginIntent.RESULT_CONDITION_UNSATISFIED);
    }

    @MediumTest
    @Test
    public void query_unknown() {
        assertQuerySatisfiedWithState(
                LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
    }

    @MediumTest
    @Test
    public void query_bad_state() {
        final Condition condition = getCondition();

        try {
            final Bundle bundle = new Bundle();
            bundle.putInt(PluginJsonValues.INT_VERSION_CODE, 1);
            bundle.putInt(PluginJsonValues.INT_RESULT_CODE,
                    1); //$NON-NLS-1$

            assertThat(condition.query(getPluginInstanceData(bundle),
                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN),
                    is(LocalePluginIntent.RESULT_CONDITION_UNKNOWN));
        } finally {
            condition.destroy();
        }
    }

    private void assertQuerySatisfiedWithState(final int state) {
        final Condition condition = getCondition();

        try {
            final Bundle bundle = newBundle(PluginJsonValues
                    .generateJson(InstrumentationRegistry.getContext(), state));
            assertThat(condition
                            .query(getPluginInstanceData(bundle),
                                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN),
                    is(state));
        } finally {
            condition.destroy();
        }
    }

    @NonNull
    private Condition getCondition() {
        return new Condition(InstrumentationRegistry.getContext(), Clock.getInstance(),
                DebugPluginFixture.getDebugPluginCondition());
    }

    @NonNull
    private PluginInstanceData getPluginInstanceData(@NonNull final Bundle bundle) {
        final Plugin debugPlugin = DebugPluginFixture.getDebugPluginCondition();

        return new PluginInstanceData(debugPlugin.getType(), debugPlugin.getRegistryName(),
                bundle, "");
    }

    @NonNull
    private static Bundle newBundle(@NonNull final JSONObject json) {
        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, json.toString());

        return bundle;
    }

}
