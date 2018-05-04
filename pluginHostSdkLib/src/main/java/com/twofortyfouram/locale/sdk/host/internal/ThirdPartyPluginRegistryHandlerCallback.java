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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.VisibleForTesting;

import com.twofortyfouram.annotation.Slow;
import com.twofortyfouram.annotation.Slow.Speed;
import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.ContextUtil;
import com.twofortyfouram.spackle.HandlerThreadFactory;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.Immutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Handler Callback to process loading and monitoring for changes to Locale plug-ins.
 * <p/>
 * After the handler has been initialized, it must be destroyed.
 * <p/>
 * After the Handler is initialized, the public API to retrieve the loaded map
 * of plug-ins is by calling {@link #getConditions()} and {@link #getSettings()}.
 */
public final class ThirdPartyPluginRegistryHandlerCallback implements Handler.Callback {
    /*
     * Design notes: The helper methods such as init(), onPackageAdded(),
     * onPackageChanged(), and onPackageRemoved() only modify the private
     * internal state of this object. The global state of the registry
     * is only modified when messages are processed by the handleMessage()
     * method of the callback. This design allows for easier unit testing of the
     * implementation details of the callback object.
     *
     * Also note that there are two handleInit() methods—one for setting up initial state and the other
     * for registering observers.  This breakdown allows for easier automated testing.
     */

    /**
     * Message to initialize the Handler.
     * <p/>
     * {@link Message#obj} is a {@link InitObj}.  The Handler is the same Handler running
     * this callback.  The latch will be decremented after loading completes.
     */
    public static final int MESSAGE_INIT = 0;

    /**
     * Message for a package being added.
     * <p/>
     * {@link Message#obj} is a {@code String} representing the package name.
     */
    private static final int MESSAGE_PACKAGE_ADDED = 1;

    /**
     * Message for a package being removed.
     * <p/>
     * {@link Message#obj} is a {@code String} representing the package name.
     */
    private static final int MESSAGE_PACKAGE_REMOVED = 2;

    /**
     * Message for a package changing.
     * <p/>
     * {@link Message#obj} is a {@code String} representing the package name.
     */
    private static final int MESSAGE_PACKAGE_CHANGED = 3;

    /**
     * Empty message to shut down the handler.
     */
    public static final int MESSAGE_DESTROY = 4;

    /**
     * Application context
     */
    @NonNull
    private final Context mContext;

    /**
     * Intent broadcast when the registry changes
     */
    @NonNull
    private final Intent mRegistryReloadedIntent;

    /**
     * Permission for securing {@link #mRegistryReloadedIntent}.
     */
    @NonNull
    private final String mRegistryReloadedPermission;

    /**
     * Map of the registry name to {@link Plugin} for all Conditions.
     * <p/>
     * This field is lazily initialized. This map is mutable.
     */
    @Nullable
    @VisibleForTesting
    /* package */ Map<String, Plugin> mMutableConditionMap = null;

    /**
     * Map of the registry name to {@link Plugin} for all Conditions.
     * <p/>
     * This field is lazily initialized. This map is mutable.
     */
    @Nullable
    @VisibleForTesting
    /* package */ Map<String, Plugin> mMutableSettingMap = null;

    /**
     * Map of the registry name to {@link Plugin} for all Conditions.
     * <p/>
     * This field is lazily initialized. Once this field is initialized, it will
     * point to an immutable map (e.g. {@link Collections#unmodifiableMap(Map)}.
     */
    @Nullable
    private volatile Map<String, Plugin> mImmutableConditionMap = null;

    /**
     * Map of the registry name to {@link Plugin} for all Settings.
     * <p/>
     * This field is lazily initialized. Once this field is initialized, it will
     * point to an immutable map (e.g. {@link Collections#unmodifiableMap(Map)}.
     */
    @Nullable
    private volatile Map<String, Plugin> mImmutableSettingMap = null;

    /**
     * Handler thread where the BroadcastReceiver runs.
     */
    @Nullable
    private HandlerThread mReceiverHandlerThread = null;

    /**
     * Receiver to detect changes to installed plug-ins.
     */
    /*
     * It is very important to run the receiver on a separate thread. Because
     * loading the registry can be slow (greater than 10 seconds), it is
     * possible that the PluginRegistryHandler could be blocked for a
     * while. We don't want the BroadcastReceiver to be blocked waiting for the
     * PluginRegistryHandler, as then Android would think the app was not
     * responding.
     */
    @NonNull
    private BroadcastReceiver mReceiver = null;

    /**
     * Construct a new {@link ThirdPartyPluginRegistryHandlerCallback}.
     *
     * @param context                Application context.
     * @param notificationAction     Intent action to broadcast when the registry changes.
     * @param notificationPermission Permission to guard {@code notificationAction}.
     */
    public ThirdPartyPluginRegistryHandlerCallback(
            @NonNull final Context context,
            @NonNull final String notificationAction,
            @NonNull final String notificationPermission) {
        assertNotNull(context, "context"); //$NON-NLS-1$
        assertNotNull(notificationAction, "notificationAction"); //$NON-NLS-1$
        assertNotNull(notificationPermission, "notificationPermission"); //$NON-NLS-1$

        mContext = ContextUtil.cleanContext(context);

        mRegistryReloadedIntent = new Intent(notificationAction);
        mRegistryReloadedIntent.setPackage(context.getPackageName());
        // Nasty workaround for Intent queue flooding.
        mRegistryReloadedIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mRegistryReloadedPermission = notificationPermission;
    }

    @Override
    public boolean handleMessage(@NonNull final Message msg) {
        Lumberjack.v("Got what=%s %s", nameThatMessage(msg.what), msg); //$NON-NLS-1$

        switch (msg.what) {
            case MESSAGE_INIT: {
                final InitObj initObj = (InitObj) msg.obj;

                try {
                    handleInit(initObj.getHandler());
                } finally {
                    initObj.getCountDownLatch().countDown();
                }

                break;
            }
            case MESSAGE_PACKAGE_ADDED: {
                final String packageName = (String) msg.obj;

                processPackageResult(handlePackageAdded(packageName));

                break;
            }
            case MESSAGE_PACKAGE_CHANGED: {
                final String packageName = (String) msg.obj;

                processPackageResult(handlePackageChanged(packageName));

                break;
            }
            case MESSAGE_PACKAGE_REMOVED: {
                final String packageName = (String) msg.obj;

                processPackageResult(handlePackageRemoved(packageName));

                break;
            }
            case MESSAGE_DESTROY: {
                handleDestroy();

                break;
            }
            default: {
                throw new AssertionError(
                        Lumberjack.formatMessage("Unrecognized what=%d", msg.what));
            }
        }

        return true;
    }

    /**
     * @param what Message what
     * @return Human-readable string of {@code what}.  Useful for debugging.
     */
    @NonNull
    private static String nameThatMessage(final int what) {
        switch (what) {
            case MESSAGE_INIT: {
                return "MESSAGE_INIT"; //$NON-NLS
            }
            case MESSAGE_PACKAGE_ADDED: {
                return "MESSAGE_PACKAGE_ADDED"; //$NON-NLS
            }
            case MESSAGE_PACKAGE_REMOVED: {
                return "MESSAGE_PACKAGE_REMOVED"; //$NON-NLS
            }
            case MESSAGE_PACKAGE_CHANGED: {
                return "MESSAGE_PACKAGE_CHANGED"; //$NON-NLS
            }
            case MESSAGE_DESTROY: {
                return "MESSAGE_DESTROY"; //$NON-NLS
            }
            default: {
                return "UNKNOWN"; //$NON-NLS
            }
        }
    }

    /**
     * Initially loads the registry.
     *
     * @see #MESSAGE_INIT
     * @see #handleDestroy()
     */
    private void handleInit(@NonNull final Handler callbackHandler) {
        assertNotNull(callbackHandler, "callbackHandler"); //$NON-NLS

        final IntentFilter packageFilter = new IntentFilter();
        {
            packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            packageFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            packageFilter.addDataScheme("package"); //$NON-NLS-1$
        }

        final IntentFilter externalStorageFilter = new IntentFilter();
        {
            externalStorageFilter
                    .addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
            externalStorageFilter
                    .addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        }

        mReceiver = new RegistryReceiver(callbackHandler);

        mReceiverHandlerThread = HandlerThreadFactory.newHandlerThread(
                RegistryReceiver.class.getName(),
                HandlerThreadFactory.ThreadPriority.BACKGROUND);

        final Handler receiverHandler = new Handler(mReceiverHandlerThread.getLooper());

        mContext.registerReceiver(mReceiver, packageFilter, null, receiverHandler);
        mContext.registerReceiver(mReceiver, externalStorageFilter, null, receiverHandler);

        handleInit();
    }

    /**
     * Helper for {@link #handleInit(Handler)}.  This method is exposed for testing and destroy
     * does not need to be called after calling this method.
     */
    @Slow(Speed.SECONDS)
    @VisibleForTesting
    /* package */void handleInit() {
        mMutableConditionMap = PluginPackageScanner.loadPluginMap(mContext,
                PluginType.CONDITION, null);
        mMutableSettingMap = PluginPackageScanner.loadPluginMap(mContext,
                PluginType.SETTING, null);

        setConditions();
        setSettings();

        sendBroadcast();
    }

    /**
     * Destroys the registry.
     *
     * @see #handleInit(Handler)
     * @see #MESSAGE_DESTROY
     */
    @VisibleForTesting
    /* package */void handleDestroy() {
        mContext.unregisterReceiver(mReceiver);
        mReceiverHandlerThread.quit();
    }

    /**
     * Call when a package is removed to scan and see if the registry should be
     * changed.
     *
     * @param packageName String name of the package that was removed.
     * @return A {@link PackageResult}.
     * @effects if the result is something other than
     * {@link PackageResult#NOTHING_CHANGED}, then the internal state
     * of this callback object was mutated.
     * @see #MESSAGE_PACKAGE_REMOVED
     */
    @NonNull
    @VisibleForTesting
    /* package */PackageResult handlePackageRemoved(@NonNull final String packageName) {
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        final boolean conditionsChanged = isPluginRemoved(PluginType.CONDITION, packageName,
                PluginPackageScanner.loadPluginMap(mContext, PluginType.CONDITION, packageName));
        final boolean settingsChanged = isPluginRemoved(PluginType.SETTING, packageName,
                PluginPackageScanner.loadPluginMap(mContext, PluginType.SETTING, packageName));

        return PackageResult.get(conditionsChanged, settingsChanged);
    }

    /**
     * Call when a package is added to scan and see if the registry should be
     * changed.
     *
     * @param packageName String name of the package that was added.
     * @return A {@link PackageResult}.
     * @effects if the result is something other than
     * {@link PackageResult#NOTHING_CHANGED}, then the internal state
     * of this callback object was mutated.
     * @see #MESSAGE_PACKAGE_ADDED
     */
    @NonNull
    @VisibleForTesting
    /* package */PackageResult handlePackageAdded(@NonNull final String packageName) {
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        final boolean conditionsChanged = isPluginAdded(PluginType.CONDITION,
                PluginPackageScanner.loadPluginMap(mContext, PluginType.CONDITION, packageName));
        final boolean settingsChanged = isPluginAdded(PluginType.SETTING,
                PluginPackageScanner.loadPluginMap(mContext, PluginType.SETTING, packageName));

        return PackageResult.get(conditionsChanged, settingsChanged);
    }

    /**
     * Call when a package is changed to scan and see if the registry should be
     * changed.
     *
     * @param packageName String name of the package that was changed.
     * @return A {@link PackageResult}.
     * @effects if the result is something other than
     * {@link PackageResult#NOTHING_CHANGED}, then the internal state
     * of this callback object was mutated.
     * @see #MESSAGE_PACKAGE_CHANGED
     */
    @NonNull
    @VisibleForTesting
    /* package */PackageResult handlePackageChanged(@NonNull final String packageName) {
        assertNotNull(packageName, "packageName"); //$NON-NLS-1$

        final Map<String, Plugin> scannedConditions = PluginPackageScanner.loadPluginMap(mContext,
                PluginType.CONDITION, packageName);
        final Map<String, Plugin> scannedSettings = PluginPackageScanner.loadPluginMap(mContext,
                PluginType.SETTING, packageName);

        boolean conditionsChanged = isPluginRemoved(PluginType.CONDITION, packageName,
                scannedConditions)
                || isPluginAdded(PluginType.CONDITION, scannedConditions);
        final boolean settingsChanged =
                isPluginRemoved(PluginType.SETTING, packageName, scannedSettings)
                        || isPluginAdded(PluginType.SETTING, scannedSettings);

        /*
         * Look for updated plug-in conditions so that their processes can be
         * re-launched by a detected registry change. Note that this looks for a
         * versionCode change, so no change will be detected if a package is
         * merely reinstalled. This is only necessary for conditions, because
         * they may need to reschedule alarms or restart services after being
         * upgraded. It is not necessary to relaunch settings, because they do
         * not typically have alarms or services that need to be restarted.
         */
        for (final Plugin newCondition : scannedConditions.values()) {
            if (mImmutableConditionMap.containsKey(newCondition
                    .getRegistryName())) {
                final int oldConditionVersion = mImmutableConditionMap
                        .get(newCondition.getRegistryName()).getVersionCode();
                final int newConditionVersion = newCondition.getVersionCode();
                if (oldConditionVersion != newConditionVersion) {
                    Lumberjack
                            .v("Package %s changed from versionCode=%d to versionCode=%d",
                                    //$NON-NLS-1$
                                    packageName, oldConditionVersion, newConditionVersion);

                    conditionsChanged = true;
                }
            }
        }

        return PackageResult.get(conditionsChanged, settingsChanged);
    }

    /**
     * Scans {@code packageName} for new plug-ins.
     *
     * @param type           Plug-in type.
     * @param scannedPlugins The plug-ins that were scanned.
     * @return True if new plug-ins were added. False if no new plug-ins were
     * added.
     * @effects The mutable map for {@code type} will be mutated if this method
     * returns true.
     */
    private boolean isPluginAdded(@NonNull final PluginType type,
            @NonNull final Map<String, Plugin> scannedPlugins) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotNull(scannedPlugins, "scannedPlugins"); //$NON-NLS-1$

        final Map<String, Plugin> oldPlugins = getMutablePluginMap(type);

        boolean isChanged = false;
        if (!oldPlugins.keySet().containsAll(scannedPlugins.keySet())) {
            Lumberjack
                    .v("New plug-ins detected: %s", scannedPlugins); //$NON-NLS-1$
            oldPlugins.putAll(scannedPlugins);

            isChanged = true;
        }

        return isChanged;
    }

    /**
     * Scans {@code packageName} for removed plug-ins.
     *
     * @param type           Plug-in type.
     * @param scannedPlugins The plug-ins that were scanned.
     * @return True if plug-ins were removed. False if no plug-ins were removed.
     * @effects The mutable map for {@code type} will be mutated if this method
     * returns true.
     */
    private boolean isPluginRemoved(@NonNull final PluginType type,
            @NonNull final String packageName,
            @NonNull final Map<String, Plugin> scannedPlugins) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotNull(scannedPlugins, "scannedPlugins"); //$NON-NLS-1$

        boolean isChanged = false;
        final Iterator<Plugin> oldPluginsIterator = getMutablePluginMap(type).values()
                .iterator();
        while (oldPluginsIterator.hasNext()) {
            final Plugin plugin = oldPluginsIterator.next();

            if (packageName.equals(plugin.getPackageName())) {
                if (!scannedPlugins.containsKey(plugin.getRegistryName())) {
                    Lumberjack
                            .v("Removing plug-in %s %s", type,
                                    plugin.getRegistryName()); //$NON-NLS-1$

                    oldPluginsIterator.remove();
                    isChanged = true;
                }
            }
        }

        return isChanged;
    }

    @NonNull
    @Size(min = 0)
    private Map<String, Plugin> getMutablePluginMap(@NonNull final PluginType type) {
        assertNotNull(type, "type"); //$NON-NLS-1$

        final Map<String, Plugin> pluginMap;
        switch (type) {
            case CONDITION: {
                pluginMap = mMutableConditionMap;
                break;
            }
            case SETTING: {
                pluginMap = mMutableSettingMap;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return pluginMap;
    }

    /**
     * Processes a {@link PackageResult} return value.
     *
     * @param result value to process.
     */
    private void processPackageResult(@NonNull final PackageResult result) {
        assertNotNull(result, "result"); //$NON-NLS-1$

        switch (result) {
            case NOTHING_CHANGED: {
                break;
            }
            case CONDITIONS_AND_SETTINGS_CHANGED: {
                setConditions();
                setSettings();

                sendBroadcast();

                break;
            }
            case CONDITIONS_CHANGED: {
                setConditions();

                sendBroadcast();

                break;
            }
            case SETTINGS_CHANGED: {
                setSettings();

                sendBroadcast();
                break;
            }
        }
    }

    /**
     * Sends a broadcast to notify clients that the registry changed.
     */
    private void sendBroadcast() {
        mContext.sendBroadcast(mRegistryReloadedIntent, mRegistryReloadedPermission);
    }

    /**
     * Clients MUST NOT attempt to modify the map.
     *
     * @return A snapshot of plug-in conditions. This method will return
     * {@code null} until the handler completes initialization.
     */
    @Nullable
    @Size(min = 0)
    public Map<String, Plugin> getConditions() {
        return mImmutableConditionMap;
    }

    /**
     * Clients MUST NOT attempt to modify the map.
     *
     * @return A snapshot of plug-in settings. This method will return
     * {@code null} until the handler completes initialization.
     */
    @Nullable
    @Size(min = 0)
    public Map<String, Plugin> getSettings() {
        return mImmutableSettingMap;
    }

    /**
     * Mutate the global registry state with the latest changes to conditions.
     *
     * @effects {@link #mImmutableConditionMap} will be
     * reassigned to a new object after this method completes.
     */
    private void setConditions() {
        /*
         * In addition to making it immutable, making a copy is also required.
         * Otherwise if a copy weren't made, changes to the
         * mMutableConditionMap would be reflected by the
         * mImmutableConditionMap.
         */
        mImmutableConditionMap = Collections
                .unmodifiableMap(new HashMap<>(
                        mMutableConditionMap));
    }

    /**
     * Mutate the global registry state with the latest changes to settings.
     *
     * @effects {@link #mImmutableSettingMap} will be
     * reassigned to a new object after this method completes.
     */
    private void setSettings() {
        /*
         * In addition to making it immutable, making a copy is also required.
         * Otherwise if a copy weren't made, changes to the
         * mMutableSettingMap would be reflected by the
         * mImmutableSettingMap.
         */
        mImmutableSettingMap = Collections
                .unmodifiableMap(new HashMap<>(
                        mMutableSettingMap));
    }

    /**
     * Dynamically registered BroadcastReceiver to keep a fresh view of what
     * plug-ins are installed.
     * <p/>
     * Expected {@code Intent}s are:
     * <ul>
     * <li>{@link Intent#ACTION_PACKAGE_ADDED}.</li>
     * <li>{@link Intent#ACTION_PACKAGE_REMOVED}.</li>
     * <li>{@link Intent#ACTION_EXTERNAL_APPLICATIONS_AVAILABLE}.</li>
     * <li>{@link Intent#ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE}.</li>
     * <li>{@link Intent#ACTION_PACKAGE_CHANGED}.</li>
     * </ul>
     */
    private static final class RegistryReceiver extends BroadcastReceiver {

        @NonNull
        private final Handler mHandler;

        public RegistryReceiver(@NonNull final Handler callbackHandler) {
            assertNotNull(callbackHandler, "callbackHandler"); //$NON-NLS

            mHandler = callbackHandler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            /*
             * THREADING: This runs on mReceiverHandlerThread.
             */

            if (BundleScrubber.scrub(intent)) {
                return;
            }

            Lumberjack.v("Received %s", intent); //$NON-NLS-1$

            final String action = intent.getAction();

            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                final String changedPackage = getChangedPackage(intent);

                if (!isReplacing(intent)) {
                    sendPackageAddedMessage(changedPackage);
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED
                    .equals(action)) {
                final String changedPackage = getChangedPackage(intent);

                if (!isReplacing(intent)) {
                    sendPackageRemovedMessage(changedPackage);
                }
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action) || Intent
                    .ACTION_PACKAGE_CHANGED.equals(action)) {
                final String changedPackage = getChangedPackage(intent);

                sendPackageChangedMessage(changedPackage);
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action) || Intent
                    .ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                final String[] changedPackages = intent
                        .getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);

                if (null != changedPackages) {
                    for (final String packageName : changedPackages) {
                        sendPackageChangedMessage(packageName);
                    }
                }
            }
        }

        /**
         * @param intent A package change Intent.
         * @return True if {@code intent} contains
         * {@link Intent#EXTRA_REPLACING} with a value of true.
         */
        private boolean isReplacing(@NonNull final Intent intent) {
            final boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            return isReplacing;
        }

        @Nullable
        private String getChangedPackage(@NonNull final Intent intent) {
            String changedPackage = null;

            final Uri data = intent.getData();

            if (null != data) {
                changedPackage = data.getSchemeSpecificPart();
            }

            return changedPackage;
        }

        private void sendPackageAddedMessage(@Nullable final String packageName) {
            if (null != packageName) {
                mHandler.sendMessage(mHandler.obtainMessage(
                        MESSAGE_PACKAGE_ADDED, packageName));
            }
        }

        private void sendPackageRemovedMessage(@Nullable final String packageName) {
            if (null != packageName) {
                mHandler.sendMessage(mHandler.obtainMessage(
                        MESSAGE_PACKAGE_REMOVED, packageName));
            }
        }

        private void sendPackageChangedMessage(@Nullable final String packageName) {
            if (null != packageName) {
                mHandler.sendMessage(mHandler.obtainMessage(
                        MESSAGE_PACKAGE_CHANGED, packageName));
            }
        }
    }

    /**
     * Container for obj in {@link ThirdPartyPluginRegistryHandlerCallback#MESSAGE_INIT}
     */
    @Immutable
    public static final class InitObj {

        @NonNull
        private final Handler mHandler;

        @NonNull
        private final CountDownLatch mCountDownLatch;

        public InitObj(@NonNull final Handler handler,
                @NonNull final CountDownLatch countDownLatch) {
            assertNotNull(handler, "handler"); //$NON-NLS
            assertNotNull(countDownLatch, "countDownLatch"); //$NON-NLS

            mHandler = handler;
            mCountDownLatch = countDownLatch;
        }

        @NonNull
        public Handler getHandler() {
            return mHandler;
        }

        @NonNull
        public CountDownLatch getCountDownLatch() {
            return mCountDownLatch;
        }
    }
}
