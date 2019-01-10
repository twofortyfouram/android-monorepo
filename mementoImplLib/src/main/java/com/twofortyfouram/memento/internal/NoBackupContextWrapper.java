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
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.NonNull;
import com.twofortyfouram.annotation.Incubating;
import com.twofortyfouram.memento.impl.BuildConfig;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.ContextUtil;
import net.jcip.annotations.ThreadSafe;

import java.io.File;

/**
 * Context wrapper that remaps the database directory path to the {@link Context#getNoBackupFilesDir()}.
 * <p>
 * Breakout is prevented by overriding {@link #getApplicationContext()} and {@link #getBaseContext()}.
 */
@ThreadSafe
@Incubating
public final class NoBackupContextWrapper extends ContextWrapper {

    @NonNull
    private final Context mBaseContext;

    public NoBackupContextWrapper(@NonNull final Context base) {
        super(ContextUtil.cleanContext(base));

        mBaseContext = ContextUtil.cleanContext(base);
    }

    /**
     * @param name Database file name.
     * @return Path under {@link Context#getNoBackupFilesDir()}.
     */
    @Override
    public File getDatabasePath(@NonNull final String name) {
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.LOLLIPOP)) {
            if (BuildConfig.DEBUG && ContextUtil.isTestContext(mBaseContext)) {
                return super.getDatabasePath(name);
            } else {
                return new File(getNoBackupFilesDir(), name);
            }
        } else {
            // Hopefully this is consistent across Android devices.  But also the probability of a 4.4 device receiving
            // the 5.0 update after this many years seems improbably low.
            @NonNull final File fakeNoBackupDir = new File(mBaseContext.getFilesDir(), "no_backup"); //$NON-NLS
            fakeNoBackupDir.mkdir();

            return new File(fakeNoBackupDir, name);
        }
    }

    @Override
    public Context getApplicationContext() {
        // Prevent breakout
        return this;
    }

    @Override
    public Context getBaseContext() {
        // Prevent breakout
        return this;
    }
}
