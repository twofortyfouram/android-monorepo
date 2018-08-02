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

package com.twofortyfouram.memento.test.second_process;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.twofortyfouram.memento.model.SqliteUriMatcher;
import com.twofortyfouram.memento.provider.MementoContentProvider;
import net.jcip.annotations.ThreadSafe;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Simple concrete implementation of {@link MementoContentProvider} for testing, configured to run in a second process
 * to verify correct behavior of the Transactable interface.
 */
@ThreadSafe
public final class SecondProcessContentProviderImpl extends MementoContentProvider {

    @NonNull
    private static final String DB_FILE_NAME = "com.twofortyfouram.memento.debug.process2.sqlite3";
    //$NON-NLS-1$

    /**
     * @param context Application context.
     * @return Retrieves the content authority of this provider.
     */
    /*
     * Note: Because the ContentProvider authority namespace is global on Android, each application must
     * have a different ContentProvider authority.  If your application has multiple flavors on the
     * app store, then using the package name as a prefix makes it easy to automatically have different
     * authorities.  The associated manifest entry can then be configured with a placeholder.
     *
     * The reason the authority is hard-coded instead of a concatenation of getPackageName() + ".provider"
     * is that the ProviderTestCase2's mock context throws UnsupportedOperationException.
     */
    @NonNull
    public static String getContentAuthority(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        return "com.twofortyfouram.memento.impl.test.provider2"; //$NON-NLS-1$
    }

    @NonNull
    public static Uri getContentAuthorityUri(@NonNull final Context context) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority
                (getContentAuthority(context)).build();
    }

    @NonNull
    @Override
    public SqliteUriMatcher newSqliteUriMatcher() {
        return new SqliteUriMatcherImpl(getContext());
    }

    @NonNull
    @Override
    public SupportSQLiteOpenHelper newSqliteOpenHelper() {
        final SupportSQLiteOpenHelper.Configuration.Builder config
                = SupportSQLiteOpenHelper.Configuration
                .builder(getContext());

        config.name(DB_FILE_NAME);

        config.callback(new CallbackImpl());

        return new FrameworkSQLiteOpenHelperFactory().create(config.build());
    }
}
