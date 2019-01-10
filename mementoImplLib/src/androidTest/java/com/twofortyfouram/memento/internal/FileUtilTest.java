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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class FileUtilTest {

    @Test
    @SmallTest
    public void copyFile() throws IOException {
        File sourceFile = null;
        File destinationFile = null;

        try {
            final String testString = "Some text 123 ..."; //NON-NLS
            final File externalFilesDir = ApplicationProvider.getApplicationContext()
                    .getExternalFilesDir(null);
            sourceFile = new File(externalFilesDir, "source_file"); //NON-NLS
            destinationFile = new File(externalFilesDir, "output_file"); //NON-NLS

            assertTrue(sourceFile.createNewFile());

            //Write test line to the newly created file
            try (final FileWriter fw = new FileWriter(sourceFile, false);
                 final BufferedWriter fos = new BufferedWriter(fw)) {
                fos.append(testString);
            }

            assertTrue(FileUtil.copyFile(sourceFile, destinationFile, true));

            //Read destination file and check If written text corresponds to source file text
            try (final FileReader fr = new FileReader(destinationFile);
                 final BufferedReader fis = new BufferedReader(fr)) {
                assertThat(fis.readLine(), is(testString));
            }
        } finally {
            //Cleanup
            if (null != sourceFile && sourceFile.exists()) {
                assertTrue(sourceFile.delete());
            }
            if (null != destinationFile && destinationFile.exists()) {
                assertTrue(destinationFile.delete());
            }
        }
    }

    @Test
    @SmallTest
    public void copyFile_non_existent_source_file() throws IOException {
        final File externalFilesDir = ApplicationProvider.getApplicationContext()
                .getExternalFilesDir(null);

        final File sourceFile = new File(externalFilesDir, "non_existent_file"); //NON-NLS
        final File destinationFile = new File(externalFilesDir, "output_file"); //NON-NLS

        boolean fileNotFound = false;
        try {
            assertFalse(FileUtil.copyFile(sourceFile, destinationFile));
        } catch (final FileNotFoundException ex) {
            fileNotFound = true;
        }

        assertTrue(fileNotFound);
    }

    @Test
    @SmallTest
    public void copyFile_not_writable_destination() throws IOException {
        File sourceFile = null;
        File destinationFile = null;

        try {
            final File externalFilesDir = ApplicationProvider.getApplicationContext()
                    .getExternalFilesDir(null);

            final File systemNonWritableDir = new File("/system"); //NON-NLS

            sourceFile = new File(externalFilesDir, "input_file"); //NON-NLS
            destinationFile = new File(systemNonWritableDir,
                    "output_file"); //NON-NLS

            assertTrue(sourceFile.createNewFile());

            boolean ioException = false;
            try {
                assertFalse(FileUtil.copyFile(sourceFile, destinationFile));
            } catch (final IOException ex) {
                ioException = true;
            }

            assertTrue(ioException);
        } finally {
            //Cleanup
            if (null != sourceFile && sourceFile.exists()) {
                assertTrue(sourceFile.delete());
            }
            if (null != destinationFile && destinationFile.exists()) {
                assertTrue(destinationFile.delete());
            }
        }
    }

    @Test
    @SmallTest
    public void copyFile_overwrite_disabled_test() throws IOException {
        File sourceFile = null;
        File destinationFile = null;

        try {
            final File externalFilesDir = ApplicationProvider.getApplicationContext()
                    .getExternalFilesDir(null);

            sourceFile = new File(externalFilesDir, "source_file"); //NON-NLS
            destinationFile = new File(externalFilesDir, "output_file"); //NON-NLS

            assertTrue(sourceFile.createNewFile());
            assertTrue(destinationFile.createNewFile());

            boolean ioException = false;
            try {
                assertFalse(FileUtil.copyFile(sourceFile, destinationFile));
            } catch (final IOException ex) {
                ioException = true;
            }

            assertTrue(ioException);
        } finally {
            //Cleanup
            if (null != sourceFile && sourceFile.exists()) {
                assertTrue(sourceFile.delete());
            }
            if (null != destinationFile && destinationFile.exists()) {
                assertTrue(destinationFile.delete());
            }
        }
    }

    @Test
    @SmallTest
    public void copyFile_overwrite_enabled_test() throws IOException {
        File sourceFile = null;
        File destinationFile = null;

        try {
            final File externalFilesDir = ApplicationProvider.getApplicationContext()
                    .getExternalFilesDir(null);

            sourceFile = new File(externalFilesDir, "source_file"); //NON-NLS
            destinationFile = new File(externalFilesDir, "output_file"); //NON-NLS

            assertTrue(sourceFile.createNewFile());
            assertTrue(destinationFile.createNewFile());

            assertTrue(FileUtil.copyFile(sourceFile, destinationFile, true));
        } finally {
            //Cleanup
            if (null != sourceFile && sourceFile.exists()) {
                assertTrue(sourceFile.delete());
            }
            if (null != destinationFile && destinationFile.exists()) {
                assertTrue(destinationFile.delete());
            }
        }
    }


    @Test
    @SmallTest
    public void copyFile_source_equals_destination() throws IOException {
        final File externalFilesDir = ApplicationProvider.getApplicationContext()
                .getExternalFilesDir(null);

        final File sourceFile = new File(externalFilesDir, "source_file"); //NON-NLS
        final File destinationFile = new File(externalFilesDir, "source_file"); //NON-NLS

        boolean exception = false;
        try {
            assertFalse(FileUtil.copyFile(sourceFile, destinationFile));
        } catch (final IllegalArgumentException ex) {
            exception = true;
        }
        assertTrue(exception);
    }

    @Test
    @SmallTest
    public void nonInstantiable() {
        assertThat(FileUtil.class, notInstantiable());
    }
}