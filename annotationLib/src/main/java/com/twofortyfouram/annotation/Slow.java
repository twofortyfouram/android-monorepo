/*
 * android-annotation-lib https://github.com/twofortyfouram/android-annotation
 * Copyright 2014 two forty four a.m. LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twofortyfouram.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Documents code that may be slow and therefore shouldn't be called from the UI
 * thread or other performance critical sections of the application.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface Slow {

    /**
     * Documents how slow the operation is likely to be.
     */
    public static enum Speed {
        /**
         * The operation may take up to a few hundred milliseconds. The operation
         * shouldn't occur on the UI thread, however this operation is also
         * unlikely to take long enough to cause an Android Not Responding
         * dialog. For example, the operation may be a quick disk read or write.
         */
        MILLISECONDS,

        /**
         * The operation may take up to a few seconds. The operation shouldn't occur
         * on the UI thread, as it is likely to take long enough to cause an
         * Android Not Responding dialog. For example, the operation may be a
         * small network request.
         */
        SECONDS,

        /**
         * The operation may take up to a few minutes. The operation must not
         * occur on the UI thread, as it is very likely to take long enough to
         * cause an Android Not Responding dialog. For example, the operation
         * may be a large disk operation such as a database migration or a large
         * network request such as a photo upload.
         */
        MINUTES,
    }

    Speed value();
}
