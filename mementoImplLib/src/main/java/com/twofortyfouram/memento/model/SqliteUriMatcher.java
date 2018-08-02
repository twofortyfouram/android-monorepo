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

package com.twofortyfouram.memento.model;

import android.net.Uri;
import androidx.annotation.NonNull;

import net.jcip.annotations.ThreadSafe;

/**
 * Provides app-specific URI matching.
 */
@ThreadSafe
public interface SqliteUriMatcher {

    /**
     * @param uri Uri to match
     * @return A match for {@code uri}.
     * @throws IllegalArgumentException If {@code uri} is not recognized.
     */
    @NonNull
    SqliteUriMatch match(@NonNull final Uri uri) throws IllegalArgumentException;
}
