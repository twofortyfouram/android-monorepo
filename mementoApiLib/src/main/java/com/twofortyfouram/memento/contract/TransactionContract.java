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

package com.twofortyfouram.memento.contract;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.api.BuildConfig;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
public final class TransactionContract {

    /**
     * Method supported by the {@link ContentProvider#call(String, String, Bundle)}
     * interface for performing a series of operations inside a transaction.
     */
    @NonNull
    public static final String METHOD_RUN_IN_TRANSACTION
            = "com.twofortyfouram.memento.method.RUN_IN_TRANSACTION"; //$NON-NLS

    /**
     * Type: {@code Transactable}
     * <p>
     * Required argument.
     */
    @NonNull
    public static final String EXTRA_BUNDLE_PARCELABLE_TRANSACTABLE
            = "com.twofortyfouram.memento.extra.PARCELABLE_TRANSACTABLE"; //$NON-NLS

    /**
     * Type: {@code Bundle}
     * <p>
     * Required argument.
     */
    @NonNull
    public static final String EXTRA_BUNDLE_TRANSACTABLE_DATA
            = "com.twofortyfouram.memento.extra.BUNDLE_ARGS"; //$NON-NLS

    /**
     * @param context      Application context.
     * @param authority    Authority of the Content Provider.
     * @param transactable Transactable to execute.
     * @param data         data argument for Transactable.
     * @return The result of {@code transactable}.
     */
    @Nullable
    @Incubating
    public static Bundle runInTransaction(@NonNull final Context context,
                                          @NonNull final Uri authority,
                                          @NonNull final Transactable transactable,
                                          @NonNull final Bundle data) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(authority, "authority"); //$NON-NLS-1$
        assertNotNull(transactable, "transactable"); //$NON-NLS-1$
        assertNotNull(data, "data"); //$NON-NLS-1$

        @NonNull final Context ctx = ContextUtil.cleanContext(context);

        return ctx.getContentResolver().call(authority, METHOD_RUN_IN_TRANSACTION, null,
                newCallBundle(transactable, data));
    }

    @NonNull
    @VisibleForTesting
    /*package*/ static Bundle newCallBundle(
            @NonNull final Transactable transactable, @NonNull final Bundle dataBundle) {
        assertNotNull(transactable, "transactable"); //$NON-NLS
        assertNotNull(dataBundle, "dataBundle"); //$NON-NLS

        /*
         * We don't want to run this all the time for production, but hopefully in debug it'll catch errors of non-static
         * Transactable objects.
         */
        if (BuildConfig.DEBUG) {
            @NonNull final Class<?> transactableClass = transactable.getClass();

            if (transactableClass.isAnonymousClass()) {
                throw new AssertionError(Lumberjack.formatMessage("%s is not static", transactableClass.getName())); //$NON-NLS
            }

            if (transactableClass.isLocalClass()) {
                throw new AssertionError(Lumberjack.formatMessage("%s is not static", transactableClass.getName())); //$NON-NLS
            }

            try {
                // Not trying to be overly thorough; just catching the obvious mistake
                transactableClass.getField("CREATOR"); //$NON-NLS
            } catch (NoSuchFieldException e) {
                throw new AssertionError(Lumberjack.formatMessage("%s is missing CREATOR", transactableClass.getName())); //$NON-NLS
            }
        }

        @NonNull final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BUNDLE_PARCELABLE_TRANSACTABLE, transactable);
        bundle.putBundle(EXTRA_BUNDLE_TRANSACTABLE_DATA, dataBundle);

        return bundle;
    }

    private TransactionContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
