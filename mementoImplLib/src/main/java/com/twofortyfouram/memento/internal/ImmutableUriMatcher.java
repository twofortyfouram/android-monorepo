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

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import net.jcip.annotations.Immutable;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Wraps Android's {@link UriMatcher} in an effectively immutable container clas.
 */
@Immutable
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ImmutableUriMatcher {

    /**
     * {@link UriMatcher} wrapped by this class.
     */
    @NonNull
    private final UriMatcher mUriMatcher;

    /**
     * @param matcher UriMatcher to be wrapped by this class. This object cannot be modified after
     *                being passed to this constructor, as that would mutate the internal state of
     *                {@link ImmutableUriMatcher} making it no longer effectively immutable.
     */
    public ImmutableUriMatcher(@NonNull final UriMatcher matcher) {
        assertNotNull(matcher, "matcher"); //$NON-NLS-1$
        mUriMatcher = matcher;
    }

    /**
     * Try to match against the path in {@code uri}.
     *
     * @param uri The uri whose path we will match against.
     * @return The code for the matched node
     * or -1 if there is no matched node.
     */
    public int match(@NonNull final Uri uri) {
        assertNotNull(uri, "uri"); //$NON-NLS-1$
        return mUriMatcher.match(uri);
    }

}
