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

package com.twofortyfouram.spackle.bundle;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public final class BundleScrubberTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(BundleScrubber.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void scrub_intent_null() {
        assertFalse(BundleScrubber.scrub((Intent) null));
    }

    @SmallTest
    @Test
    public void scrub_intent_empty() {
        final Intent intent = new Intent();
        assertThat(intent.getExtras(), nullValue());

        assertThat(BundleScrubber.scrub(intent), is(false));

        assertThat(intent.getExtras(), nullValue());
    }

    @SmallTest
    @Test
    public void scrub_intent_non_empty() {
        final Intent intent = new Intent()
                .putExtra("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(BundleScrubber.scrub(intent), is(false));

        BundleAssertions.assertKeyCount(intent.getExtras(), 1);
        BundleAssertions.assertHasString(intent.getExtras(), "test_key",
                "test_value"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test
    public void scrub_bundle_null() {
        assertFalse(BundleScrubber.scrub((Bundle) null));
    }

    @SmallTest
    @Test
    public void scrub_bundle_empty() {
        final Bundle bundle = new Bundle();

        assertThat(BundleScrubber.scrub(bundle), is(false));

        BundleAssertions.assertKeyCount(bundle, 0);
    }

    @SmallTest
    @Test
    public void scrub_bundle_non_empty() {
        final Bundle bundle = new Bundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(BundleScrubber.scrub(bundle), is(false));

        BundleAssertions.assertKeyCount(bundle, 1);
        BundleAssertions
                .assertHasString(bundle, "test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    public void scrub_bundle_persistable_null() {
        assertFalse(BundleScrubber.scrub((PersistableBundle) null));
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    public void scrub_bundle_persistable_empty() {
        final PersistableBundle bundle = new PersistableBundle();

        assertThat(BundleScrubber.scrub(bundle), is(false));

        BundleAssertions.assertKeyCount(bundle, 0);
    }

    @SmallTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    public void scrub_bundle_persistable_non_empty() {
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putString("test_key", "test_value"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(BundleScrubber.scrub(bundle), is(false));

        BundleAssertions.assertKeyCount(bundle, 1);
    }
}
