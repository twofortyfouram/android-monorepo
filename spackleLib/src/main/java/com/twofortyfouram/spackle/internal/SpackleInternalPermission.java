/*
 * android-spackle
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

package com.twofortyfouram.spackle.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SpackleInternalPermission {

    /**
     * @param context Application context.
     * @return The default internal permission for the UI.
     */
    @NonNull
    public static String getSpackleInternalPermission(@NonNull final Context context) {
        return context.getPackageName() + ".com.twofortyfouram.spackle.permission.INTERNAL"; //$NON-NLS
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be
     *                                       instantiated.
     */
    private SpackleInternalPermission() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
