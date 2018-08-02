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

package com.twofortyfouram.memento.internal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.internal.BatchHelper;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

public abstract class AbstractContentProviderOperationReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        if (BundleScrubber.scrub(intent)) {
            return;
        }

        Lumberjack.v("Received %s", intent); //$NON-NLS-1$

        @NonNull final PendingResult pendingResult = goAsync();

        new Thread(() -> {
            BatchHelper.handleIntent(context, intent);

            pendingResult.finish();
        }).start();
    }
}
