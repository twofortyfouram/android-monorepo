/*
 * android-memento
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

package com.twofortyfouram.memento.internal;

import android.content.ContentProviderClient;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.test.ContentProviderImpl;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class ContentProviderClientCompatTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(ContentProviderClientCompat.class, notInstantiable());
    }

    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    public void close_nougat() {
        // Probably need to use something like Mockito to implement this test
    }

    @SmallTest
    @SdkSuppress(maxSdkVersion = Build.VERSION_CODES.M)
    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("deprecation")
    public void close_legacy() {

        // Cannot construct a ContentProviderClient object, so use a real one.

        // Don't convert to try with resources.
        @Nullable ContentProviderClient client = null;
        try {
            client = getContext().getContentResolver()
                    .acquireContentProviderClient(ContentProviderImpl
                            .getContentAuthority(getContext()));
        } finally {
            if (null != client) {
                ContentProviderClientCompat.close(client);

                client.release(); // throws
            }
        }
    }
}