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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.intent.matcher.ComponentNameMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginErrorEdit;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.test.fixture.PluginFixture;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public final class PluginEditDelegateTest {

    @SmallTest
    @Test
    public void isIntentValid_null() {
        assertThat(
                PluginEditDelegate.isIntentValid(null, PluginFixture.newDefaultPlugin())
                , not(empty())
        );
    }

    @SmallTest
    @Test
    public void isIntentValid_empty() {
        assertThat(PluginEditDelegate
                .isIntentValid(new Intent(), PluginFixture.newDefaultPlugin()), not(empty()));
    }

    @SmallTest
    @Test
    public void isIntentValid_missing_blurb() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtra(LocalePluginIntent.EXTRA_BUNDLE,
                        new Bundle()), PluginFixture.newDefaultPlugin()
        ), contains(PluginErrorEdit.BLURB_MISSING));
    }

    @SmallTest
    @Test
    public void isIntentValid_missing_bundle() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                        "foo"), PluginFixture.newDefaultPlugin()
        ), not(empty())); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void isIntentValid_null_blurb() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtras(generateBundle(new Bundle(), null)),
                PluginFixture.newDefaultPlugin()), not(empty()));
    }

    @SmallTest
    @Test
    public void isIntentValid_null_empty() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtras(generateBundle(new Bundle(), "")),
                PluginFixture.newDefaultPlugin()), empty()); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void isIntentValid_null_bundle() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtras(generateBundle(null, "foo")),
                PluginFixture.newDefaultPlugin())
                , not(empty())); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void isIntentValid_valid() {
        assertThat(PluginEditDelegate.isIntentValid(
                new Intent().putExtras(generateBundle(new Bundle(), "foo")),
                PluginFixture.newDefaultPlugin()), empty()); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void getPluginStartIntent_old_condition() throws Exception {
        final Plugin plugin = PluginFixture.newDefaultPlugin();
        final PluginInstanceData pluginInstanceData = new PluginInstanceData(plugin.getType(),
                plugin.getRegistryName(), new Bundle(),
                "bar");
        final Intent i = PluginEditDelegate.getPluginStartIntent(
                plugin,
                pluginInstanceData, "Edit Situation"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(i, notNullValue());
        assertThat(i, hasAction(LocalePluginIntent.ACTION_EDIT_CONDITION));
        assertThat(i.getComponent(),
                ComponentNameMatchers.hasPackageName(plugin.getPackageName()));
        assertThat(i.getComponent(),
                ComponentNameMatchers.hasClassName(plugin.getActivityClassName()));
        assertThat(i.getData(), nullValue());

        BundleAssertions.assertHasString(i.getExtras(),
                LocalePluginIntent.EXTRA_STRING_BREADCRUMB,
                "Edit Situation"); //$NON-NLS-1$
        BundleAssertions.assertKeyCount(i.getExtras(), 3);
    }

    @SmallTest
    @Test
    public void getPluginStartIntent_new_condition() {
        final Plugin plugin = PluginFixture.newDefaultPlugin();

        final Intent i = PluginEditDelegate.getPluginStartIntent(
                PluginFixture.newDefaultPlugin(), null, "Edit Situation"); //$NON-NLS-1$

        assertNotNull(i);
        assertThat(i, hasAction(LocalePluginIntent.ACTION_EDIT_CONDITION));
        assertThat(i.getComponent(),
                ComponentNameMatchers.hasPackageName(plugin.getPackageName()));
        assertThat(i.getComponent(),
                ComponentNameMatchers.hasClassName(plugin.getActivityClassName()));
        assertThat(i.getData(), nullValue());

        BundleAssertions.assertHasString(i.getExtras(),
                LocalePluginIntent.EXTRA_STRING_BREADCRUMB,
                "Edit Situation"); //$NON-NLS-1$
        BundleAssertions.assertKeyCount(i.getExtras(), 1);
    }

    @NonNull
    private static Bundle generateBundle(@Nullable final Bundle bundle,
            @Nullable final String blurb) {
        final Bundle result = new Bundle();
        result.putString(LocalePluginIntent.EXTRA_STRING_BLURB, blurb);
        result.putBundle(LocalePluginIntent.EXTRA_BUNDLE, bundle);

        return result;
    }
}
