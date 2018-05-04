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

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.contract.BatchContract;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;

import static com.twofortyfouram.assertion.Assertions.assertNoNullElements;
import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

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

    /**
     * Type: {@code Uri}.
     * <p>
     * URI for the operations to write.
     */
    @NonNull
    @VisibleForTesting
    /*package*/ static final String EXTRA_PARCELABLE_URI =
            AbstractContentProviderOperationService.class
                    .getName() + ".extra.PARCELABLE_URI"; //$NON-NLS-1$

    /**
     * Type: {@code <ArrayList<ArrayList<ContentProviderOperation>>}.
     * <p>
     * Set of Content Provider operations to apply.
     */
    @NonNull
    @VisibleForTesting
    /*package*/ static final String EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS =
            AbstractContentProviderOperationService.class
                    .getName() + ".extra.SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS";
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
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (BundleScrubber.scrub(intent)) {
            return;
        }

        Lumberjack.v("Received %s", intent); //$NON-NLS-1$

        if (null != intent) {
            handleIntent(getApplicationContext(), intent);
        } else {
            Lumberjack.e("Intent was null"); //$NON-NLS
        }
    }

    @Slow(Slow.Speed.MILLISECONDS)
    @SuppressWarnings("unchecked")
    private static void handleIntent(@NonNull final Context context, @NonNull final Intent intent) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(intent, "intent"); //$NON-NLS

        BundleAssertions.assertHasKey(intent.getExtras(), EXTRA_PARCELABLE_URI);
        BundleAssertions.assertHasKey(intent.getExtras(),
                EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);

        // Note this implementation isn’t safe as a true public API, because these extras are not
        // checked to make sure they are correct.  An incorrect class would cause a
        // ClassCastException
        final Uri authority = intent.getParcelableExtra(EXTRA_PARCELABLE_URI);
        final ArrayList<ArrayList<ContentProviderOperation>> operations
                = (ArrayList<ArrayList<ContentProviderOperation>>) intent
                .getSerializableExtra(
                        EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);

        BatchContract
                .applyBatchWithAlternatives(context, authority,
                        operations);
    }

    /**
     * @param authority  Content authority of the provider that the operations should be applied
     *                   to.
     * @param operations The operations to apply.
     * @return A new bundle with the necessary extras to start the service.
     */
    @NonNull
    public static final Bundle newExtras(@NonNull final Uri authority,
            @NonNull final ArrayList<ArrayList<ContentProviderOperation>> operations) {
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotEmpty(operations, "operations"); //$NON-NLS
        assertNoNullElements(operations, "operations"); //$NON-NLS

        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_PARCELABLE_URI, authority);
        bundle.putSerializable(EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS,
                operations);

        return bundle;
    }
}
