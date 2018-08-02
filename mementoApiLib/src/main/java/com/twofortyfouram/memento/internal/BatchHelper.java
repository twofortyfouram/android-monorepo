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

package com.twofortyfouram.memento.internal;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.*;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.contract.BatchContract;
import com.twofortyfouram.memento.internal.receiver.DefaultProcessContentProviderOperationReceiver;
import com.twofortyfouram.memento.internal.service.AbstractContentProviderOperationService;
import com.twofortyfouram.memento.internal.service.DefaultProcessContentProviderOperationService;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;

import static com.twofortyfouram.assertion.Assertions.*;

@ThreadSafe
public final class BatchHelper {
    /**
     * Type: {@code Uri}.
     * <p>
     * URI for the operations to write.
     */
    @NonNull
    @VisibleForTesting
    /*package*/ public static final String EXTRA_PARCELABLE_URI =
            AbstractContentProviderOperationService.class
                    .getName() + ".extra.PARCELABLE_URI"; //$NON-NLS-1$
    /**
     * Type: {@code <ArrayList<ArrayList<ContentProviderOperation>>}.
     * <p>
     * Set of Content Provider operations to apply.
     */
    @NonNull
    @VisibleForTesting
    /*package*/ public static final String EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS =
            AbstractContentProviderOperationService.class
                    .getName() + ".extra.SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS";

    @Slow(Slow.Speed.MILLISECONDS)
    @SuppressWarnings("unchecked")
    public static void handleIntent(@NonNull final Context context, @NonNull final Intent intent) {
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

    /**
     * Starts an Intent service to perform {@code ops} in the default application process.
     *
     * @param context   Application context.
     * @param authority Content authority of the provider that the operations should be applied to.
     * @param ops       The operations to apply.
     */
    @AnyThread
    public static void applyAsync(@NonNull final Context context, @NonNull final Uri authority,
                                  @NonNull @Size(min = 1) final ArrayList<ArrayList<ContentProviderOperation>> ops) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotEmpty(ops, "ops"); //$NON-NLS
        assertNoNullElements(ops, "ops"); //$NON-NLS

        @NonNull final Bundle extras = BatchHelper.newExtras(authority, ops);
        try {
            @Nullable final ComponentName componentName = context
                    .startService(DefaultProcessContentProviderOperationService.newStartIntent(context, extras));

            if (null == componentName) {
                Lumberjack.e("Failed to start service for component %s", componentName); //$NON-NLS

                context.sendBroadcast(DefaultProcessContentProviderOperationReceiver.newStartIntent(context, extras));
            }
        } catch (final IllegalStateException e) {
            // On Oreo, can fail to start the Service if the app is not in the foreground
            Lumberjack.i("App not in foreground; applying workaround", e); //$NON-NLS

            context.sendBroadcast(DefaultProcessContentProviderOperationReceiver.newStartIntent(context, extras));
        }
    }
}
