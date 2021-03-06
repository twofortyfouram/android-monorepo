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

package com.twofortyfouram.spackle;


import android.text.format.DateUtils;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import net.jcip.annotations.ThreadSafe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertInRangeInclusive;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.spackle.TimeFormatter.TimeFormat.ISO_8601;

/**
 * Provides the functionality for formatting time to given {@link TimeFormat} type.
 */
@ThreadSafe
public final class TimeFormatter {

    /**
     * Formats the wall time from {@code clock}
     * to its string representation in {@code timeFormat}.
     *
     * This method is preferred to {@link #formatTime(String, long)} because it makes it less likely to accidentally pass
     * in the wrong time in milliseconds.
     *
     * @param timeFormat String representation of time format.
     * @param clock      To determine wall time.
     * @return String representation of {@code wallTimeMillis} formatted in {@code timeFormat}
     */
    @NonNull
    public static String formatTime(@TimeFormat final String timeFormat,
                                    @NonNull final Clock clock) {
        assertNotNull(clock, "clock"); //$NON-NLS

        return formatTime(timeFormat, clock.getWallTimeMillis());
    }

    /**
     * Formats the given {@code wallTimeMillis}
     * to its string representation in {@code timeFormat}.
     *
     * @param timeFormat     String representation of time format.
     * @param wallTimeMillis Wall time in milliseconds.
     * @return String representation of {@code wallTimeMillis} formatted in {@code timeFormat}
     */
    @NonNull
    public static String formatTime(@TimeFormat final String timeFormat,
                                    final long wallTimeMillis) {
        return new SimpleDateFormat(timeFormat, Locale.US).format(new Date(wallTimeMillis));
    }

    /**
     * @param elapsedMilliseconds Elapsed duration in milliseconds.
     * @return The duration formatted in a more human readable format: H:MM:SS.millis
     */
    @NonNull
    public static String formatMilliseconds(final long elapsedMilliseconds) {
        assertInRangeInclusive(elapsedMilliseconds, 0, Long.MAX_VALUE,
                "elapsedMilliseconds"); //$NON-NLS-1$

        final Formatter formatter = new Formatter(
                Locale.US); // Lock to US, since this is for debugging only.

        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long millis = 0;
        long leftoverMilliseconds = elapsedMilliseconds;
        if (elapsedMilliseconds >= DateUtils.HOUR_IN_MILLIS) {
            hours = elapsedMilliseconds / DateUtils.HOUR_IN_MILLIS;
            leftoverMilliseconds -= hours * DateUtils.HOUR_IN_MILLIS;
        }
        if (elapsedMilliseconds >= DateUtils.MINUTE_IN_MILLIS) {
            minutes = leftoverMilliseconds / DateUtils.MINUTE_IN_MILLIS;
            leftoverMilliseconds -= minutes * DateUtils.MINUTE_IN_MILLIS;
        }
        if (elapsedMilliseconds >= DateUtils.SECOND_IN_MILLIS) {
            seconds = leftoverMilliseconds / DateUtils.SECOND_IN_MILLIS;
            leftoverMilliseconds -= seconds * DateUtils.SECOND_IN_MILLIS;
        }
        millis = leftoverMilliseconds;

        return formatter
                .format(Locale.US, "%1$d:%2$02d:%3$02d.%4$d", hours, minutes, seconds,
                        millis) //$NON-NLS-1$
                .toString();
    }

    /**
     * Parse the {@code source} in given {@code timeFormat} to its {@link Date} representation.
     *
     * @param timeFormat String representation of time format.
     * @param source     A {@code String} whose beginning should be parsed.
     * @return A {@code Date} parsed from the string.
     * @throws IllegalArgumentException if the given {@code source} cannot be parsed into {@code
     *                                  timeFormat}.
     */
    @NonNull
    public static Date parseTime(@TimeFormat final String timeFormat,
                                 @NonNull final String source) {
        assertNotNull(source, "source");  //$NON-NLS-1$

        try {
            return new SimpleDateFormat(timeFormat, Locale.US).parse(source);
        } catch (final ParseException parseException) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "%s is not in desired %s format",  //$NON-NLS-1$
                            source, timeFormat));
        }
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
