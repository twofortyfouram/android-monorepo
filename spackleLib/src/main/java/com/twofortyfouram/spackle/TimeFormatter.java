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


import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import net.jcip.annotations.ThreadSafe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.twofortyfouram.spackle.TimeFormatter.TimeFormat.ISO_8601;

/**
 * Provides the functionality for formatting time to given {@link TimeFormat} type.
 */
@ThreadSafe
public final class TimeFormatter {

    /**
     * Formats the given {@code wallTimeMillis}
     * to its string representation in {@code timeFormat}.
     *
     * @param timeFormat     String representation of time format.
     * @param wallTimeMillis Wall time in milliseconds.
     * @return String representation of {@code wallTimeMillis} formatted in {@code timeFormat}
     */
    @NonNull
    public static String formatTime(@TimeFormat final String timeFormat, final long wallTimeMillis) {
        return new SimpleDateFormat(timeFormat, Locale.US).format(new Date(wallTimeMillis));
    }

    /**
     * Supported time formats.
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef(ISO_8601)
    public @interface TimeFormat {

        /**
         * ISO 8601 format.  E.g. 2017-01-26 09:02:17.000-0800
         */
        String ISO_8601 = "yyyy-MM-dd HH:mm:ss.SSSZ"; //$NON-NLS-1$
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private TimeFormatter() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
