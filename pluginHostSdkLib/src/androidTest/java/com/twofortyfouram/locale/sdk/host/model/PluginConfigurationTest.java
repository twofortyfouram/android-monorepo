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

package com.twofortyfouram.locale.sdk.host.model;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.host.test.fixture.PluginConfigurationFixture;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public final class PluginConfigurationTest {

    @SmallTest
    @Test
    public void getIsBackwardsCompatibilityEnabled_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration()
                .isBackwardsCompatibilityEnabled(), is(false));
    }

    @SmallTest
    @Test
    public void getIsBackwardsCompatibilityEnabled_true() {
        final PluginConfiguration configuration = new PluginConfiguration(true, false, false,
                false, false, false, new LinkedList<>());

        assertThat(configuration.isBackwardsCompatibilityEnabled(), is(true));
    }

    @SmallTest
    @Test
    public void isRequiresConnectivity_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration().isRequiresConnectivity(),
                is(false));
    }

    @SmallTest
    @Test
    public void isRequiresConnectivity_true() {
        final PluginConfiguration configuration = new PluginConfiguration(false, true, false,
                false, false, false, new LinkedList<>());

        assertThat(configuration.isRequiresConnectivity(),
                is(true));
    }

    @SmallTest
    @Test
    public void isDisruptsConnectivity_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration().isDisruptsConnectivity(),
                is(false));
    }

    @SmallTest
    @Test
    public void isDisruptsConnectivity_true() {
        final PluginConfiguration configuration = new PluginConfiguration(false, false, true,
                false, false, false, new LinkedList<>());

        assertThat(configuration.isDisruptsConnectivity(), is(true));
    }

    @SmallTest
    @Test
    public void isBuggy_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration().isBuggy(), is(false));
    }

    @SmallTest
    @Test
    public void isBuggy_true() {
        final PluginConfiguration configuration = new PluginConfiguration(false, false, false,
                true, false, false, new LinkedList<>());

        assertThat(configuration.isBuggy(), is(true));
    }

    @SmallTest
    @Test
    public void isDrainsBattery_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration().isDrainsBattery(),
                is(false));
    }

    @SmallTest
    @Test
    public void isDrainsBattery_true() {
        final PluginConfiguration configuration = new PluginConfiguration(false, false, false,
                false, true, false, new LinkedList<>());

        assertThat(configuration.isDrainsBattery(), is(true));
    }

    @SmallTest
    @Test
    public void isBlacklisted_false() {
        assertThat(PluginConfigurationFixture.newPluginConfiguration().isBlacklisted(), is(false));
    }

    @SmallTest
    @Test
    public void isBlacklisted_true() {
        final PluginConfiguration configuration = new PluginConfiguration(false, false, false,
                false, false, true, new LinkedList<>());

        assertThat(configuration.isBlacklisted(), is(true));
    }

    @SmallTest
    @Test
    public void parcelable() {
        final PluginConfiguration configuration = PluginConfigurationFixture
                .newPluginConfiguration();

        Parcel parcel = Parcel.obtain();

        try {
            configuration.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            final PluginConfiguration unparceled = PluginConfiguration.CREATOR
                    .createFromParcel(parcel);

            assertThat(unparceled, is(configuration));
        } finally {
            parcel.recycle();
        }
    }

    @SmallTest
    @Test
    public void equals_same() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertEquals(defaultConfiguration, defaultConfiguration);
    }

    @SmallTest
    @Test
    public void equals_equal() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertEquals(defaultConfiguration, PluginConfigurationFixture
                .newPluginConfiguration());
    }

    @SmallTest
    @Test
    public void equals_different_backwards_compatibility() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(true, false, false, false, false, false,
                        new LinkedList<>()));
    }

    @SmallTest
    @Test
    public void equals_different_requires_connectivity() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, true, false, false, false, false,
                        new LinkedList<>())
        );
    }

    @SmallTest
    @Test
    public void equals_different_disrupts_connectivity() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, false, true, false, false, false,
                        new LinkedList<>())
        );
    }

    @SmallTest
    @Test
    public void equals_different_buggy() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, false, false, true, false, false,
                        new LinkedList<>())
        );
    }

    @SmallTest
    @Test
    public void equals_different_drains_battery() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, false, false, false, true, false,
                        new LinkedList<>())
        );
    }

    @SmallTest
    @Test
    public void equals_different_blacklisted() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, false, false, false, false, true,
                        new LinkedList<>())
        );
    }

    @SmallTest
    @Test
    public void equals_different_alternatives() {
        final PluginConfiguration defaultConfiguration = PluginConfigurationFixture
                .newPluginConfiguration();

        final List<String> nonEmptyList = new LinkedList<>();
        nonEmptyList.add("foo");

        assertNotEquals(defaultConfiguration,
                new PluginConfiguration(false, false, false, false, false, false,
                        nonEmptyList)
        );
    }
}
