/*
 * android-plugin-sdk-for-locale
 * https://github.com/twofortyfouram/android-plugin-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.spackle.bundle.BundleComparer;
import com.twofortyfouram.spackle.bundle.BundlePrinter;

import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Data representing an instance of a plug-in. This consists of a key for
 * identifying the actual {@link Plugin}, along with the Bundle and Blurb
 * actually representing the plug-in's saved data.
 *
 * Note: this class is thread-safe and effectively immutable as long as clients do not attempt to
 * mutate the Bundle passed into the constructor or returned by {@link #getBundle()}.
 */
@ThreadSafe
public final class PluginInstanceData implements Parcelable {

    /**
     * Implements the {@link Parcelable} interface.
     */
    @NonNull
    public static final Parcelable.Creator<PluginInstanceData> CREATOR
            = new PluginInstanceDataCreator();

    /**
     * Maximum size of a serialized {@code Bundle}, which is about 25 kilobytes
     * (base-10).
     */
    /*
     * Intent extras are limited to about 500 kilobytes, although the exact
     * number is not specified by Android. In Android 4.4 KitKat, the maximum
     * amount of data that can be written to a ContentProvider was reduced to
     * less than 300 kilobytes. The maximum bundle size here was chosen to allow several large
     * plug-ins to be added to a Situation before overflow occurs.
     */
    public static final int MAXIMUM_BUNDLE_SIZE_BYTES = 25 * 1000;

    /**
     * The type of the plug-in.
     */
    @NonNull
    private final PluginType mType;

    /**
     * Key identifying the actual plug-in that created this data.
     */
    @NonNull
    private final String mRegistryName;

    /**
     * The plug-in's Bundle.
     *
     * @see LocalePluginIntent#EXTRA_BUNDLE
     */
    @NonNull
    private final Bundle mBundle;

    /**
     * The blurb.
     *
     * @see LocalePluginIntent#EXTRA_STRING_BLURB
     */
    @NonNull
    private final String mBlurb;

    /**
     * Constructs a new instance of plug-in data.
     *
     * @param type         The type of the plug-in.
     * @param registryName Registry name of the plug-in.
     * @param bundle       The plug-in's Bundle.  Do not mutate {@code bundle} or its contents
     *                     after passing it into this constructor.
     * @param blurb        The plug-in's blurb.
     */
    public PluginInstanceData(@NonNull final PluginType type, @NonNull final String registryName,
            @NonNull final Bundle bundle,
            @NonNull final String blurb) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotNull(registryName, "registryName"); //$NON-NLS-1$
        assertNotNull(bundle, "bundle"); //$NON-NLS-1$
        assertNotNull(blurb, "blurb"); //$NON-NLS-1$

        mType = type;
        mRegistryName = registryName;

        // TODO: Android O perform a deep copy
        mBundle = new Bundle(bundle);
        mBlurb = blurb;
    }

    /**
     * @return The type of the plug-in.
     */
    @NonNull
    public PluginType getType() {
        return mType;
    }

    /**
     * @return The registry name of the plug-in.
     * @see Plugin#getRegistryName()
     */
    @NonNull
    public String getRegistryName() {
        return mRegistryName;
    }

    /**
     * @return The plug-in's Bundle. Do not modify, as this is the internal representation of this
     * class.
     */
    @NonNull
    public Bundle getBundle() {
        // TODO: Use O's deep copy API
        return new Bundle(mBundle);
    }

    /**
     * @return The plug-in's blurb.
     */
    @NonNull
    public String getBlurb() {
        return mBlurb;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PluginInstanceData that = (PluginInstanceData) o;

        if (!mBlurb.equals(that.mBlurb)) {
            return false;
        }
        if (!mRegistryName.equals(that.mRegistryName)) {
            return false;
        }
        if (!BundleComparer.areBundlesEqual(mBundle, that.mBundle)) {
            return false;
        }
        if (mType != that.mType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + mRegistryName.hashCode();
        result = 31 * result + mBundle.hashCode();
        result = 31 * result + mBlurb.hashCode();
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.US,
                "PluginInstanceData{mType=\'%s\', mRegistryName=\'%s\', mBlurb=\'%s\', mBundle=\'%s\'",
                //$NON-NLS-1$
                mType, mRegistryName, mBlurb, BundlePrinter.toString(mBundle));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        assertNotNull(dest, "dest"); //$NON-NLS-1$

        dest.writeString(mType.name());
        dest.writeString(mRegistryName);
        dest.writeBundle(mBundle);
        dest.writeString(mBlurb);
    }

    @ThreadSafe
    private static class PluginInstanceDataCreator implements Creator<PluginInstanceData> {

        @Override
        public PluginInstanceData createFromParcel(@NonNull final Parcel in) {
            assertNotNull(in, "in"); //$NON-NLS-1$

            final PluginType pluginType = PluginType.valueOf(in.readString());
            final String registryName = in.readString();
            final Bundle bundle = in.readBundle(getClass().getClassLoader());
            final String blurb = in.readString();

            return new PluginInstanceData(pluginType, registryName, bundle, blurb);
        }

        @Override
        public PluginInstanceData[] newArray(final int size) {
            return new PluginInstanceData[size];
        }
    }
}
