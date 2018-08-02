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

package com.twofortyfouram.locale.sdk.client.test.condition.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.assertion.Assertions;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractFragmentPluginActivity;

import net.jcip.annotations.NotThreadSafe;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * A concrete implementation of {@link AbstractFragmentPluginActivity}
 * in order to test the abstract class.
 */
@NotThreadSafe
public final class FragmentPluginActivityImpl extends AbstractFragmentPluginActivity {

    @NonNull
    public final AtomicInteger mIsJsonValidCount = new AtomicInteger(0);

    @NonNull
    public final AtomicInteger mOnPostCreateWithPreviousJsonCount = new AtomicInteger(0);

    @NonNull
    public final AtomicInteger mGetBlurbCount = new AtomicInteger(0);

    @NonNull
    public final AtomicInteger mGetResultJsonCount = new AtomicInteger(0);

    @Nullable
    public volatile String mBlurb = null;

    @Nullable
    public volatile JSONObject mJson = null;

    @Override
    public boolean isJsonValid(@NonNull final JSONObject jsonObject) {
        Assertions.assertIsMainThread();

        mIsJsonValidCount.incrementAndGet();
        return PluginJsonValues.isJsonValid(jsonObject);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull final JSONObject previousJson,
            @NonNull final String previousBlurb) {
        Assertions.assertIsMainThread();

        mOnPostCreateWithPreviousJsonCount.incrementAndGet();

        assertNotNull(previousJson, "previousJson"); //$NON-NLS-1$
    }

    @Override
    public JSONObject getResultJson() {
        Assertions.assertIsMainThread();

        mGetResultJsonCount.incrementAndGet();

        return mJson;
    }

    @Override
    @NonNull
    public String getResultBlurb(@NonNull final JSONObject jsonObject) {
        Assertions.assertIsMainThread();

        mGetBlurbCount.incrementAndGet();

        assertNotNull(jsonObject, "jsonObject"); //$NON-NLS-1$

        return mBlurb;
    }

}
