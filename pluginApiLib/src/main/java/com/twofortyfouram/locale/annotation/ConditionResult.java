/*
 * android-plugin-api-for-locale
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

package com.twofortyfouram.locale.annotation;

import android.support.annotation.IntDef;

import com.twofortyfouram.locale.api.LocalePluginIntent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({LocalePluginIntent.RESULT_CONDITION_SATISFIED, LocalePluginIntent.RESULT_CONDITION_UNKNOWN, LocalePluginIntent
        .RESULT_CONDITION_UNSATISFIED})
@Retention(RetentionPolicy.SOURCE)
public @interface ConditionResult {

}
