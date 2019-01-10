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

import android.annotation.TargetApi;
import android.content.ContentProviderClient;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.twofortyfouram.spackle.AndroidSdkVersion;

import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * This is not part of the public API of the library.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@ThreadSafe
public final class ContentProviderClientCompat {

    /**
     * Calls {@link ContentProviderClient#close()} or {@link ContentProviderClient#release()} as
     * appropriate.
     *
     * @param client To be closed.
     */
    public static void close(@NonNull final ContentProviderClient client) {
        assertNotNull(client, "client"); //$NON-NLS

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.N)) {
            closeNougat(client);
        } else {
            releaseLegacy(client);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void closeNougat(@NonNull final ContentProviderClient client) {
        client.close();
    }


    @SuppressWarnings("deprecation")
    private static void releaseLegacy(@NonNull final ContentProviderClient client) {
        client.release();
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private ContentProviderClientCompat() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
