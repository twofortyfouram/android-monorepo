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

package com.twofortyfouram.locale.sdk.host.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginConfiguration;
import com.twofortyfouram.locale.sdk.host.model.PluginErrorRegister;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.Clock;
import com.twofortyfouram.spackle.PermissionCompat;

import net.jcip.annotations.ThreadSafe;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Provides utility methods for checking Android packages.
 */
@ThreadSafe
public final class PluginPackageScanner {

    /**
     * Comparator for sorting by package name.
     */
    @NonNull
    private static final Comparator<ResolveInfo> PACKAGE_NAME_COMPARATOR
            = new com.twofortyfouram.locale.sdk.host.internal.PackageNameComparator();

    /**
     * Loads a map of plug-ins by scanning packages currently installed.
     *
     * @param context        Application context.
     * @param type           Type of plug-in to load.
     * @param onlyForPackage Optional filter to only get a map for the given
     *                       package. {@code null} implies no filter.
     * @return Map of registry name to plug-in.
     */
    /*
     * Note: The performance of this method varies with the number of plug-ins
     * installed on the device.
     */
    /*
     * Deprecation warnings are suppressed because there isn't an alternative to
     * PluginCharacteristics yet.
     */
    @NonNull
    @Slow(Speed.SECONDS)
    public static Map<String, IPlugin> loadPluginMap(@NonNull final Context context,
            @NonNull final PluginType type, @Nullable final String onlyForPackage) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(type, "type"); //$NON-NLS-1$

        final Clock clock = Clock.getInstance();
        final long start = clock.getRealTimeMillis();

        final Map<String, IPlugin> result = new HashMap<>();
        for (final ResolveInfo activityResolveInfo : findActivities(context, type,
                onlyForPackage)) {
            final String pluginPackageName = activityResolveInfo.activityInfo.packageName;
            final int pluginVersionCode = getVersionCode(context.getPackageManager(),
                    pluginPackageName);
            final List<ResolveInfo> pluginReceiverInfos = findReceivers(context, type,
                    pluginPackageName);

            Lumberjack
                    .always("Found plug-in %s with package: %s, Activity: %s, BroadcastReceiver: %s, versionCode: %d",
                            type, pluginPackageName, activityResolveInfo, pluginReceiverInfos,
                            pluginVersionCode); //$NON-NLS-1$

            final EnumSet<PluginErrorRegister> errors = checkPluginForErrors(context, type,
                    activityResolveInfo, pluginReceiverInfos);

            // TODO: Some errors are not fatal.
            if (errors.isEmpty()) {
                // TODO: Plugin characteristics should be downloaded from the cloud.
                final String registryName = Plugin.generateRegistryName(pluginPackageName,
                        activityResolveInfo.activityInfo.name);

                final IPlugin plugin = new Plugin(type, pluginPackageName,
                        activityResolveInfo.activityInfo.name,
                        pluginReceiverInfos.get(0).activityInfo.name,
                        pluginVersionCode,
                        new PluginConfiguration(PluginCharacteristics
                                .isBackwardsCompatibilityEnabled(type, registryName),
                                PluginCharacteristics.isRequiresConnectivity(type, registryName),
                                PluginCharacteristics.isDisruptsConnectivity(
                                        type, registryName), PluginCharacteristics.isBuggy(type,
                                registryName), PluginCharacteristics.isDrainsBattery(type,
                                registryName),
                                PluginCharacteristics.isBlacklisted(type, registryName),
                                PluginCharacteristics.getBuiltInAlternative(type, registryName)
                        )
                );
                result.put(plugin.getRegistryName(), plugin);
            } else {
                for (final PluginErrorRegister error : errors) {
                    Lumberjack.always(error.getDeveloperExplanation());
                }
            }
        }

        Lumberjack
                .v("Loaded plug-in map in %d milliseconds",
                        clock.getRealTimeMillis() - start); //$NON-NLS-1$

