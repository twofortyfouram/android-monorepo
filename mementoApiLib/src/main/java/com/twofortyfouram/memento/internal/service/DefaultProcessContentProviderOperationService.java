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

package com.twofortyfouram.memento.internal.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Implementation of {@link AbstractContentProviderOperationService} that runs in the default
 * process.
 */
public final class DefaultProcessContentProviderOperationService
        extends AbstractContentProviderOperationService {

    @NonNull
    @VisibleForTesting
    public static final Intent newStartIntent(@NonNull final Context context,
                                              @NonNull final Bundle extras) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(extras, "extras"); //$NON-NLS

        return new Intent(context, DefaultProcessContentProviderOperationService.class)
                .putExtras(extras);
    }

}
