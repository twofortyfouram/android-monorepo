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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.sdk.host.internal.BundleSerializer;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.bundle.BundlePrinter;

import net.jcip.annotations.ThreadSafe;

import java.util.Arrays;
import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Data representing an instance of a plug-in. This consists of a key for
 * identifying the actual {@link Plugin}, along with the Bundle and Blurb
 * actually representing the plug-in's saved data.
 */
/*
 * Implementation note: Bundle is represented in byte[] form, because that allows
 * this class to be guaranteed immutable.  For example, a copy of the byte[] is made during
 * construction, and another copy is made when the getter is called.  The alternative,
 * representing the Bundle as a Bundle object, does not allow this class to be immutable.  A
 * shallow copy of Bundle could be made during construction and via the getter, but a deep copy
 * is necessary to make this truly safe.
 *
 * Because this class is immutable, it is not as efficient as it could be.  To improve
 * performance during runtime, the Condition and Setting classes can also accept a deserialized
 * bundle.  When using that API, ensuring immutability of the data is the responsibility of the
 * SDK's user.
 */
@ThreadSafe
public final class PluginInstanceData implements Parcelable {

    /**
     * Implements the {@link Parcelable} interface.
     */
    @NonNull
    public static final Parcelable.Creator<PluginInstanceData> CREATOR
            = new Parcelable.Creator<PluginInstanceData>() {
        @Override
        public PluginInstanceData createFromParcel(final Parcel in) {
            assertNotNull(in, "in"); //$NON-NLS-1$

            final PluginType pluginType = PluginType.valueOf(in.readString());
            final String registryName = in.readString();

            final byte[] serializedBundle;
            {
                final int length = in.readInt();
                serializedBundle = new byte[length];
                in.readByteArray(serializedBundle);
            }

            final String blurb = in.readString();

            return new PluginInstanceData(pluginType, registryName, serializedBundle, blurb);
        }

        @Override
        public PluginInstanceData[] newArray(final int size) {
            return new PluginInstanceData[size];
        }
    };

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
     * Serialized representation of the plug-in's Bundle.
     *
     * @see com.twofortyfouram.locale.sdk.host.internal.BundleSerializer
     * @see com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE
     */
    @NonNull
    private final byte[] mSerializedBundle;

    /**
     * The blurb.
     *
     * @see com.twofortyfouram.locale.api.Intent#EXTRA_STRING_BLURB
     */
    @NonNull
    private final String mBlurb;

    /**
     * Constructs a new Plug-in instance.
     *
     * @param type             The type of the plug-in.
     * @param registryName     Registry name of the plug-in.
     * @param serializedBundle Serialized representation of the plug-in's
     *                         Bundle. These bytes will be copied, in order to prevent
     *                         exposing the internal representation of this class to
     *                         mutation.
     * @param blurb            The plug-in's blurb.
     */
    public PluginInstanceData(@NonNull final PluginType type, @NonNull final String registryName,
                              @NonNull final byte[] serializedBundle,
                              @NonNull final String blurb) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotNull(registryName, "registryName"); //$NON-NLS-1$
        assertNotNull(serializedBundle, "serializedBundle"); //$NON-NLS-1$
        assertNotNull(blurb, "blurb"); //$NON-NLS-1$

        mType = type;
        mRegistryName = registryName;

        mSerializedBundle = copyArray(serializedBundle);
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
     * @return The serialized representation of the plug-in's Bundle. A new copy
     * of the bytes are returned each time this method is called, in
     * order to avoid exposing the internal representation of this
     * class.
     */
    @NonNull
    public byte[] getSerializedBundle() {
        return copyArray(mSerializedBundle);
    }

    /**
     * @return The plug-in's blurb.
     */
    @NonNull
    public String getBlurb() {
        return mBlurb;
    }

    @NonNull
    @SuppressLint("NewApi")
    private static byte[] copyArray(@NonNull final byte[] toCopy) {
        assertNotNull(toCopy, "toCopy"); //$NON-NLS-1$

        final byte[] result;
        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.GINGERBREAD)) {
            result = Arrays.copyOf(toCopy, toCopy.length);
        } else {
            result = new byte[toCopy.length];
            System.arraycopy(toCopy, 0, result, 0, toCopy.length);
        }

        return result;
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
        if (!Arrays.equals(mSerializedBundle, that.mSerializedBundle)) {
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
        result = 31 * result + Arrays.hashCode(mSerializedBundle);
        result = 31 * result + mBlurb.hashCode();
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        Bundle bundle;
        try {
            bundle = BundleSerializer.deserializeFromByteArray(mSerializedBundle);
        } catch (final ClassNotFoundException e) {
            bundle = null;
        }

        return String.format(Locale.US,
                "PluginInstanceData{mType=\'%s\', mRegistryName=\'%s\', mBlurb=\'%s\', mSerializedBundle=\'%s\'", //$NON-NLS-1$
                mType, mRegistryName, mBlurb, BundlePrinter.toString(bundle));
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
        dest.writeInt(mSerializedBundle.length);
        dest.writeByteArray(mSerializedBundle);
        dest.writeString(mBlurb);
    }
}
