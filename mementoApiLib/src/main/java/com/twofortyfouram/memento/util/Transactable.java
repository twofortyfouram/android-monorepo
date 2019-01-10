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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.annotation.Incubating;

/**
 * Represents work to be performed in a transaction.
 * <p>
 * Transactable objects must be defined as static and must implement {@link Parcelable} (e.g. add
 * {@code CREATOR}).
 */
@Incubating
public interface Transactable extends Parcelable {

    /**
     * Implementations of this method should only be database operations
     * to minimize the amount of time the database is locked.  In other words, don't do network
     * or other slow operations inside this method.
     *
     * @context Application context.
     * @param bundle Bundle of arguments specific to this Transactable object.  These will be
     *               been passed along with the Transactable object itself to the ContentProvider and then provided to
     *               {@link Transactable#runInTransaction(Context, Bundle)}.
     * @return An optional Bundle result.
     */
    @Nullable
    Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle);

}
