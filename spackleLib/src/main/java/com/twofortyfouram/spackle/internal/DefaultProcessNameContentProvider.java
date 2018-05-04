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

import android.support.annotation.Keep;
import android.support.annotation.RestrictTo;

import com.twofortyfouram.spackle.AbstractProcessNameContentProvider;

import net.jcip.annotations.ThreadSafe;


/*
 * Keep the class name to prevent a race condition in Android's Content Resolver when the
 * class name changes during an update to the app.  This crash is difficult to reproduce locally
 * but Crashlytics indicates it can happen fairly frequently during updates.  The crash is that
 * the content provider cannot be found, because Android still looks for the old class name.
 */
@Keep
@ThreadSafe
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class DefaultProcessNameContentProvider extends
        AbstractProcessNameContentProvider {

}
