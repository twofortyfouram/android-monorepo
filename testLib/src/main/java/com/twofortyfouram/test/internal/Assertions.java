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

package com.twofortyfouram.test.internal;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

/**
 * Runtime assertions.
 */
@ThreadSafe
public final class Assertions {

    /**
     * @param object Object to check for being {@code null}.
     * @param name   Name of the object for human-readable exceptions.
     * @return {@code object} sanitized as being non-null.
     * @throws AssertionError If {@code object} is {@code null}.
     */
    @NonNull
    public static <T> T assertNotNull(@Nullable final T object, @NonNull final String name) {
        if (null == object) {
            throw new AssertionError(
                    formatMessage("%s cannot be null", name)); //$NON-NLS-1$
        }

        return object;
    }

    /**
     * Helper for formatting messages.
     *
     * @param msg  The format string.
     * @param args The format arguments.
     * @return A string formatted with the arguments
     */
    @NonNull
    private static String formatMessage(@NonNull final String msg, @NonNull final Object... args) {
        return String.format(Locale.US, msg, args);
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private Assertions() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
