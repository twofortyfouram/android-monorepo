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

package com.twofortyfouram.memento.service;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.log.Lumberjack;

import java.util.ArrayList;

import static com.twofortyfouram.assertion.Assertions.assertNoNullElements;
import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Implementation of {@link AbstractContentProviderOperationService} that runs in the default
 * process.
 */
public final class DefaultProcessContentProviderOperationService
        extends AbstractContentProviderOperationService {

    /**
     * Starts an Intent service to perform {@code ops} in the default application process.
     *
     * @param context   Application context.
     * @param authority Content authority of the provider that the operations should be applied to.
     * @param ops       The operations to apply.
     */
    @AnyThread
    public static void startService(@NonNull final Context context, @NonNull final Uri authority,
            @NonNull @Size(min = 1) final ArrayList<ArrayList<ContentProviderOperation>> ops) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotEmpty(ops, "ops"); //$NON-NLS
        assertNoNullElements(ops, "ops"); //$NON-NLS

        final ComponentName componentName = context
                .startService(newStartIntent(context, newExtras(authority, ops)));

        if (null == componentName) {
            Lumberjack.e("Failed to start service for component %s", componentName); //$NON-NLS
        }
    }

    @NonNull
    @VisibleForTesting
    /*package*/ static final Intent newStartIntent(@NonNull final Context context,
            @NonNull final Bundle extras) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(extras, "extras"); //$NON-NLS

        return new Intent(context, DefaultProcessContentProviderOperationService.class)
                .putExtras(extras);
    }

}
