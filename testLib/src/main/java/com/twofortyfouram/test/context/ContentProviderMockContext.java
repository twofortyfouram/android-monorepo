/*
 * android-test
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

package com.twofortyfouram.test.context;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.test.mock.MockContentResolver;

import net.jcip.annotations.NotThreadSafe;

import java.util.Map;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * This class itself is thread safe, however safety ultimately depends on the thread safety of
 * the underlying ContentProvider passed into the constructor.
 */
@NotThreadSafe
public final class ContentProviderMockContext extends ContextWrapper {

    @NonNull
    private final android.test.mock.MockContentResolver mResolver = new MockContentResolver();

    /**
     * @param baseContext          Base context for calls besides {@link #getContentResolver()}.
     * @param authorityProviderMap Mapping of authority string to content provider.
     */
    public ContentProviderMockContext(@NonNull final Context baseContext,
            @NonNull final Map<String, ContentProvider> authorityProviderMap) {
        super(baseContext);

        assertNotNull(authorityProviderMap, "authorityProviderMap"); //$NON-NLS

        for (final Map.Entry<String, ContentProvider> entry : authorityProviderMap.entrySet()) {
            mResolver.addProvider(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return The mock context; prevents breaking out.
     */
    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public ContentResolver getContentResolver() {
        return mResolver;
    }
}
