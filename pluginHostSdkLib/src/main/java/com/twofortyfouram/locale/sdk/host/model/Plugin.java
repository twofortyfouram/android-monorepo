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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.Size;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.R;
import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;


@Immutable
public final class Plugin implements IPlugin {

    /**
     * Implements the {@link Parcelable} interface.
     */
    @NonNull
    public static final Parcelable.Creator<Plugin> CREATOR = new PluginParcelableCreator();

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
     * @see LocalePluginIntent#ACTION_EDIT_CONDITION
     * @see LocalePluginIntent#ACTION_EDIT_SETTING
     */
    @NonNull
    private final String mActivityClassName;

    /**
     * The fully qualified class name of the plug-in's BroadcastReceiver.
     *
     * @see LocalePluginIntent#ACTION_QUERY_CONDITION
     * @see LocalePluginIntent#ACTION_FIRE_SETTING
     */
    @NonNull
    private final String mReceiverClassName;

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
     * @param type              the type of the plug-in.
     * @param packageName       the package name of the plug-in.
     * @param activityClassName the class name of the plug-in's edit
     *                          {@code Activity}.
     * @param receiverClassName The class name of the plug-in's
     *                          {@code BroadcastReceiver}.
     * @param versionCode       The versionCode of the plug-in's package.
     * @param configuration     Configuration for the plug-in.
     */
    public Plugin(@NonNull final PluginType type, @NonNull @Size(min = 1) final String packageName,
            @Size(min = 1) @NonNull final String activityClassName,
            @NonNull @Size(min = 1) final String receiverClassName,
            final int versionCode, @NonNull final PluginConfiguration configuration) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(packageName, "packageName"); //$NON-NLS-1$
        assertNotEmpty(activityClassName, "activityClassName"); //$NON-NLS-1$
        assertNotEmpty(receiverClassName, "receiverClassName"); //$NON-NLS-1$
        assertNotNull(configuration, "configuration"); //$NON-NLS-1$

        mType = type;
        mPackageName = packageName;
        mActivityClassName = activityClassName;
        mReceiverClassName = receiverClassName;
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

    @NonNull
    @Override
    public final String getReceiverClassName() {
        return mReceiverClassName;
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

        return getActivityLabel(context, this);
    }

    // TODO: Java 8 refactor this to the IPlugin interface.
    @NonNull
    @Slow(Speed.MILLISECONDS)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    /*package*/ static final String getActivityLabel(@NonNull final Context context,
            @NonNull final IPlugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(context, "plugin"); //$NON-NLS-1$

        CharSequence name = plugin.getActivityClassName();

        final PackageManager packageManager = context.getPackageManager();
        try {
            final ActivityInfo activityInfo = packageManager
                    .getActivityInfo(newComponentName(plugin), 0);

            if (0 == activityInfo.labelRes && null != activityInfo.nonLocalizedLabel) {
                name = activityInfo.nonLocalizedLabel;
            } else if (0 != activityInfo.labelRes) {
                name = packageManager.getText(plugin.getPackageName(), activityInfo.labelRes,
                        activityInfo.applicationInfo);
            }

            if (null == name || 0 == name.length()) {
                name = plugin.getActivityClassName();
            }
        } catch (final NameNotFoundException e) {
            /*
             * This could happen if the plug-in is uninstalled.
             */
        }

        return name.toString();
    }

    @Nullable
    @Slow(Speed.MILLISECONDS)
    @Override
    public final Drawable getActivityIcon(@NonNull final Context context) {
        assertNotNull(context, "context"); //$NON-NLS-1$

        return getActivityIcon(context, this);
    }

    // TODO: Java 8 refactor this to the IPlugin interface.
    @Nullable
    @Slow(Speed.MILLISECONDS)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    /*package*/ static final Drawable getActivityIcon(@NonNull final Context context,
            @NonNull final IPlugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(plugin, "plugin"); //$NON-NLS

        final PackageManager packageManager = context.getPackageManager();

        Drawable icon = null;
        try {
            icon = packageManager
                    .getActivityIcon(newComponentName(plugin));
        } catch (final NameNotFoundException e) {
            icon = packageManager.getDefaultActivityIcon();
        }

        /*
         * If necessary, scale the icon.
         */
        if (icon instanceof BitmapDrawable) {
            final Resources res = context.getResources();
            final int size = res.getDimensionPixelSize(
                    R.dimen.com_twofortyfouram_locale_sdk_host_plugin_icon_size);
            if (icon.getIntrinsicHeight() != size
                    || icon.getIntrinsicWidth() != size) {
                Lumberjack
                        .always("WARNING: Plug-in %s Activity icon size %dx%d is inappropriate for current screen resolution.  Icon should be %dx%d pixels",
                                plugin.getActivityClassName(), icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight(), size, size); //$NON-NLS-1$
                icon = new BitmapDrawable(res,
                        Bitmap.createScaledBitmap(
                                ((BitmapDrawable) icon).getBitmap(), size, size, false)
                );
            }
        }

        return icon;
    }

    @NonNull
    private static ComponentName newComponentName(@NonNull final IPlugin plugin) {
        return new ComponentName(plugin.getPackageName(), plugin.getActivityClassName());
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Plugin plugin = (Plugin) o;

        if (mVersionCode != plugin.mVersionCode) {
            return false;
        }
        if (!mActivityClassName.equals(plugin.mActivityClassName)) {
            return false;
        }
        if (!mConfiguration.equals(plugin.mConfiguration)) {
            return false;
        }
        if (!mPackageName.equals(plugin.mPackageName)) {
            return false;
        }
        if (!mReceiverClassName.equals(plugin.mReceiverClassName)) {
            return false;
        }
        if (!mRegistryName.equals(plugin.mRegistryName)) {
            return false;
        }
        if (mType != plugin.mType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + mPackageName.hashCode();
        result = 31 * result + mActivityClassName.hashCode();
        result = 31 * result + mReceiverClassName.hashCode();
        result = 31 * result + mRegistryName.hashCode();
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
        dest.writeString(mReceiverClassName);
        dest.writeInt(mVersionCode);
        dest.writeParcelable(mConfiguration, flags);
    }

    @Override
    public String toString() {
        return String
                .format(Locale.US,
                        "Plugin [mType=%s, mPackageName=%s, mActivityClassName=%s, mReceiverClassName=%s, mVersionCode=%s, mConfiguration=%s]",
//$NON-NLS-1$
                        mType, mPackageName, mActivityClassName, mReceiverClassName, mVersionCode,
                        mConfiguration);
    }

    @ThreadSafe
    private static final class PluginParcelableCreator implements Parcelable.Creator<Plugin> {

        @Override
        public Plugin createFromParcel(@NonNull final Parcel in) {
            final PluginType pluginType = PluginType.valueOf(in.readString());
            final String packageName = in.readString();
            final String activityClassName = in.readString();
            final String receiverClassName = in.readString();
            final int versionCode = in.readInt();
            final PluginConfiguration configuration = in
                    .readParcelable(this.getClass().getClassLoader());

            return new Plugin(pluginType, packageName, activityClassName, receiverClassName,
                    versionCode, configuration);
        }

        @Override
        public Plugin[] newArray(final int size) {
            return new Plugin[size];
        }
    }
}
