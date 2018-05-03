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

package com.twofortyfouram.locale.sdk.host.ui.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginErrorEdit;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;

import java.util.EnumSet;

/**
 * Common interface for Fragments that manage editing plug-ins.
 */
public interface IPluginEditFragment {
    /**
     * Type: {@code Plugin} (e.g. Parcelable).
     * <p>
     * The plug-in currently being edited by this Fragment.
     */
    @NonNull
    String ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN = "com.twofortyfouram.locale.sdk.host.ui.fragment.PluginEditFragment.arg.PARCELABLE_CURRENT_PLUGIN"; //$NON-NLS-1$

    /**
     * Type: {@code PluginInstanceData} (e.g. {@code Parcelable}).
     * <p>
     * Optional argument for the previously saved plug-in instance.
     */
    @NonNull
    String ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA = "com.twofortyfouram.locale.sdk.host.ui.fragment.PluginEditFragment.arg.PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA"; //$NON-NLS-1$

    /**
     * Callback when a plug-in is ready to be saved.
     * <p>
     * After this method is called, the Fragment will be automatically removed.
     *
     * @param plugin                The plug-in.
     * @param newPluginInstanceData The new plug-in instance data.
     */
    void handleSave(@NonNull final Plugin plugin,
            @NonNull final PluginInstanceData newPluginInstanceData);

    /**
     * Called when a plug-in is canceled.
     * <p>
     * After this method is called, the Fragment will be automatically removed.
     *
     * @param plugin The plug-in.
     */
    void handleCancel(@NonNull final Plugin plugin);


    /**
     * Called when a plug-in has an error.
     * <p>
     * After this method is called, the Fragment will be automatically removed.
     *
     * @param plugin The plug-in
     * @param error  The errors that occurred.
     */
    void handleErrors(@NonNull final Plugin plugin,
            @NonNull @Size(min = 1) final EnumSet<PluginErrorEdit> error);
}
