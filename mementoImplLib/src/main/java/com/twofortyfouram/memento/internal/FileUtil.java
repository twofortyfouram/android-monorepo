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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import net.jcip.annotations.Immutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

@Immutable
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class FileUtil {

    public static boolean copyFile(@NonNull final File sourceFile, @NonNull final File destFile,
            final boolean overwriteIfDestFileExists) throws IOException {
        assertNotNull(sourceFile, "sourceFile"); //$NON-NLS
        assertNotNull(destFile, "destFile"); //$NON-NLS

        if (sourceFile.equals(destFile)) {
            throw new IllegalArgumentException("sourceFile == destFile");
        }

        if (!sourceFile.exists()) {
            throw new FileNotFoundException("sourceFile does not exist");
        }

        @Nullable final File parentFile = destFile.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdirs();
        }

        if (!destFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            destFile.createNewFile();
        } else {
            if (!overwriteIfDestFileExists) {
                throw new IOException("Destination file already exists!");
            }
        }

        try (final FileChannel source = new FileInputStream(sourceFile).getChannel();
             final FileChannel destination = new FileOutputStream(destFile, false).getChannel()) {
            final long sourceFileSize = source.size();
            final long transferredBytes = destination.transferFrom(source, 0, sourceFileSize);

            return sourceFileSize == transferredBytes;
        }
    }

    public static boolean copyFile(@NonNull final File sourceFile, @NonNull final File destFile)
            throws IOException {
        return copyFile(sourceFile, destFile, false);
    }

    private FileUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

}
