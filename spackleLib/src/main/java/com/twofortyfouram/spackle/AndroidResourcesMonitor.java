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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import net.jcip.annotations.NotThreadSafe;

import java.util.HashSet;
import java.util.Set;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Monitor for configuration changes.  This class is not thread safe. Callbacks will be delivered to the thread that
 * constructs this class.
 */
@NotThreadSafe
public final class AndroidResourcesMonitor {

    @NonNull
    private final Context mContext;

    @NonNull
    private final InterestingConfigChanges mInterestingConfigChanges;

    @Nullable
    private BroadcastReceiver mConfigurationChangedReceiver = null;

    @NonNull
    private final Set<ResConfigurationChangeCallback> mObservers;

    @NonNull
    private final Handler mHandler;

    /**
     * @param context                Application context.
     * @param isDensityImportant     True if density changes are important.
     * @param interestingConfigFlags Bitwise or res config flags.
     */
    public AndroidResourcesMonitor(@NonNull final Context context, final boolean isDensityImportant, final int interestingConfigFlags) {
        mContext = ContextUtil.cleanContext(context);

        mInterestingConfigChanges = new InterestingConfigChanges(isDensityImportant, interestingConfigFlags);

        mObservers = new HashSet<>();
        mHandler = new Handler();
    }

    @SuppressLint("CheckResult")
    public void addObserver(@NonNull final ResConfigurationChangeCallback callback) {
        assertNotNull(callback, "callback"); //$NON-NLS

        if (mObservers.isEmpty()) {
            mConfigurationChangedReceiver = new ConfigurationChangeReceiver();
            mContext.registerReceiver(mConfigurationChangedReceiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED), null, mHandler);

            mInterestingConfigChanges.applyNewConfig(mContext.getResources());
        }

        mObservers.add(callback);
    }

    public void removeObserver(@NonNull final ResConfigurationChangeCallback callback) {
        assertNotNull(callback, "callback"); //$NON-NLS

        mObservers.remove(callback);

        if (mObservers.isEmpty()) {
            mContext.unregisterReceiver(mConfigurationChangedReceiver);
            mConfigurationChangedReceiver = null;
        }
    }

    @NotThreadSafe
    private final class ConfigurationChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
            if (mInterestingConfigChanges.applyNewConfig(mContext.getResources())) {
                for (@NonNull final ResConfigurationChangeCallback callback : mObservers) {
                    callback.onResChanged();
                }
            }
        }
    }

    public interface ResConfigurationChangeCallback {
        void onResChanged();
    }

    /**
     * Helper for determining if the configuration has changed in an interesting
     * way.
     */
    @NotThreadSafe
    public static final class InterestingConfigChanges {

        /**
         * The configuration as of the last time
         * {@link #applyNewConfig(Resources)} was called.
         */
        @NonNull
        private final Configuration mLastConfiguration = new Configuration();

        private final boolean mIsDensityImportant;

        private final int mConfigFlags;

        /**
         * The density as of the last time {@link #applyNewConfig(Resources)}
         * was called.
         */
        private int mLastDensity;

        public InterestingConfigChanges(final boolean isDensityImportant, final int configFlags) {
            mIsDensityImportant = isDensityImportant;
            mConfigFlags = configFlags;
        }

        /**
         * Call to provide the latest configuration.
         *
         * @param res New resources.
         * @return true if the new resources are different from the previous
         * resources.
         */
        @CheckResult
        public boolean applyNewConfig(@NonNull final Resources res) {
            assertNotNull(res, "res"); //$NON-NLS-1$

            /*
             * Note: this implementation is somewhat brittle, as future versions
             * of Android could introduce new configuration changes that this
             * mechanism doesn't detect.
             */
            final int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            final boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;

            if (mIsDensityImportant && densityChanged) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }

            if (0 != (configChanges & mConfigFlags)) {
                return true;
            }

            return false;
        }
    }
}
