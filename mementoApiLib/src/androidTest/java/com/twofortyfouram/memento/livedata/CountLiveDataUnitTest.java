/*
 * android-memento
 * https://github.com/twofortyfouram/android-monorepo
 * Copyright (C) 2008–2019 two forty four a.m. LLC
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

package com.twofortyfouram.memento.livedata;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import com.twofortyfouram.memento.test.BaseColumnsCursorFixture;
import com.twofortyfouram.test.provider.MockableContentProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public final class CountLiveDataUnitTest {

    @Test
    @SmallTest
    public void query_non_null_count() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            final int expectedCount = 3;

            @NonNull final String authority = "foo"; //$NON-NLS

            @NonNull final MockableContentProvider provider = MockableContentProvider.newMockProvider(ApplicationProvider.getApplicationContext(), authority);
            @NonNull final Cursor cursor = BaseColumnsCursorFixture.newCountCursor(expectedCount);
            provider.addQueryResult(cursor);

            @NonNull final CountLiveData countLiveData = new CountLiveData(provider.getContext(), false, new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(authority).build(), null, null);

            @NonNull final AtomicInteger observerCount = new AtomicInteger(0);
            final Observer<Integer> observer = o -> {
                observerCount.incrementAndGet();
            };
            countLiveData.observeForever(observer);

            assertThat(observerCount.get(), is(1));
            assertThat(countLiveData.getValue(), is(expectedCount));

            countLiveData.removeObserver(observer);
        });
    }
}
