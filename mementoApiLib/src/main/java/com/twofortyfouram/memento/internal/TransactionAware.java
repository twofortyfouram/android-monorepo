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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.memento.util.Transactable;

/**
 * <p>
 * Interface marking a ContentProvider subclass as supporting transactions.
 * </p>
 * <p>Note the current implementation doesn't work across app or process boundaries.</p>
 */
/*
 * This class exists in the API module, so that we can cast the ContentProvider object.
 */
@Incubating
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface TransactionAware {

    @Nullable
    <T> T runInTransaction(@NonNull Transactable<T> transactable);

}
