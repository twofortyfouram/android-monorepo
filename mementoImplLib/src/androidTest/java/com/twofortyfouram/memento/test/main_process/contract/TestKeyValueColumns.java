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

package com.twofortyfouram.memento.test.main_process.contract;

import android.provider.BaseColumns;

import androidx.annotation.NonNull;

public interface TestKeyValueColumns extends BaseColumns {

    /**
     * Type: {@code String}.
     * <p>
     * Key for the value.
     * <p>
     * Constraints: This column cannot be null or the empty string ''.
     */
    @NonNull
    String COLUMN_STRING_KEY = "key"; //$NON-NLS

    /**
     * Type: {@code String}.
     * <p>
     * String value.
     * <p>
     * Constraints: This column cannot be null.
     */
    @NonNull
    String COLUMN_STRING_VALUE = "value"; //$NON-NLS-1$

}
