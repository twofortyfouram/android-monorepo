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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.locale.api.LocalePluginIntent;

import net.jcip.annotations.Immutable;

/**
 * Represents a plug-in for Locale.  A plug-in consists of an Android package name, an Activity
 * for
 * {@link
 * LocalePluginIntent#ACTION_EDIT_CONDITION ACTION_EDIT_CONDITION} or {@link
 * LocalePluginIntent#ACTION_EDIT_SETTING ACTION_EDIT_SETTING}, along with a
 * BroadcastReceiver for
 * {@link LocalePluginIntent#ACTION_QUERY_CONDITION ACTION_QUERY_CONDITION} or
 * {@link
 * LocalePluginIntent#ACTION_FIRE_SETTING ACTION_FIRE_SETTING}.
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
 * implementation provided by {@link Plugin}.
 */
@Immutable
public interface IPlugin extends Parcelable {

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
     * @return The fully qualified class name of the plug-in's
     * BroadcastReceiver.
     */
    @NonNull
    String getReceiverClassName();

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
     * Retrieves a human-readable version of the Plugin's name that would be
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
