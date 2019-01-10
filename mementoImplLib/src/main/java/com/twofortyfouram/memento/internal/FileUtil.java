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
import com.twofortyfouram.annotation.NotMultiProcessSafe;
import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.log.Lumberjack;
import net.jcip.annotations.Immutable;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * Note: this implementation may create a temporary file in the same directory as {@code destFile}.
     *
     * @param sourceFiles
     * @param destFile
     * @param requireAllInputFiles True if all of {@code sourceFiles} are required.  If false and one of the source files
     *                             doesn't exist, then this method will continue with the remaining files.
     * @throws IOException If a failure occurs.
     */
    @NotMultiProcessSafe
    @Slow(Slow.Speed.MILLISECONDS)
    public static void zipFiles(@NonNull final List<File> sourceFiles, @NonNull final File destFile, final boolean requireAllInputFiles)
            throws IOException {
        assertNotNull(sourceFiles, "sourceFile"); //$NON-NLS
        assertNotNull(destFile, "destFile"); //$NON-NLS

        for (@NonNull final File sourceFile: sourceFiles){
            if (sourceFile.equals(destFile)) {
                throw new IllegalArgumentException("sourceFile == destFile");
            }
        }

        // Note if parent is null, then the default temp directory may not be on the same filesystem and therefore the
        // rename at the end may not be atomic
        @Nullable final String parent = destFile.getParent();
        @NonNull final File tempDest = File.createTempFile(destFile.getName(), ".tmp", null == parent ? null : new File(parent));
        try {
            try (@NonNull final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(
                    new FileOutputStream(tempDest, false)))) {

                final byte[] buffer = new byte[1024];
                for (@NonNull final File sourceFile : sourceFiles) {
                    try (@NonNull final InputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile))) {
                        zipOutputStream.putNextEntry(new ZipEntry(sourceFile.getName()));

                        int length;
                        while (0 <= (length = inputStream.read(buffer))) {
                            zipOutputStream.write(buffer, 0, length);
                        }

                        zipOutputStream.closeEntry();
                    } catch (final FileNotFoundException e) {
                        Lumberjack.w("One of the input files no longer exists %s", e); //$NON-NLS
                        if (requireAllInputFiles) {
                            throw e;
                        }
                    }
                }

                zipOutputStream.finish();
                zipOutputStream.flush();
            }

            tempDest.renameTo(destFile);
        }
        finally {
            // Don't leave temp files dangling if it fails
            // Note: Could still be left behind if the process dies.
            tempDest.delete();
        }
    }

    private FileUtil() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
