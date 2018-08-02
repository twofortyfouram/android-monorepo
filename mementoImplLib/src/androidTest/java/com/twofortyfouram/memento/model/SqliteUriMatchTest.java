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

package com.twofortyfouram.memento.model;


import android.content.ContentResolver;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.model.Operation;
import com.twofortyfouram.memento.model.SqliteUriMatch;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class SqliteUriMatchTest {

    /**
     * @return A test content URI.
     */
    @NonNull
    private static Uri getTestUri() {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority("com.twofortyfouram.memento.test.authority").build(); //$NON-NLS-1$
    }

    /**
     * @return A test content URI.
     */
    @NonNull
    private static Collection<Uri> getTestUris() {
        final Collection<Uri> uris = new ArrayList<>(1);
        uris.add(getTestUri());
        return uris;
    }

    @SmallTest
    @Test
    public void getMimeType() {
        final Uri testUri = getTestUri();
        final SqliteUriMatch match = new SqliteUriMatch(testUri, getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(match.getMimeType(), is("test_mime")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void getTableName() {
        final Uri testUri = getTestUri();
        final SqliteUriMatch match = new SqliteUriMatch(testUri, getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(match.getTableName(), is("test_table")); //$NON-NLS-1$
    }

    @SmallTest
    @Test
    public void getUri() {
        final Uri testUri = getTestUri();
        final SqliteUriMatch match = new SqliteUriMatch(testUri, getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(match.getBaseUri(), is(testUri));
    }

    @SmallTest
    @Test
    public void isIdUri_false() {
        final Uri testUri = getTestUri();
        final SqliteUriMatch match = new SqliteUriMatch(testUri, getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                false); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(match.isIdUri());
    }

    @SmallTest
    @Test
    public void isIdUri_true() {
        final Uri testUri = getTestUri();
        final SqliteUriMatch match = new SqliteUriMatch(testUri, getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(match.isIdUri());
    }

    @SmallTest
    @Test
    public void getUris() {
        final Collection<Uri> uris = getTestUris();

        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), uris,
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(match.getNotifyUris(), hasSize(1));
        assertThat(match.getNotifyUris(), contains(getTestUri()));
    }

    @SmallTest
    @Test
    public void getUris_copy_in_constructor() {
        final Collection<Uri> uris = getTestUris();

        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), uris,
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        // Ensure object cannot be mutated
        uris.clear();
        assertThat(match.getNotifyUris(), not(empty()));
    }

    @SmallTest
    @Test(expected = UnsupportedOperationException.class)
    public void getUris_immutable_return_value() {
        final Collection<Uri> uris = getTestUris();

        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), uris,
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        // Ensure return value cannot mutate object internals
        match.getNotifyUris().clear();
    }

    @Test
    @SmallTest
    public void isOperationAllowed_true() {
        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), getTestUris(),
                EnumSet.allOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        for (final Operation operation : Operation.values()) {
            assertThat(match.isOperationAllowed(operation), is(true));
        }
    }

    @Test
    @SmallTest
    public void isOperationAllowed_false() {
        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), getTestUris(),
                EnumSet.noneOf(Operation.class), "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        for (final Operation operation : Operation.values()) {
            assertThat(match.isOperationAllowed(operation), is(false));
        }
    }

    @Test
    @SmallTest
    public void isOperationAllowed_mutation() {
        final EnumSet<Operation> allowedOperations = EnumSet.allOf(Operation.class);

        final SqliteUriMatch match = new SqliteUriMatch(getTestUri(), getTestUris(),
                allowedOperations, "test_table",
                "test_mime",
                true); //$NON-NLS-1$ //$NON-NLS-2$

        allowedOperations.clear();

        for (final Operation operation : Operation.values()) {
            assertThat(match.isOperationAllowed(operation), is(true));
        }
    }
}
