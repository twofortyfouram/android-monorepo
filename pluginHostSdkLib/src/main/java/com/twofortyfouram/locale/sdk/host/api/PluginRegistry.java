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

package com.twofortyfouram.locale.sdk.host.api;


import android.content.Context;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.twofortyfouram.locale.sdk.host.internal.ThirdPartyPluginRegistry;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;

import java.util.Map;

/**
 * Dynamically discovers all {@link Plugin}s currently installed.  Initially loading the plug-ins
 * can take a few seconds, so there are both blocking {@link #getPluginMap(PluginType)} and
 * non-blocking {@link #peekPluginMap(PluginType)} calls to retrieve a snapshot
 * the registry.
 * <p>
 * The registry will automatically keep itself up to date, and clients of this
 * class can monitor for changes via the Intent action returned by
 * {@link #getChangeIntentAction()}. When the registry changes, the snapshots
 * previously retrieved from the registry will not change. To get the latest
 * changes, call {@link #getPluginMap(PluginType)} or
 * {@link #peekPluginMap(PluginType)} again.
 */
/*
 * Note: This interface exists to make it easier to substitute the plug-in registry in automated
 * tests, and also for swapping out the registry for a composition class in the main Locale app
 * that unifies third party and built-in conditions/settings.
 */
public interface PluginRegistry {

    /**
     * @param context Application Context.
     * @return the default singleton implementation of the registry.
     */
    @NonNull
    @AnyThread
    static PluginRegistry getInstance(@NonNull final Context context) {
        return ThirdPartyPluginRegistry.getInstance(context);
    }

    /**
     * The registry broadcasts this Intent action when the registry changes,
     * allowing clients to implement an observer design pattern.
     *
     * @return the Intent action broadcast when the registry changes.
     * @see #getChangeIntentPermission()
     */
    @NonNull
    @Size(min = 1)
    String getChangeIntentAction();

    /**
     * @return Permission string protecting {@link #getChangeIntentAction()}.
     */
    @NonNull
    @Size(min = 1)
    String getChangeIntentPermission();

    /**
     * Retrieves a snapshot of the registry's latest state, blocking until the
     * registry is loaded. Once the registry is initially loaded, this method
     * will no longer block.
     * <p>
     * Return values of this method are snapshots and will not change. If the
     * Intent from {@link #getChangeIntentAction()} is broadcast, call
     * {@code getPluginMap(PluginType)} again to get an updated snapshot.
     *
     * @param type The type of registry information to return.
     * @return A snapshot of the current registry state, which is a map of
     * {@link Plugin#getRegistryName()} keys to {@link Plugin}
     * instances. Clients MUST NOT attempt to modify the map.
     */
    @NonNull
    @Size(min = 0)
    Map<String, IPlugin> getPluginMap(@NonNull final PluginType type);

    /**
     * Retrieves a snapshot of the registry's latest state or {@code null} if
     * the registry isn't loaded yet. Once the registry is initially loaded,
     * this method will no longer return {@code null}.
     * <p>
     * Return values of this method are snapshots and will not change. If the
     * Intent from {@link #getChangeIntentAction()} is broadcast, call
     * {@code peekPluginMap(PluginType)} again to get an updated snapshot.
     *
     * @param type The type of registry information to return.
     * @return A snapshot of the current registry state, which is a map of
     * {@link Plugin#getRegistryName()} keys to {@link Plugin}
     * instances. This may return null if
     * the registry isn't loaded yet. Clients MUST NOT attempt to modify the map.
     */
    @Nullable
    @Size(min = 0)
    Map<String, IPlugin> peekPluginMap(@NonNull final PluginType type);
}
