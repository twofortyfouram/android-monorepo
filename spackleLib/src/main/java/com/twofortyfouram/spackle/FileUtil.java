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

import androidx.annotation.NonNull;
import com.twofortyfouram.annotation.NotMultiProcessSafe;
import com.twofortyfouram.annotation.Slow;

import java.io.File;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

public final class FileUtil {
    /**
     * Delete a file or directory. If {@code file} is a directory, this method
     * recursively deletes any items it contains before finally deleting
     * {@code file}.
     * <p>
     * This method is not atomic. If deletion of a directory fails (e.g.
     * {@code file} is a directory and this method returns false), then the
     * directory's contents may be partially deleted.
     * <p>
     * Note: this method is not thread-safe if two threads are attempting to
     * delete the same directory tree.
     *
     * @param file Directory or file to delete.
     * @return true if deletion succeeded. False if deletion failed.
     */
    @NotMultiProcessSafe
    @Slow(Slow.Speed.MILLISECONDS)
    public static boolean deleteRecursively(@NonNull final File file) {
        assertNotNull(file, "file"); //$NON-NLS-1$

        if (file.exists() && file.isDirectory()) {
            for (final String child : file.list()) {
                final boolean success = deleteRecursively(new File(file, child));
                if (!success) {
                    return false;
                }
            }
        }

        return file.delete();
    }
}
