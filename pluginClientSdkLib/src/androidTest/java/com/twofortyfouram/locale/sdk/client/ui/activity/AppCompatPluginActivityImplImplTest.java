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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.locale.sdk.client.test.condition.ui.activity.AppCompatPluginActivityImpl;

import org.json.JSONObject;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Superclass for Activity unit tests that provides facilities to make testing
 * easier.
 */
@RunWith(AndroidJUnit4.class)
public final class AppCompatPluginActivityImplImplTest
        extends AbstractPluginActivityImplTest<AppCompatPluginActivityImpl> {

    @NonNull
    @Override
    protected Class<AppCompatPluginActivityImpl> getActivityClass() {
        return AppCompatPluginActivityImpl.class;
    }

    @Override
    protected void setActivityJsonAndBlurb(@Nullable final JSONObject json,
            @Nullable final String blurb) {
        final AppCompatPluginActivityImpl activity = mActivity.getActivity();
        activity.mJson = json;
        activity.mBlurb = blurb;
    }

    @Override
    protected void assertGetBlurbCount(final int expectedCount) {
        final AppCompatPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mGetBlurbCount.get());
    }

    @Override
    protected void assertGetResultBundleCount(final int expectedCount) {
        final AppCompatPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mGetResultJsonCount.get());
    }

    @Override
    protected void assertIsBundleValidCount(final int expectedCount) {
        final AppCompatPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mIsJsonValidCount.get());
    }

    @Override
    protected void assertOnPostCreateWithPreviousResultCount(final int expectedCount) {
        final AppCompatPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mOnPostCreateWithPreviousJsonCount.get());
    }
}
