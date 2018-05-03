/*
 * android-spackle https://github.com/twofortyfouram/android-spackle
 * Copyright (C) 2009–2017 two forty four a.m. LLC
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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public final class AppBuildInfoTest {

    @Test
    @SmallTest
    public void testNonInstantiable() {
        assertThat(AppBuildInfo.class, notInstantiable());
    }

    @Test
    @SmallTest
    public void isDebuggable_debug() {
        assertThat(AppBuildInfo.isDebuggable(new DebugContext(true)), is(true));
    }


    @Test
    @SmallTest
    public void isDebuggable_release() {
        assertThat(AppBuildInfo.isDebuggable(new DebugContext(false)), is(false));
    }

    @Test
    @SmallTest
    public void getVersionName() throws NameNotFoundException {
        final String expected = ""; //$NON-NLS-1$
        final String actual = AppBuildInfo.getVersionName(getContext());

        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getVersionCode() throws NameNotFoundException {
        final int expected = 0;
        final int actual = AppBuildInfo.getVersionCode(getContext());
        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getApplicationName() {
        final String expected = "com.twofortyfouram.spackle.test"; //$NON-NLS-1$
        final String actual = AppBuildInfo.getApplicationName(getContext());

        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getInstallWallTimeMillis() {
        final long expectedMillis = 1000;

        final InstallTimeContext context = new InstallTimeContext(expectedMillis);

        assertThat(AppBuildInfo.getInstallWallTimeMillis(context), is(expectedMillis));
    }

    private static final class InstallTimeContext extends MockContext {

        private final long mInstallWallTimeMillis;

        public InstallTimeContext(final long installWallTimeMillis) {
            mInstallWallTimeMillis = installWallTimeMillis;
        }

        @Override
        public String getPackageName() {
            return InstrumentationRegistry.getContext().getPackageName();
        }

        @Override
        @SuppressWarnings("deprecation")
        public PackageManager getPackageManager() {
            return new MockPackageManager() {
                @Override
                public PackageInfo getPackageInfo(String packageName, int flags)
                        throws NameNotFoundException {
                    PackageInfo info = new PackageInfo();

                    info.firstInstallTime = mInstallWallTimeMillis;

                    return info;
                }
            };
        }
    }

    private static final class DebugContext extends MockContext {

        private final boolean mIsDebuggable;

        public DebugContext(final boolean isDebuggable) {
            mIsDebuggable = isDebuggable;
        }

        @Override
        public String getPackageName() {
            return InstrumentationRegistry.getContext().getPackageName();
        }

        @Override
        @SuppressWarnings("deprecation")
        public PackageManager getPackageManager() {
            return new MockPackageManager() {
                @Override
                public PackageInfo getPackageInfo(String packageName, int flags)
                        throws NameNotFoundException {
                    PackageInfo info = new PackageInfo();

                    info.applicationInfo = new ApplicationInfo();

                    if (mIsDebuggable) {
                        info.applicationInfo.flags = ApplicationInfo.FLAG_DEBUGGABLE;
                    }

                    return info;
                }
            };
        }
    }
}
