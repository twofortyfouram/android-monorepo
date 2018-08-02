/*
 * android-plugin-client-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.client.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.locale.sdk.client.test.condition.ui.activity.FragmentPluginActivityImpl;

import org.json.JSONObject;

import static junit.framework.Assert.assertEquals;

/**
 * Superclass for Activity unit tests that provides facilities to make testing
 * easier.
 */
public final class FragmentPluginActivityImplImplTest
        extends AbstractPluginActivityImplTest<FragmentPluginActivityImpl> {

    @NonNull
    @Override
    protected Class<FragmentPluginActivityImpl> getActivityClass() {
        return FragmentPluginActivityImpl.class;
    }

    @Override
    protected void setActivityJsonAndBlurb(@Nullable final JSONObject json,
            @Nullable final String blurb) {
        final FragmentPluginActivityImpl activity = mActivity.getActivity();
        activity.mJson = json;
        activity.mBlurb = blurb;
    }

    @Override
    protected void assertGetBlurbCount(final int expectedCount) {
        final FragmentPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mGetBlurbCount.get());
    }

    @Override
    protected void assertGetResultBundleCount(final int expectedCount) {
        final FragmentPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mGetResultJsonCount.get());
    }

    @Override
    protected void assertIsBundleValidCount(final int expectedCount) {
        final FragmentPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mIsJsonValidCount.get());
    }

    @Override
    protected void assertOnPostCreateWithPreviousResultCount(final int expectedCount) {
        final FragmentPluginActivityImpl activity = mActivity.getActivity();

        assertEquals(expectedCount, activity.mOnPostCreateWithPreviousJsonCount.get());
    }
}
