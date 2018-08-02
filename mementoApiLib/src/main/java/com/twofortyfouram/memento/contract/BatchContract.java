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
import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.annotation.MultiProcessSafe;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.memento.internal.BatchHelper;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;

import static com.twofortyfouram.assertion.Assertions.*;

@ThreadSafe
@Incubating
public final class BatchContract {

    /**
     * Method supported by the {@link ContentProvider#call(String, String, Bundle)}
     * interface for performing
     * a series of ContentProviderOperations.
     *
     * @see #EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS
     * @see #applyBatchWithAlternatives(Context,
     * Uri, ArrayList)
     */
    @NonNull
    public static final String METHOD_BATCH_OPERATIONS
            = "com.twofortyfouram.memento.method.BATCH"; //$NON-NLS

    /**
     * {@code ArrayList<ArrayList<ContentProviderOperation>>}.
     *
     * List of groups of operations to try.  The first group to succeed leads to the completion of
     * the transaction.
     */
    @NonNull
    public static final String EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS
            = "com.twofortyfouram.memento.extra.ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS"; //$NON-NLS

    /**
     * This can only be used for a ContentProvider that are implemented with the Memento library and
     * this can only be called from within the same package as the ContentProvider.
     *
     * Calls groups of operations in order until one group succeeds.  This occurs within a
     * transaction.  The benefit of this method is that it provides a bit more flexibility for
     * atomic transactions compared to {@link ContentProvider#applyBatch(ArrayList)}.  For example,
     * a client wishing to implement an "upsert" operation could do it with this method.
     *
     * @param operationGroups An ordered list of operation groups.  The outer
     *                        and inner lists must not contain null elements.
     */
    @Slow(Slow.Speed.MILLISECONDS)
    @MultiProcessSafe
    public static void applyBatchWithAlternatives(@NonNull final Context context,
            @NonNull final Uri authority,
            @Size(min = 1) @NonNull final ArrayList<ArrayList<ContentProviderOperation>> operationGroups) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotNull(operationGroups, "operationGroups"); //$NON-NLS
        assertNotEmpty(operationGroups, "operationGroups"); //$NON-NLS
        assertNoNullElements(operationGroups, "operationGroups"); //$NON-NLS

        @NonNull final Context ctx = ContextUtil.cleanContext(context);

        ctx.getContentResolver()
                .call(authority, METHOD_BATCH_OPERATIONS, null,
                        newCallBundle(operationGroups));
    }

    /**
     * This can only be used for a ContentProvider that are implemented with the Memento library and
     * this can only be called from within the same package as the ContentProvider.
     *
     * Calls groups of operations in order until one group succeeds.  This occurs within a
     * transaction.  The benefit of this method is that it provides a bit more flexibility for
     * atomic transactions compared to {@link ContentProvider#applyBatch(ArrayList)}.  For example,
     * a client wishing to implement an "upsert" operation could do it with this method.
     *
     * @param operationGroups An ordered list of operation groups.  The outer
     *                        and inner lists must not contain null elements.
     */
    @MultiProcessSafe
    public static void applyBatchWithAlternativesAsync(@NonNull final Context context,
                                                  @NonNull final Uri authority,
                                                  @Size(min = 1) @NonNull final ArrayList<ArrayList<ContentProviderOperation>> operationGroups) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(authority, "authority"); //$NON-NLS
        assertNotNull(operationGroups, "operationGroups"); //$NON-NLS
        assertNotEmpty(operationGroups, "operationGroups"); //$NON-NLS
        assertNoNullElements(operationGroups, "operationGroups"); //$NON-NLS

        BatchHelper.applyAsync(context, authority, operationGroups);
    }

    /**
     * @param operationGroups List of operation lists.
     * @return A new bundle appropriate for the {@link BatchContract#METHOD_BATCH_OPERATIONS} call.
     */
    @NonNull
    @MultiProcessSafe
    /*package*/ static final Bundle newCallBundle(
            @NonNull @Size(min = 1) final ArrayList<ArrayList<ContentProviderOperation>> operationGroups) {
        assertNotNull(operationGroups, "operationGroups"); //$NON-NLS
        assertNoNullElements(operationGroups, "operationGroups"); //$NON-NLS

        @NonNull final Bundle bundle = new Bundle();

        bundle.putSerializable(
                EXTRA_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS,
                operationGroups);

        return bundle;
    }

    private BatchContract() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
