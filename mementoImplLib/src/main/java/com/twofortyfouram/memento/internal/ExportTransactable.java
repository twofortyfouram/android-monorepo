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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.memento.contract.ExportContract;
import com.twofortyfouram.memento.util.Transactable;
import net.jcip.annotations.ThreadSafe;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@ThreadSafe
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ExportTransactable implements Transactable {

    @NonNull
    public static final Creator<ExportTransactable> CREATOR = new Creator<ExportTransactable>() {
        @Override
        public ExportTransactable createFromParcel(@NonNull final Parcel parcel) {
            return new ExportTransactable();
        }

        @Override
        public ExportTransactable[] newArray(final int i) {
            return new ExportTransactable[i];
        }
    };

    @NonNull
    private static final String EXTRA_BOOLEAN_RESULT =
            ExportTransactable.class.getName() + ".extra.BOOLEAN_RESULT"; //$NON-NLS-1$

    @NonNull
    private static final String EXTRA_STRING_DATABASE_PATH =
            ExportTransactable.class.getName() + ".extra.STRING_DATABASE_PATH"; //$NON-NLS-1$

    @NonNull
    private static final String EXTRA_STRING_FILE_DESTINATION =
            ExportTransactable.class.getName() + ".extra.STRING_FILE_DESTINATION"; //$NON-NLS-1$

    @NonNull
    public static Bundle newDataBundle(@NonNull final String databasePath,
                                       @NonNull final String destinationFilePath) {
        assertNotNull(databasePath, "databasePath"); //$NON-NLS-1$
        assertNotNull(destinationFilePath, "destinationFilePath"); //$NON-NLS-1$

        @NonNull final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_STRING_DATABASE_PATH, databasePath);
        bundle.putString(EXTRA_STRING_FILE_DESTINATION, destinationFilePath);

        return bundle;
    }

    @CheckResult
    public static boolean getResultFromBundle(@Nullable final Bundle bundle) {
        return null != bundle && bundle.getBoolean(EXTRA_BOOLEAN_RESULT);
    }

    @NonNull
    private static Bundle newResultBundle(final boolean result) {
        @NonNull final Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_BOOLEAN_RESULT, result);

        return bundle;
    }

    @Nullable
    @Override
    public Bundle runInTransaction(@NonNull final Context context, @NonNull final Bundle bundle) {
        assertNotNull(bundle, "bundle"); //$NON-NLS
        assertNotNull(context, "context"); //$NON-NLS

        BundleAssertions.assertHasString(bundle, EXTRA_STRING_DATABASE_PATH);
        BundleAssertions.assertHasString(bundle, EXTRA_STRING_FILE_DESTINATION);

        @NonNull final String originDatabaseFilePath = bundle.getString(EXTRA_STRING_DATABASE_PATH);
        @NonNull final String destinationFilePath = bundle.getString(EXTRA_STRING_FILE_DESTINATION);

        @NonNull final File originalDatabasePath = new File(originDatabaseFilePath);
        @NonNull final File originWalPath = new File((originDatabaseFilePath + ExportContract.WAL_SUFFIX)); //$NON-NLS
        @NonNull final File originJournalPath = new File(originDatabaseFilePath + ExportContract.JOURNAL_SUFFIX); //$NON-NLS

        @NonNull final List<File> files = new LinkedList<>();
        files.add(originalDatabasePath);
        if (originWalPath.exists()) {
            files.add(originWalPath);
        }
        if (originJournalPath.exists()) {
            files.add(originJournalPath);
        }

        boolean success;
        try {
            FileUtil.zipFiles(files, new File(destinationFilePath), false);
            success = true;
        } catch (final IOException e) {
            success = false;
            Lumberjack.e("Could not copy database file (source: %s, destinationFilePath: %s)", //NON-NLS
                    files, destinationFilePath);
            Lumberjack.e(e.getMessage());
        }

        return newResultBundle(success);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel parcel, final int i) {

    }
}
