/*
 * android-test https://github.com/twofortyfouram/android-test
 * Copyright (C) 2014–2017 two forty four a.m. LLC
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

package com.twofortyfouram.test;

import javax.annotation.concurrent.Immutable;

// This class is supposed to generate a warning.
@SuppressWarnings("NonFinalFieldInImmutable")
@Immutable
public final class AccidentallyMutableClass {

    @SuppressWarnings("FieldMayBeFinal")
    private Object mObject = new Object();
}
