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

package com.twofortyfouram.memento.util;

import androidx.annotation.Nullable;

import com.twofortyfouram.annotation.Incubating;

/**
 * Represents work to be performed in a transaction.
 *
 * Note: The current implementation does not support multiple processes or packages.  To improve
 * likelihood of being future-proof, subclasses should probably be static classes that implement
 * {@code Parcelable}.
 */
@Incubating
public interface Transactable<T> {

    /**
     * Implementations of this method should only be database operations
     * to minimize the amount of time the database is locked.  In other words, don't do network
     * or other slow operations inside this method.
     */
    @Nullable
    T runInTransaction();

}