        return result;
    }

    /**
     * @param context            Application context.
     * @param type               Plug-in type.
     * @param packageToFilterFor Optional package to restrict the search to.
     * @return a set of plug-in Activities that match the given type and package
     * filter. May return an empty collection.
     */
    @NonNull
    @VisibleForTesting
    /* package */ static Collection<ResolveInfo> findActivities(@NonNull final Context context,
            @NonNull final PluginType type, @Nullable final String packageToFilterFor) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(type, "type"); //$NON-NLS-1$

        final PackageManager packageManager = context.getPackageManager();

        final Intent activityIntent = new Intent(type.getActivityIntentAction());
        if (null != packageToFilterFor) {
            activityIntent.setPackage(packageToFilterFor);
        }

        List<ResolveInfo> activities = packageManager.queryIntentActivities(activityIntent,
                0);
        /*
         * Although the documentation for queryIntentActivities says that it will return an empty
         * list, this is not always the case for older API levels.
         */
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            if (null == activities) {
                activities = new ArrayList<>(0);
            }
        }

        assertNotNull(activities, "activities"); //$NON-NLS-1$

        /*
         * Sort so that plug-ins always appear in the same order in the log.
         * While not absolutely necessary, this makes debugging easier.
         */
        Collections.sort(activities, PACKAGE_NAME_COMPARATOR);

        return activities;
    }

    /**
     * @param context            Application context.
     * @param type               Plug-in type.
     * @param packageToFilterFor Optional package to restrict the search to.
     * @return a set of plug-in BroadcastReceivers that match the given type and
     * package filter. May return an empty collection.
     */
    @NonNull
    @VisibleForTesting
    /* package */ static List<ResolveInfo> findReceivers(@NonNull final Context context,
            @NonNull final PluginType type, @Nullable final String packageToFilterFor) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(type, "type"); //$NON-NLS-1$

        final PackageManager packageManager = context.getPackageManager();

        final Intent receiverIntent = new Intent(type.getReceiverIntentAction());
        if (null != packageToFilterFor) {
            receiverIntent.setPackage(packageToFilterFor);
        }

        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(receiverIntent,
                0);
        /*
         * Although the documentation for queryBroadcastReceivers says that it will return an empty
         * list, this is not always the case for older API levels.
         */
        if (!AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            if (null == receivers) {
                receivers = new ArrayList<>(0);
            }
        }

        /*
         * Sort so that plug-ins always appear in the same order in the log.
         * While not absolutely necessary, this makes debugging easier.
         */
        Collections.sort(receivers, PACKAGE_NAME_COMPARATOR);

        return receivers;
    }

    /**
     * @param packageManager Android Package Manager.
     * @param packageName    Name of the package to retrieve the versionCode of.
     * @return The versionCode of {@code packageName} or -1 if the versionCode
     * cannot be read.
     */
    /*
     * Note: There is a slight TOCTTOU error in that a plug-in package could be
     * found, then uninstalled prior to reading the versionCode. Similarly, the
     * versionCode could change. This code doesn't worry about that. Instead,
     * the registry should detect Intents broadcast when packages change,
     * causing a reload with the corrected info.
     */
    public static int getVersionCode(@NonNull final PackageManager packageManager,
            @NonNull final String packageName) {
        assertNotNull(packageManager, "packageManager"); //$NON-NLS-1$
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            /*
             * This should only happen if the package is uninstalled right after
             * being detected.
             */
            Lumberjack.w("Error reading versionCode%s", e); //$NON-NLS-1$
        }

        final int versionCode;
        if (null != packageInfo) {
            versionCode = packageInfo.versionCode;
        } else {
            versionCode = -1;
        }

        return versionCode;
    }

    /**
     * Helper method to validate a plug-in.
     *
     * @param context              Application context.
     * @param type                 Plug-in type.
     * @param activityResolveInfo  ResolveInfo for the plug-in's Activity.
     * @param receiverResolveInfos ResolveInfo for the plug-in's
     *                             BroadcastReceivers.
     * @return Set of errors detected in the plug-in. If the plug-in has no
     * errors, then it is valid.
     */
    @VisibleForTesting
    /* package */ static EnumSet<PluginErrorRegister> checkPluginForErrors(
            @NonNull final Context context,
            @NonNull final PluginType type,
            @NonNull final ResolveInfo activityResolveInfo,
            @NonNull final List<ResolveInfo> receiverResolveInfos) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotNull(activityResolveInfo, "activityResolveInfo"); //$NON-NLS-1$
        assertNotNull(receiverResolveInfos, "receiverResolveInfos"); //$NON-NLS-1$

        final EnumSet<PluginErrorRegister> errors = EnumSet.noneOf(PluginErrorRegister.class);

        if (!isTargetSdkCorrect(context, activityResolveInfo)) {
            Lumberjack
                    .always("WARNING: %s targetSdkVersion is older than %s",
                            activityResolveInfo.activityInfo.packageName,
                            context.getPackageName()); //$NON-NLS-1$
            // Do not block plug-ins running in compatibility mode
        }

        if (!isInstallLocationCorrect(context, activityResolveInfo)) {
            errors.add(PluginErrorRegister.INSTALL_LOCATION_BAD);
        }

        if (!isApplicationEnabled(activityResolveInfo)) {
            errors.add(PluginErrorRegister.APPLICATION_NOT_ENABLED);
        }

        if (!isComponentEnabled(activityResolveInfo)) {
            errors.add(PluginErrorRegister.ACTIVITY_NOT_ENABLED);
        }

        if (!isComponentExported(activityResolveInfo)) {
            errors.add(PluginErrorRegister.ACTIVITY_NOT_EXPORTED);
        }

        if (!isComponentPermissionGranted(context, activityResolveInfo)) {
            errors.add(PluginErrorRegister.ACTIVITY_REQUIRES_PERMISSION);
        }

        if (1 == receiverResolveInfos.size()) {
            final ResolveInfo receiverResolveInfo = receiverResolveInfos.get(0);
            if (!isComponentEnabled(receiverResolveInfo)) {
                errors.add(PluginErrorRegister.RECEIVER_NOT_ENABLED);
            }

            if (!isComponentExported(receiverResolveInfo)) {
                errors.add(PluginErrorRegister.RECEIVER_NOT_EXPORTED);
            }

            if (!isComponentPermissionGranted(context, receiverResolveInfo)) {
                errors.add(PluginErrorRegister.RECEIVER_REQUIRES_PERMISSION);
            }
        } else if (2 >= receiverResolveInfos.size()) {
            errors.add(PluginErrorRegister.RECEIVER_DUPLICATE);
        } else {
            errors.add(PluginErrorRegister.MISSING_RECEIVER);
        }

        return errors;
    }

    @VisibleForTesting
    /* package */ static boolean isInstallLocationCorrect(@NonNull final Context context,
            @NonNull final ResolveInfo resolveInfo) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(resolveInfo, "resolveInfo"); //$NON-NLS-1$

        boolean isValid = true;

        if (ApplicationInfo.FLAG_EXTERNAL_STORAGE == (resolveInfo.activityInfo.applicationInfo.flags
                & ApplicationInfo.FLAG_EXTERNAL_STORAGE)) {

            /*
             * Note: some custom ROMs allow apps to be moved to external storage
             * despite the AndroidManifest install location.
             */
            isValid = false;
        }

        try {
            final com.twofortyfouram.locale.sdk.host.internal.InstallLocation installLocation
                    = com.twofortyfouram.locale.sdk.host.internal.InstallLocation
                    .getManifestInstallLocation(
                            context, resolveInfo.activityInfo.packageName);

            if (com.twofortyfouram.locale.sdk.host.internal.InstallLocation.auto == installLocation
                    || com.twofortyfouram.locale.sdk.host.internal.InstallLocation.preferExternal
                    == installLocation) {
                isValid = false;
            }
        } catch (final NameNotFoundException | IOException | XmlPullParserException e) {
            // Was it uninstalled?
        }

        return isValid;
    }

    /**
     * Helper to determine whether a plug-in is running in compatibility mode.
     *
     * @param context Application context
     * @param info    The resolved info for the plug-in package.
     * @return True if the plug-in is targeting at least the same SDK version of
     * the host application.
     */
    @VisibleForTesting
    /* package */ static boolean isTargetSdkCorrect(@NonNull final Context context,
            @NonNull final ResolveInfo info) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(info, "info"); //$NON-NLS-1$

        boolean isTargetSdkCorrect = true;

        final int pluginTargetSdk = info.activityInfo.applicationInfo.targetSdkVersion;
        final int localeTargetSdk = context.getApplicationInfo().targetSdkVersion;
        if (pluginTargetSdk < localeTargetSdk) {
            isTargetSdkCorrect = false;
        }

        return isTargetSdkCorrect;
    }

    /**
     * @param info plug-in's resolved info.
     * @return True if the application is enabled. False if the application is
     * disabled.
     */
    @VisibleForTesting
    /* package */ static boolean isApplicationEnabled(@NonNull final ResolveInfo info) {
        assertNotNull(info, "info"); //$NON-NLS-1$

        return info.activityInfo.applicationInfo.enabled;
    }

    /**
     * @param info plug-in's resolved info.
     * @return True if the Activity is enabled. False if the Activity is
     * disabled.
     */
    @VisibleForTesting
    /* package */ static boolean isComponentEnabled(@NonNull final ResolveInfo info) {
        assertNotNull(info, "info"); //$NON-NLS-1$

        return info.activityInfo.enabled;
    }

    /**
     * @param info Component's resolved info.
     * @return True if the component is exported. False if the component is not
     * exported.
     */
    @VisibleForTesting
    /* package */ static boolean isComponentExported(@NonNull final ResolveInfo info) {
        assertNotNull(info, "info"); //$NON-NLS-1$

        return info.activityInfo.exported;
    }

    /**
     * @param info Component's resolved info.
     * @return True if the component does not require a permission the host does
     * not have.
     */
    @VisibleForTesting
    /* package */ static boolean isComponentPermissionGranted(@NonNull final Context context,
            @NonNull final ResolveInfo info) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(info, "info"); //$NON-NLS-1$

        final boolean isPermissionGrantedToMe;
        if (null != info.activityInfo.permission) {
            isPermissionGrantedToMe = PermissionCompat.PermissionStatus.GRANTED == PermissionCompat
                    .getPermissionStatus(context,
                            info.activityInfo.permission);
        } else {
            isPermissionGrantedToMe = true;
        }

        return isPermissionGrantedToMe;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginPackageScanner() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
