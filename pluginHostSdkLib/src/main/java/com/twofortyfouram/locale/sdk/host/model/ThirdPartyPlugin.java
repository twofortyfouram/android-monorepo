/*
 * android-plugin-host-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.host.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;


@Immutable
public final class ThirdPartyPlugin implements Plugin {

    /**
     * Implements the {@link Parcelable} interface.
     */
    @NonNull
    public static final Parcelable.Creator<ThirdPartyPlugin> CREATOR
            = new PluginParcelableCreator();

    /**
     * The type of plug-in.
     */
    @NonNull
    private final PluginType mType;

    /**
     * The component's Android package name.
     */
    @NonNull
    private final String mPackageName;

    /**
     * The fully qualified class name of the plug-in's edit Activity.
     *
     * @see LocalePluginIntentV1#ACTION_EDIT_CONDITION
     * @see LocalePluginIntentV1#ACTION_EDIT_SETTING
     */
    @NonNull
    private final String mActivityClassName;

    /**
     * Type of component for {@link #mComponentClassName}.
     */
    @NonNull
    private final ComponentType mComponentType;

    /**
     * The fully qualified class name of the plug-in's component.
     *
     * @see LocalePluginIntentV1#ACTION_QUERY_CONDITION
     * @see LocalePluginIntentV1#ACTION_FIRE_SETTING
     */
    @NonNull
    private final String mComponentClassName;

    /**
     * The cached registry name, as generated by
     * {@link #generateRegistryName(String, String)}.
     */
    @NonNull
    private final String mRegistryName;

    /**
     * The versionCode of the plug-in.
     *
     * @see PackageInfo#versionCode
     */
    private final int mVersionCode;

    /**
     * Configuration for the plug-in.
     */
    @NonNull
    private final PluginConfiguration mConfiguration;

    /**
     * Constructs a new instance.
     *
     * @param type               the type of the plug-in.
     * @param packageName        the package name of the plug-in.
     * @param activityClassName  the class name of the plug-in's edit
     *                           {@code Activity}.
     * @param componentType      Type of component for {@code componentClassName}.
     * @param componentClassName The class name of the plug-in's
     *                           component.
     * @param versionCode        The versionCode of the plug-in's package.
     * @param configuration      Configuration for the plug-in.
     */
    public ThirdPartyPlugin(@NonNull final PluginType type,
            @NonNull @Size(min = 1) final String packageName,
            @Size(min = 1) @NonNull final String activityClassName,
            @NonNull final ComponentType componentType,
            @NonNull @Size(min = 1) final String componentClassName,
            final int versionCode, @NonNull final PluginConfiguration configuration) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(packageName, "packageName"); //$NON-NLS-1$
        assertNotEmpty(activityClassName, "activityClassName"); //$NON-NLS-1$
        assertNotNull(componentType, "componentType"); //$NON-NLS-1$
        assertNotEmpty(componentClassName, "componentClassName"); //$NON-NLS-1$
        assertNotNull(configuration, "configuration"); //$NON-NLS-1$

        mType = type;
        mPackageName = packageName;
        mActivityClassName = activityClassName;
        mComponentType = componentType;
        mComponentClassName = componentClassName;
        mRegistryName = generateRegistryName(packageName, activityClassName);
        mVersionCode = versionCode;
        mConfiguration = configuration;
    }

    @NonNull
    @Override
    public final PluginType getType() {
        return mType;
    }

    @NonNull
    @Override
    public final String getRegistryName() {
        return mRegistryName;
    }

    @NonNull
    @Override
    public final String getPackageName() {
        return mPackageName;
    }

    @NonNull
    @Override
    public final String getActivityClassName() {
        return mActivityClassName;
    }

    @Override
    @NonNull
    public ComponentType getComponentType() {
        return mComponentType;
    }

    @NonNull
    @Override
    public final String getComponentIdentifier() {
        return mComponentClassName;
    }

    @Override
    public final int getVersionCode() {
        return mVersionCode;
    }

    @NonNull
    @Override
    public final PluginConfiguration getConfiguration() {
        return mConfiguration;
    }

    @NonNull
    @Slow(Speed.MILLISECONDS)
    @Override
    public final String getActivityLabel(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        return Plugin.getActivityLabel(context, this);
    }

    @Nullable
    @Slow(Speed.MILLISECONDS)
    @Override
    public final Drawable getActivityIcon(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        return Plugin.getActivityIcon(context, this);
    }

    /**
     * Generates the registry name using the plug-in's Android package and
     * {@code Activity} class.
     * <p>
     * By definition, all instances of a specific plug-in have the same registry
     * name. This requires that the Android package name and {@code Activity}
     * class name of a plug-in be consistent across updates.
     *
     * @param packageName  {@code String} representing the component's Android
     *                     package name.
     * @param activityName {@code String} representing the component's
     *                     {@code Activity} name.
     * @return {@code String} representing the registry name.
     */
    @NonNull
    public static String generateRegistryName(@NonNull final String packageName,
            @NonNull final String activityName) {
        assertNotEmpty(packageName, "packageName"); //$NON-NLS-1$
        assertNotEmpty(activityName, "activityName"); //$NON-NLS-1$

        /*
         * This string should remain stable--changes may have unintended
         * side-effects throughout the system.
         */
        return String.format(Locale.US, "%s:%s", packageName, activityName); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ThirdPartyPlugin that = (ThirdPartyPlugin) o;

        if (mVersionCode != that.mVersionCode) {
            return false;
        }
        if (mType != that.mType) {
            return false;
        }
        if (!mPackageName.equals(that.mPackageName)) {
            return false;
        }
        if (!mActivityClassName.equals(that.mActivityClassName)) {
            return false;
        }
        if (!mComponentClassName.equals(that.mComponentClassName)) {
            return false;
        }
        if (mComponentType != that.mComponentType) {
            return false;
        }
        return mConfiguration.equals(that.mConfiguration);
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + mPackageName.hashCode();
        result = 31 * result + mActivityClassName.hashCode();
        result = 31 * result + mComponentClassName.hashCode();
        result = 31 * result + mComponentType.hashCode();
        result = 31 * result + mVersionCode;
        result = 31 * result + mConfiguration.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mType.name());
        dest.writeString(mPackageName);
        dest.writeString(mActivityClassName);
        dest.writeSerializable(mComponentType);
        dest.writeString(mComponentClassName);
        dest.writeInt(mVersionCode);
        dest.writeParcelable(mConfiguration, flags);
    }

    @Override
    public String toString() {
        return "ThirdPartyPlugin{" +
                "mType=" + mType +
                ", mPackageName='" + mPackageName + '\'' +
                ", mActivityClassName='" + mActivityClassName + '\'' +
                ", mComponentClassName='" + mComponentClassName + '\'' +
                ", mComponentType=" + mComponentType +
                ", mRegistryName='" + mRegistryName + '\'' +
                ", mVersionCode=" + mVersionCode +
                ", mConfiguration=" + mConfiguration +
                '}';
    }

    @ThreadSafe
    private static final class PluginParcelableCreator
            implements Parcelable.Creator<ThirdPartyPlugin> {

        @Override
        public ThirdPartyPlugin createFromParcel(@NonNull final Parcel in) {
            final PluginType pluginType = PluginType.valueOf(in.readString());
            final String packageName = in.readString();
            final String activityClassName = in.readString();
            final ComponentType componentType = (ComponentType) in.readSerializable();
            final String componentClassName = in.readString();
            final int versionCode = in.readInt();
            final PluginConfiguration configuration = in
                    .readParcelable(this.getClass().getClassLoader());

            return new ThirdPartyPlugin(pluginType, packageName, activityClassName,
                    componentType, componentClassName,
                    versionCode, configuration);
        }

        @Override
        public ThirdPartyPlugin[] newArray(final int size) {
            return new ThirdPartyPlugin[size];
        }
    }
}
