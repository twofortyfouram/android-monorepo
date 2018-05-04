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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.Size;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.locale.api.v1.LocalePluginIntentV1;
import com.twofortyfouram.locale.sdk.host.R;
import com.twofortyfouram.log.Lumberjack;

import net.jcip.annotations.Immutable;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Represents a plug-in for Locale.  A plug-in consists of an Android package name, an Activity
 * for
 * {@link
 * LocalePluginIntentV1#ACTION_EDIT_CONDITION ACTION_EDIT_CONDITION} or {@link
 * LocalePluginIntentV1#ACTION_EDIT_SETTING ACTION_EDIT_SETTING}, along with a
 * ContentProvider for
 * {@link LocalePluginIntentV1#ACTION_QUERY_CONDITION ACTION_QUERY_CONDITION} or
 * {@link
 * LocalePluginIntentV1#ACTION_FIRE_SETTING ACTION_FIRE_SETTING}.  (Version 1.0 of the spec allows for
 * a BroadcastReceiver for these intent actions).
 * <p>
 * Note: This class does not validate the plug-in it represents. It is the
 * caller's responsibility to provide valid names during construction, and the
 * caller's responsibility to handle plug-ins that become invalid. This
 * lack of validation is an intentional design decision because packages on
 * Android can be installed, uninstalled, or changed at any time.
 */
/*
 * As an implementation note, this interface exists to make testing and implementation in the main
 * Locale app possible.  Clients are not expected to subclass this, but rather use the
 * implementation provided by {@link ThirdPartyPlugin} which is transparently provided by the ThirdPartyPluginRegistry.
 */
@Immutable
public interface Plugin extends Parcelable {

    @NonNull
    static ComponentName newComponentName(@NonNull final Plugin plugin) {
        return new ComponentName(plugin.getPackageName(), plugin.getActivityClassName());
    }

    @Nullable
    @Slow(Slow.Speed.MILLISECONDS)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    /*package*/ static Drawable getActivityIcon(@NonNull final Context context,
            @NonNull final Plugin plugin) {
        assertNotNull(context, "context"); //$NON-NLS
        assertNotNull(plugin, "plugin"); //$NON-NLS

        final PackageManager packageManager = context.getPackageManager();

        Drawable icon = null;
        try {
            icon = packageManager
                    .getActivityIcon(newComponentName(plugin));
        } catch (final PackageManager.NameNotFoundException e) {
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
    @Slow(Slow.Speed.MILLISECONDS)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    /*package*/ static String getActivityLabel(@NonNull final Context context,
            @NonNull final Plugin plugin) {
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
        } catch (final PackageManager.NameNotFoundException e) {
            /*
             * This could happen if the plug-in is uninstalled.
             */
        }

        return name.toString();
    }

    /**
     * @return The plug-in's type.
     */
    @NonNull
    PluginType getType();

    /**
     * @return The plug-in's registry name, as would uniquely identify the plug-in across all apps.
     * Note that the combination of {@link PluginType} and registry name is unique, so two plug-ins
     * could share the same registry name as long as the type is different.
     */
    @NonNull
    @Size(min = 1)
    String getRegistryName();

    /**
     * @return The plug-in's package name. This is the Android definition of
     * package (e.g. AndroidManifest), rather than the Java definition
     * of package.
     */
    @NonNull
    @Size(min = 1)
    String getPackageName();

    /**
     * @return The fully qualified class name of the plug-in's edit Activity.
     */
    @NonNull
    @Size(min = 1)
    String getActivityClassName();

    /**
     * @return The type of application component represented by {@link #getComponentIdentifier()}.
     * @see #getComponentIdentifier()
     */
    @NonNull
    ComponentType getComponentType();

    /**
     * @return For {@link ComponentType#BROADCAST_RECEIVER}: The fully qualified class name of the
     * plug-in's application BroadcastReceiver.  For {@link ComponentType#CONTENT_PROVIDER}: the
     * provider's authority string.
     * @see #getComponentType()
     */
    @NonNull
    String getComponentIdentifier();

    /**
     * @return The versionCode of the plug-in's package.
     */
    int getVersionCode();

    /**
     * @return The plug-in's configuration.
     */
    @NonNull
    PluginConfiguration getConfiguration();

    /**
     * Retrieves a human-readable version of the ThirdPartyPlugin's name that would be
     * appropriate to display to a user in a UI.
     * <p>
     * The results of this method may change after a configuration change (e.g.
     * the system's locale is changed).
     *
     * @param context Application context.
     * @return the display name of the plug-in.
     */
    @NonNull
    @Slow(Slow.Speed.MILLISECONDS)
    String getActivityLabel(@NonNull final Context context);

    /**
     * Retrieves a human-viewable version of the plug-in's icon that would be
     * appropriate to display to a user in a UI.
     * <p>
     * The results of this method may change after a configuration change (e.g.
     * the system's locale is changed).
     * <p>
     * This method will scale the size of the icon if the dimension resource
     * com_twofortyfouram_locale_sdk_host_plugin_icon_size exists.
     *
     * @param context Application context.
     * @return the icon of the plug-in.
     */
    @Nullable
    Drawable getActivityIcon(@NonNull final Context context);

}
