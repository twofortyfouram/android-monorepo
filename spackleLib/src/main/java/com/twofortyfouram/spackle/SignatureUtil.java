/*
 * android-spackle https://github.com/twofortyfouram/android-spackle
 * Copyright (C) 2009–2017 two forty four a.m. LLC
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

package com.twofortyfouram.spackle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;

import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.ThreadSafe;

import java.util.Arrays;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Utilities for application signatures.
 */
@ThreadSafe
public final class SignatureUtil {

    /**
     * @param context Application context.
     * @return The signatures of this app.
     */
    @NonNull
    public static String[] getApplicationSignatures(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        try {
            return getApplicationSignatures(context, context.getPackageName());
        } catch (final NameNotFoundException e) {
            /*
             * This should never occur, because the currently running
             * application's package must exist.
             */
            throw new AssertionError(e);
        }
    }

    /**
     * @param context     Application context.
     * @param packageName Name of the package whose signatures are to be
     *                    obtained.
     * @return The signatures of the app with package {@code packageName}.
     * @throws NameNotFoundException If {@code packageName} isn't available.
     */
    @NonNull
    public static String[] getApplicationSignatures(@NonNull final Context context,
            @NonNull final String packageName) throws NameNotFoundException {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotEmpty(packageName, "packageName"); //$NON-NLS-1$

        final PackageManager packageManager = context.getPackageManager();

        final PackageInfo info = packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

        final String[] signatures;
        if (null == info.signatures) {
            signatures = new String[0];
        } else {
            final int length = info.signatures.length;
            signatures = new String[length];
            for (int x = 0; x < length; x++) {
                signatures[x] = info.signatures[x].toCharsString();
            }
        }

        Lumberjack.v("Signatures are %s", Arrays.toString(signatures)); //$NON-NLS-1$

        return signatures;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be
     *                                       instantiated.
     */
    private SignatureUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
