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

package com.twofortyfouram.spackle;

import android.app.Application;
import android.content.*;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import com.twofortyfouram.log.Lumberjack;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;


/**
 * Content Provider that reads the process name from the ProviderInfo associated with the Content
 * Provider.  This solves the problem of reading the current process name reliably.
 *
 * An implementation for the default process already exists.  Clients with other processes should
 * add a subclass implementation for each additional process, assigning the content provider the
 * highest initialization order and setting the provider to not be multiprocess.
 */
@ThreadSafe
public class AbstractProcessNameContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Lumberjack.v("Creating ContentProvider %s at elapsedRealtimeMillis=%d", getClass().getName(), Clock.getInstance().getRealTimeMillis()); //$NON-NLS

        return true;
    }

    @Override
    public void attachInfo(final Context context, final ProviderInfo info) {
        super.attachInfo(context, info);

        // Although the default ContentProvider is disabled via a resource boolean, that doesn't prevent
        // implementors of the ContentProvider from not disabling it.
        @NonNull final String processName;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.P)) {
            processName = getProcessNamePPlus();
        }
        else {
            processName = getProcessNameLegacy(context, info);
        }
        ProcessUtil.setProcessName(processName);

        Lumberjack.init(context);
    }

    @NonNull
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    /*package*/ static String getProcessNameLegacy(@NonNull final Context context,
                                                   @NonNull final ProviderInfo info) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(info, "info"); //$NON-NLS

        @Nullable final String providerProcessName = info.processName;

        if (null == providerProcessName) {
            @Nullable final String appProcessName = context.getApplicationInfo().processName;
            if (null == appProcessName) {
                return context.getPackageName();
            } else {
                return appProcessName;
            }
        }

        return providerProcessName;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static final String getProcessNamePPlus() {
        return Application.getProcessName();
    }

    @Nullable
    @Override
    public final Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return null;
    }

    @Override
    public final Cursor query(final Uri uri, final String[] projection, final Bundle queryArgs,
            final CancellationSignal cancellationSignal) {
        return super.query(uri, projection, queryArgs, cancellationSignal);
    }

    @Nullable
    @Override
    public final Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder,
            @Nullable final CancellationSignal cancellationSignal) {
        return super
                .query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Nullable
    @Override
    public final String getType(@NonNull final Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public final Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        return null;
    }

    @Override
    public final int delete(@NonNull final Uri uri, @Nullable final String selection,
            @Nullable final String[] selectionArgs) {
        return 0;
    }

    @Override
    public final int update(@NonNull final Uri uri, @Nullable final ContentValues values,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs) {
        return 0;
    }

    @NonNull
    @Override
    public final ContentProviderResult[] applyBatch(
            @NonNull final ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        return super.applyBatch(operations);
    }
}
