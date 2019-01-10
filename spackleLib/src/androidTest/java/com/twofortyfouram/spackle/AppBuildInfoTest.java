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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import com.twofortyfouram.test.context.MyMockContext;
import com.twofortyfouram.test.context.MyMockPackageManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
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
    public void getVersionName() {
        final String expected = ""; //$NON-NLS-1$
        final String actual = AppBuildInfo.getVersionName(ApplicationProvider.getApplicationContext());

        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getVersionCode() {
        final long expected = 0;
        final long actual = AppBuildInfo.getVersionCode(ApplicationProvider.getApplicationContext());
        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getApplicationName() {
        final String expected = ApplicationProvider.getApplicationContext().getPackageName();
        final String actual = AppBuildInfo.getApplicationName(ApplicationProvider.getApplicationContext());

        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getTargetSdkVersion() {
        final int expected = 5;
        final int actual = AppBuildInfo.getTargetSdkVersion(new TargetSdkVersionContext(expected));


        assertThat(actual, is(expected));
    }

    @Test
    @SmallTest
    public void getInstallWallTimeMillis() {
        final long expectedMillis = 1000;

        final InstallTimeContext context = new InstallTimeContext(expectedMillis);

        assertThat(AppBuildInfo.getInstallWallTimeMillis(context), is(expectedMillis));
    }

    private static final class InstallTimeContext extends MyMockContext {

        private final long mInstallWallTimeMillis;

        public InstallTimeContext(final long installWallTimeMillis) {
            mInstallWallTimeMillis = installWallTimeMillis;
        }

        @Override
        public String getPackageName() {
            return ApplicationProvider.getApplicationContext().getPackageName();
        }

        @Override
        @SuppressWarnings("deprecation")
        public PackageManager getPackageManager() {
            return new MyMockPackageManager() {
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

    private static final class DebugContext extends MyMockContext {

        private final boolean mIsDebuggable;

        public DebugContext(final boolean isDebuggable) {
            mIsDebuggable = isDebuggable;
        }

        @Override
        public String getPackageName() {
            return ApplicationProvider.getApplicationContext().getPackageName();
        }

        @Override
        public PackageManager getPackageManager() {
            return new MyMockPackageManager() {
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

    public static final class TargetSdkVersionContext extends MyMockContext {

        private final int mTargetSdkVersion;

        public TargetSdkVersionContext(final int targetSdkVersion) {
            mTargetSdkVersion = targetSdkVersion;
        }

        @Override
        public String getPackageName() {
            return ApplicationProvider.getApplicationContext().getPackageName();
        }

        @Override
        public ApplicationInfo getApplicationInfo() {
            @NonNull final ApplicationInfo info = new ApplicationInfo();

            info.targetSdkVersion = mTargetSdkVersion;

            return info;
        }

        @Override
        public PackageManager getPackageManager() {
            return new MyMockPackageManager() {
                @Override
                public PackageInfo getPackageInfo(String packageName, int flags)
                        throws NameNotFoundException {
                    PackageInfo info = new PackageInfo();

                    info.applicationInfo = TargetSdkVersionContext.this.getApplicationInfo();

                    return info;
                }
            };
        }
    }
}
