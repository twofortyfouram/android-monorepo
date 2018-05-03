/*
 * android-test https://github.com/twofortyfouram/android-test
 * Copyright (C) 2014–2017 two forty four a.m. LLC
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

package com.twofortyfouram.test.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.test.ActivityImpl;
import com.twofortyfouram.test.espresso.UiTestPrerequesites;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(AndroidJUnit4.class)
public final class ActivityTestHelperTest extends UiTestPrerequesites {

    @Rule
    @NonNull
    public final ActivityTestRule<ActivityImpl> mActivityRule = new ActivityTestRule<>(
            ActivityImpl.class, false, true);

    @SmallTest
    @Test
    public void resultCanceled() {
        final ActivityImpl activity = mActivityRule.getActivity();

        activity.setResult(Activity.RESULT_CANCELED);

        activity.finish();

        assertThat(ActivityTestUtil.getActivityResultCodeSync(getInstrumentation(), activity),
                is(Activity.RESULT_CANCELED));
    }

    @SmallTest
    @Test
    public void resultOk() {
        final ActivityImpl activity = mActivityRule.getActivity();

        activity.setResult(Activity.RESULT_OK);

        activity.finish();

        assertThat(ActivityTestUtil.getActivityResultCodeSync(getInstrumentation(), activity),
                is(Activity.RESULT_OK));
    }

    @SmallTest
    @Test
    public void resultIntent_null() {
        final ActivityImpl activity = mActivityRule.getActivity();

        activity.setResult(Activity.RESULT_OK, null);

        activity.finish();

        assertThat(ActivityTestUtil.getActivityResultDataSync(getInstrumentation(), activity),
                nullValue());
    }

    @SmallTest
    @Test
    public void resultIntent_non_null() {
        final ActivityImpl activity = mActivityRule.getActivity();

        final Intent result = new Intent();
        activity.setResult(Activity.RESULT_OK, result);

        activity.finish();

        assertThat(ActivityTestUtil.getActivityResultDataSync(getInstrumentation(),
                activity), sameInstance(result));
    }

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(ActivityTestUtil.class, notInstantiable());
    }
}
