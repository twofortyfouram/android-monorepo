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

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.internal.BatchHelper;
import com.twofortyfouram.spackle.bundle.BundleScrubber;
import net.jcip.annotations.ThreadSafe;

/**
 * Perform a series of {@link ContentProviderOperation}s in a background thread.
 * The primary use case is to simplify improving UI performance. For example,
 * when the user hits the back button in an Activity that wants to save some
 * data. Rather than blocking the Activity (which is necessary to ensure the
 * process has enough priority to complete the save to disk), this service
 * provides an alternative way to raise the process's priority without blocking
 * the main thread for the disk write.
 * <p>
 * Clients must subclass and implement a concrete implementation.  This allows clients
 * have control over the process that the service runs in.
 * </p>
 * <p>This class is not intended to be a public interface for other processes, but rather an
 * internal interface within an application.  As public interface, clients could crash the
 * service by putting incorrect types into the extras.</p>
 */
@ThreadSafe
public abstract class AbstractContentProviderOperationService extends IntentService {

    //$NON-NLS-1$

    /**
     * Construct a new ContentProviderService.
     */
    public AbstractContentProviderOperationService() {
        super(AbstractContentProviderOperationService.class.getName());

        /*
         * Redelivery is not desired, as that could cause duplicate
         * transactions.
         */
        setIntentRedelivery(false);
    }

    @Override
    protected final void onHandleIntent(@Nullable final Intent intent) {
        if (BundleScrubber.scrub(intent)) {
            return;
        }

        Lumberjack.v("Received %s", intent); //$NON-NLS-1$

        if (null != intent) {
            BatchHelper.handleIntent(getApplicationContext(), intent);
        } else {
            Lumberjack.e("Intent was null"); //$NON-NLS
        }
    }

}
