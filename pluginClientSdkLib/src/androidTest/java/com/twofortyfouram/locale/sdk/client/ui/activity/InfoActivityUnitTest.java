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

package com.twofortyfouram.locale.sdk.client.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.client.internal.HostPackageUtilTest.HostPackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasScheme;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class InfoActivityUnitTest {

    @SmallTest
    @Test
    public void testGetLaunchIntent_host() {
        final Intent i = InfoActivity.getLaunchIntent(getContext(), new ExtendedHostPackageManager(
                "com.twofortyfouram.locale"), getContext().getPackageName()); //$NON-NLS-1$

        assertThat(i, hasPackage("com.twofortyfouram.locale")); //$NON-NLS
    }

    @SmallTest
    @Test
    public void testGetLaunchIntent_google_play() {
        final Intent i = InfoActivity
                .getLaunchIntent(getContext(), new HostPackageManager(), getContext()
                        .getPackageName());

        assertThat(i, hasAction(Intent.ACTION_VIEW));
        assertThat(i, hasData(hasScheme("market"))); //$NON-NLS
    }

    private static final class ExtendedHostPackageManager extends HostPackageManager {

        public ExtendedHostPackageManager(@NonNull final String... packages) {
            super(packages);
        }

        @Override
        public Intent getLaunchIntentForPackage(String packageName) {
            return new Intent().setPackage(packageName);
        }
    }
}
