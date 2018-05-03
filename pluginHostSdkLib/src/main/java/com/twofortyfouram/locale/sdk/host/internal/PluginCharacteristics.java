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

package com.twofortyfouram.locale.sdk.host.internal;

import android.os.Build;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.sdk.host.model.Plugin;
import com.twofortyfouram.locale.sdk.host.model.PluginType;
import com.twofortyfouram.spackle.AndroidSdkVersion;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.twofortyfouram.assertion.Assertions.assertNotEmpty;
import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Tracks various characteristics of specific plug-ins, in order to improve user experience and
 * compatibility.
 */
/*
 * Locale downloads these from the cloud, so that they can be dynamically
 * updated. For the initial version of the open source library, we rely on a hard-coded
 * configuration.  A future version should move this into configuration retrieved from the cloud.
 * Hence, this class is deprecated by design.
 */
@ThreadSafe
public final class PluginCharacteristics {

    @NonNull
    private static final Set<String> CONDITIONS_REQUIRING_BACKWARDS_COMPATIBILITY = Collections
            .unmodifiableSet(getConditionsRequiringBackwardsCompatibility());

    @NonNull
    private static final Set<String> SETTINGS_REQUIRING_BACKWARDS_COMPATIBILITY = Collections
            .unmodifiableSet(getSettingsRequiringBackwardsCompatibility());

    @NonNull
    private static final Set<String> CONDITIONS_THAT_DRAIN_BATTERY = Collections
            .unmodifiableSet(getConditionsThatDrainBattery());

    @NonNull
    private static final Set<String> SETTINGS_THAT_DRAIN_BATTERY = Collections
            .unmodifiableSet(getSettingsThatDrainBattery());

    @NonNull
    private static final Set<String> CONDITIONS_THAT_DISRUPT_CONNECTIVITY = Collections
            .unmodifiableSet(getConditionsThatDisruptConnectivity());

    @NonNull
    private static final Set<String> SETTINGS_THAT_DISRUPT_CONNECTIVITY = Collections
            .unmodifiableSet(getSettingsThatDisruptConnectivity());

    @NonNull
    private static final Set<String> CONDITIONS_THAT_REQUIRE_CONNECTIVITY = Collections
            .unmodifiableSet(getConditionsThatRequireConnectivity());

    @NonNull
    private static final Set<String> SETTINGS_THAT_REQUIRE_CONNECTIVITY = Collections
            .unmodifiableSet(getSettingsThatRequireConnectivity());

    @NonNull
    private static final Set<String> CONDITIONS_THAT_ARE_BUGGY = Collections
            .unmodifiableSet(getConditionsThatAreBuggy());

    @NonNull
    private static final Set<String> SETTINGS_THAT_ARE_BUGGY = Collections
            .unmodifiableSet(getSettingsThatAreBuggy());

    @NonNull
    private static final Set<String> CONDITIONS_THAT_ARE_BLACKLISTED = Collections
            .unmodifiableSet(getConditionsThatAreBlacklisted());

    @NonNull
    private static final Set<String> SETTINGS_THAT_ARE_BLACKLISTED = Collections
            .unmodifiableSet(getSettingsThatAreBlacklisted());

    @NonNull
    private static final Map<String, Set<String>> CONDITIONS_THAT_HAVE_ALTERNATIVES = Collections
            .unmodifiableMap(getConditionsThatHaveAlternatives());

    @NonNull
    private static final Map<String, Set<String>> SETTINGS_THAT_HAVE_ALTERNATIVES = Collections
            .unmodifiableMap(getSettingsThatHaveAlternatives());

    @NonNull
    private static Set<String> getConditionsRequiringBackwardsCompatibility() {
        final Set<String> set = new HashSet<String>();

        set.add(Plugin.generateRegistryName("com.DriftingAway.Skim", //$NON-NLS-1$
                "com.DriftingAway.Skim.EditActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getSettingsRequiringBackwardsCompatibility() {
    /*
     * This represents a "hall of shame", as two forty four a.m. LLC has
     * contacted all of these developers to update their plug-ins but no reply
     * was received. Many of these plug-ins are no longer even available on the
     * Google Play Store, yet users may still have them installed on their
     * devices.
     */

        final Set<String> set = new HashSet<String>();

        set.add(Plugin.generateRegistryName("com.dattasmoon.gtalkcontrol", //$NON-NLS-1$
                "com.dattasmoon.gtalkcontrol.LocaleEdit")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.droidmunkey.localePlaySound", //$NON-NLS-1$
                "com.droidmunkey.localePlaySound.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.droidmunkey.LocaleSpeakerphone", //$NON-NLS-1$
                "com.droidmunkey.LocaleSpeakerphone.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.DroidMunkey.localeTextToSpeech", //$NON-NLS-1$
                "com.DroidMunkey.localeTextToSpeech.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.DroidMunkey.LocaleTimer", //$NON-NLS-1$
                "com.DroidMunkey.LocaleTimer.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.DroidMunkey.localeVariables", //$NON-NLS-1$
                "com.DroidMunkey.localeVariables.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.DroidMunkey.LocaleVibrate", //$NON-NLS-1$
                "com.DroidMunkey.LocaleVibrate.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.google.ase", //$NON-NLS-1$
                "com.google.ase.locale.LocalePlugin")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.googlecode.android_scripting", //$NON-NLS-1$
                "com.googlecode.android_scripting.LocalePlugin")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.handyandy.whoisit", //$NON-NLS-1$
                "com.handyandy.whoisit.TaskerActivateProfile")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.handydroid.openwatch.locale.button", //$NON-NLS-1$
                "org.handydroid.openwatch.locale.button.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.handydroid.openwatch.locale.graphic", //$NON-NLS-1$
                "org.handydroid.openwatch.locale.graphic.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.handydroid.openwatch.locale.message", //$NON-NLS-1$
                "org.handydroid.openwatch.locale.message.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.levelup.foxyring", //$NON-NLS-1$
                "com.levelup.foxyring.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mariobialos.LocaleDialPlugIn", //$NON-NLS-1$
                "com.mariobialos.LocaleDialPlugIn.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mariobialos.LocaleHapticPlugIn", //$NON-NLS-1$
                "com.mariobialos.LocaleHapticPlugIn.EditActivity")); //$NON-NLS-1$

        set.add(Plugin
                .generateRegistryName("com.mariobialos.LocalePrevLocationPlugIn", //$NON-NLS-1$
                        "com.mariobialos.LocalePrevLocationPlugIn.EditActivity")); //$NON-NLS-1$

        set.add(Plugin
                .generateRegistryName("com.mariobialos.LocaleRotateScreenPlugIn", //$NON-NLS-1$
                        "com.mariobialos.LocaleRotateScreenPlugIn.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mariobialos.LocaleVoiceCallPlugIn", //$NON-NLS-1$
                "com.mariobialos.LocaleVoiceCallPlugIn.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mb.locale.cardock", //$NON-NLS-1$
                "com.mb.locale.cardock.LocaleEditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mooapps.autolock", //$NON-NLS-1$
                "com.mooapps.autolock.LocaleSettings")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.mooapps.autolock2", //$NON-NLS-1$
                "com.mooapps.autolock2.LocaleSettings")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.olib.locplug.scenemode", //$NON-NLS-1$
                "com.olib.locplug.scenemode.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.olib.locplug.scenemodepro", //$NON-NLS-1$
                "com.olib.locplug.scenemodepro.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.smartideas.handsfreesmsdemo", //$NON-NLS-1$
                "com.smartideas.handsfreesmsdemo.locale.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.smartideas.handsfreesms", //$NON-NLS-1$
                "com.smartideas.handsfreesms.locale.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.splunchy.android.speakingringtone", //$NON-NLS-1$
                "com.splunchy.android.speakingringtone.LocaleSettingEditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.steelgirder.LocaleSendEmailPlugin", //$NON-NLS-1$
                "com.steelgirder.LocaleSendEmailPlugin.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.steelgirder.LocaleWOLPlugin", //$NON-NLS-1$
                "com.steelgirder.LocaleWOLPlugin.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.steelgirder.LocalePingFMPlugin", //$NON-NLS-1$
                "com.steelgirder.LocalePingFMPlugin.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.sugree.jibjib", //$NON-NLS-1$
                "com.sugree.jibjib.LocaleSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.al.SmartReply.Deluxe", //$NON-NLS-1$
                "com.al.SmartReply.Deluxe.LocaleEditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.al.SmartReply.Pro", //$NON-NLS-1$
                "com.al.SmartReply.Pro.LocaleEditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.pwnwithyourphone.talkingcalendar", //$NON-NLS-1$
                "com.pwnwithyourphone.talkingcalendar.locale.SetupActivity")); //$NON-NLS-1$

        set.add(Plugin
                .generateRegistryName("com.pwnwithyourphone.talkingcalendar.trial", //$NON-NLS-1$
                        "com.pwnwithyourphone.talkingcalendar.locale.SetupActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.tenromans.locale.systemnotification", //$NON-NLS-1$
                "com.tenromans.locale.systemnotification.EditSystemNotificationActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.tenromans.locale.emailsilencer", //$NON-NLS-1$
                "com.tenromans.locale.emailsilencer.EditSystemNotificationActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.twofortyfouram.locale.setting.gps", //$NON-NLS-1$
                "com.twofortyfouram.locale.setting.gps.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.yurivolkov.android.locale_audio_update_notifier",
                //$NON-NLS-1$
                "com.yurivolkov.android.locale_audio_update_notifier.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("de.elmicha.app.LocaleExecute", //$NON-NLS-1$
                "de.elmicha.app.LocaleExecute.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("de.sifl.wolcale", //$NON-NLS-1$
                "de.sifl.wolcale.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("mobi.gearsoft.android.wifisync", //$NON-NLS-1$
                "mobi.gearsoft.android.wifisync.LocaleEditSettings")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("net.andvari.android.notificationsetting", //$NON-NLS-1$
                "net.andvari.android.notificationsetting.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("net.andvari.android.syncsetting", //$NON-NLS-1$
                "net.andvari.android.syncsetting.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.adaptroid.habitats", //$NON-NLS-1$
                "org.adaptroid.habitats.EditLocaleSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.damazio.notifier.locale.notify", //$NON-NLS-1$
                "org.damazio.notifier.locale.notify.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.darune.autowakeonlan", //$NON-NLS-1$
                "org.darune.autowakeonlan.AutoWakeOnLan")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("org.mailboxer.saymyname", //$NON-NLS-1$
                "com.announcify.ui.activity.LocaleActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getConditionsThatDrainBattery() {
        final Set<String> set = new HashSet<String>();

        set.add(Plugin.generateRegistryName("com.DriftingAway.Skim", //$NON-NLS-1$
                "com.DriftingAway.Skim.EditActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getSettingsThatDrainBattery() {
        final Set<String> set = new HashSet<String>();

        return set;
    }

    @NonNull
    private static Set<String> getConditionsThatDisruptConnectivity() {
        final Set<String> set = new HashSet<String>();

        return set;
    }

    @NonNull
    private static Set<String> getSettingsThatDisruptConnectivity() {
        final Set<String> set = new HashSet<String>();

        set.add(Plugin.generateRegistryName("com.codecarpet.apndroid.locale",
                //$NON-NLS-1$
                "com.google.code.apndroid.LocaleActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.kludgenics.locale.superplane",
                //$NON-NLS-1$
                "com.kludgenics.locale.superplane.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.twofortyfouram.locale.setting.airplanemode",
                //$NON-NLS-1$
                "com.twofortyfouram.locale.setting.airplanemode.EditActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.willemstoker.AutoPilot",
                //$NON-NLS-1$
                "com.willemstoker.AutoPilot.EditYourSettingActivity")); //$NON-NLS-1$

        set.add(Plugin.generateRegistryName("com.suttco.locale.net",
                //$NON-NLS-1$
                "com.suttco.locale.net.DataEnabledSettingActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getConditionsThatRequireConnectivity() {
        final Set<String> set = new HashSet<String>();

        set.add(Plugin.generateRegistryName(
                "com.twofortyfouram.locale",
                "com.twofortyfouram.locale.ui.activities.LocationConditionActivity"));

        return set;
    }

    @NonNull
    private static Set<String> getSettingsThatRequireConnectivity() {
        final Set<String> set = new HashSet<String>();

        return set;
    }

    @NonNull
    private static Set<String> getConditionsThatAreBuggy() {
        final Set<String> set = new HashSet<String>();

        // Doesn't correctly request a requery, causing users to complain that Locale isn't working
        // unless they open the app. (Locale forces a query when the UI is launched).
        set.add(Plugin.generateRegistryName("org.acm.steidinger.calendar.localePlugin",
                //$NON-NLS-1$
                "org.acm.steidinger.calendar.localePlugin.EditConditionActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getSettingsThatAreBuggy() {
        final Set<String> set = new HashSet<String>();

        //Doesn't correctly handle unified volume streams on Android 4.0+; doesn't handle volume bugs on HTC 2.3 devices; doesn't handle new vibrate settings in Android 4.2; etc.
        set.add(Plugin.generateRegistryName("com.akiware.locale.allvolumes",
                //$NON-NLS-1$
                "com.akiware.locale.allvolumes.EditActivity")); //$NON-NLS-1$

        return set;
    }

    @NonNull
    private static Set<String> getConditionsThatAreBlacklisted() {
        final Set<String> set = new HashSet<String>();

        return set;
    }

    @NonNull
    private static Set<String> getSettingsThatAreBlacklisted() {
        final Set<String> set = new HashSet<String>();

        if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.JELLY_BEAN)) {

            set.add(Plugin.generateRegistryName("com.kludgenics.locale.superplane",
                    //$NON-NLS-1$
                    "com.kludgenics.locale.superplane.EditActivity")); //$NON-NLS-1$

            set.add(Plugin.generateRegistryName("com.twofortyfouram.locale.setting.airplanemode",
                    //$NON-NLS-1$
                    "com.twofortyfouram.locale.setting.airplanemode.EditActivity")); //$NON-NLS-1$

            set.add(Plugin.generateRegistryName("com.willemstoker.AutoPilot",
                    //$NON-NLS-1$
                    "com.willemstoker.AutoPilot.EditYourSettingActivity")); //$NON-NLS-1$
        }

        return set;
    }

    @NonNull
    private static Map<String, Set<String>> getConditionsThatHaveAlternatives() {
        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        {
            final Set<String> builtInCalendarConditions;
            {
                final Set<String> temp = new LinkedHashSet<String>();
                temp.add(Plugin.generateRegistryName(
                        "com.twofortyfouram.locale",
                        "com.twofortyfouram.locale.ui.activities.CalendarConditionActivity"));

                builtInCalendarConditions = Collections.unmodifiableSet(temp);
            }

            map.put(Plugin.generateRegistryName("org.acm.steidinger.calendar.localePlugin",
                            "org.acm.steidinger.calendar.localePlugin.EditConditionActivity"),
                    builtInCalendarConditions
            );

            map.put(Plugin.generateRegistryName("com.DroidMunkey.LocaleCalendarConditions",
                            "com.DroidMunkey.LocaleCalendarConditions.EditActivity"),
                    builtInCalendarConditions
            );
        }

        {
            final Set<String> builtInLocationConditions;
            {
                final Set<String> temp = new LinkedHashSet<String>();
                temp.add(Plugin.generateRegistryName(
                        "com.twofortyfouram.locale",
                        "com.twofortyfouram.locale.ui.activities.LocationConditionActivity"));

                builtInLocationConditions = Collections.unmodifiableSet(temp);
            }

            map.put(Plugin.generateRegistryName("at.pansy.droid.locale.location",
                            "at.pansy.droid.locale.location.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("com.DroidMunkey.LocaleWifiConditions",
                            "com.DroidMunkey.LocaleWifiConditions.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("com.hush.locale.cell_beta",
                            "com.hush.locale.cell_beta.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("com.joaomgcd.autolocation",
                            "com.joaomgcd.autolocation.activity.ActivityConfigRequestGeofenceReport"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("com.kanetik.geofence",
                            "com.kanetik.geofence.ui.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("com.suttco.locale.condition.poi",
                            "com.suttco.locale.condition.poi.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("net.appstalk.wifimatch",
                            "net.appstalk.wifimatch.ui.EditActivity"),
                    builtInLocationConditions
            );

            map.put(Plugin.generateRegistryName("org.johanhil.ssid",
                            "org.johanhil.ssid.EditActivity"),
                    builtInLocationConditions
            );
        }

        {
            final Set<String> builtInMovementConditions;
            {
                final Set<String> temp = new LinkedHashSet<String>();
                temp.add(Plugin.generateRegistryName(
                        "com.twofortyfouram.locale",
                        "com.twofortyfouram.locale.ui.activities.MovementConditionActivity"));

                builtInMovementConditions = Collections.unmodifiableSet(temp);
            }

            map.put(Plugin
                            .generateRegistryName("com.jarettmillard.localeactivityplugin",
                                    "com.jarettmillard.localeactivityplugin.EditActivity"),
                    builtInMovementConditions
            );

            map.put(Plugin
                            .generateRegistryName("com.kanetik.movement_detection",
                                    "com.kanetik.movement_detection.ui.EditActivity"),
                    builtInMovementConditions
            );
        }

        return map;
    }

    @NonNull
    private static Map<String, Set<String>> getSettingsThatHaveAlternatives() {
        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        {
            final Set<String> builtInVolumeSettings;
            {
                final Set<String> temp = new LinkedHashSet<String>();
                temp.add(Plugin.generateRegistryName(
                        "com.twofortyfouram.locale",
                        "com.twofortyfouram.locale.ui.activities.VolumeSettingActivity"));
                temp.add(Plugin.generateRegistryName(
                        "com.twofortyfouram.locale",
                        "com.twofortyfouram.locale.ui.activities.VolumeMediaSettingActivity"));

                builtInVolumeSettings = Collections.unmodifiableSet(temp);
            }

            map.put(Plugin.generateRegistryName("com.akiware.locale.allvolumes",
                            "com.akiware.locale.allvolumes.EditActivity"),
                    builtInVolumeSettings
            );

            map.put(Plugin.generateRegistryName("com.olib.locplug.scenemode",
                            "com.olib.locplug.scenemode.EditActivity"),
                    builtInVolumeSettings
            );

            map.put(Plugin.generateRegistryName("com.olib.locplug.scenemodepro",
                            "com.olib.locplug.scenemodepro.EditActivity"),
                    builtInVolumeSettings
            );
        }

        return map;
    }

    /**
     * @return True if the plug-in will disrupt device Internet connectivity.  This may warrant a
     * warning from the host's UI, especially if the plug-in is used in conjunction with a
     * Condition that requires connectivity.
     */
    public static boolean isDisruptsConnectivity(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isDisruptsConnectivity;
        switch (type) {
            case CONDITION: {
                isDisruptsConnectivity = CONDITIONS_THAT_DISRUPT_CONNECTIVITY
                        .contains(registryName);
                break;
            }
            case SETTING: {
                isDisruptsConnectivity = SETTINGS_THAT_DISRUPT_CONNECTIVITY
                        .contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return isDisruptsConnectivity;
    }

    /**
     * @return True if the plug-in requires connectivity.  This may warrant a
     * warning from the host's UI, especially if the plug-in is used in conjunction with a
     * Condition that requires connectivity.
     */
    public static boolean isRequiresConnectivity(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isRequiresConnectivity;
        switch (type) {
            case CONDITION: {
                isRequiresConnectivity = CONDITIONS_THAT_REQUIRE_CONNECTIVITY
                        .contains(registryName);
                break;
            }
            case SETTING: {
                isRequiresConnectivity = SETTINGS_THAT_REQUIRE_CONNECTIVITY
                        .contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return isRequiresConnectivity;
    }

    /**
     * Determines whether the plug-in requires backwards compatibility.
     * <p>The
     * beta version of the plug-in API for Locale released in January 2009 was
     * slightly different from the final version of the API release in December
     * 2009. Prior to Locale 1.0,
     * {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE} didn't exist and
     * plug-ins stored and retrieved their extras directly from the
     * {@link com.twofortyfouram.locale.api.Intent#ACTION_EDIT_SETTING} or
     * {@link com.twofortyfouram.locale.api.Intent#ACTION_FIRE_SETTING} Intents.
     * Although this backwards compatibility should only apply to Setting plug-ins
     * (since Conditions weren't supported until the API was finalized in Locale
     * 1.0), some plug-in developers copy-pasted their setting implementations to
     * create conditions so some conditions need backwards compatibility enabled as
     * well.</p>
     *
     * @return true if backwards compatibility is enabled for the plug-in.
     */
    public static boolean isBackwardsCompatibilityEnabled(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isBackwardsCompatibilityEnabled;
        switch (type) {
            case CONDITION: {
                isBackwardsCompatibilityEnabled = CONDITIONS_REQUIRING_BACKWARDS_COMPATIBILITY
                        .contains(registryName);
                break;
            }
            case SETTING: {
                isBackwardsCompatibilityEnabled = SETTINGS_REQUIRING_BACKWARDS_COMPATIBILITY
                        .contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return isBackwardsCompatibilityEnabled;
    }

    /**
     * Determines whether a third party plug-in has an alternative first party implementation.
     * <p>The UI may decide to inform the user that the first party implementation exists.  This
     * is not intended to prevent users from trying third party plug-ins, however it is intended to
     * educate users that built-in functionality may already exist.  Given the lack of a review
     * process for plug-ins, often plug-in quality is below that of built-in functionality and
     * educating the user generally improves the user experience without taking away choice.
     * </p>
     * <p>Note: The first party implementation is not
     * guaranteed to be available, and it is the caller's responsibility to handle such a case.
     * For
     * example, different hosts may have different first party implementations.
     * As another example example, Locale contains a built-in Calendar condition but it is only
     * available on API
     * 14
     * or greater.  When determining whether the user should be presented with alternatives to a
     * third party plug-in, Locale must first determine whether the alternative is actually
     * available or not.</p>
     *
     * @return A set of possible first-party implementation registry names.  This set has consistent
     * iteration ordering may have been wrapped
     * in a call to {@link Collections#unmodifiableSet(java.util.Set)}.  The set may be empty.
     */
    @NonNull
    public static Set<String> getBuiltInAlternative(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        Set<String> alternativeRegistryName;
        switch (type) {
            case CONDITION: {
                alternativeRegistryName = CONDITIONS_THAT_HAVE_ALTERNATIVES
                        .get(registryName);
                break;
            }
            case SETTING: {
                alternativeRegistryName = SETTINGS_THAT_HAVE_ALTERNATIVES
                        .get(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        if (null == alternativeRegistryName) {
            alternativeRegistryName = Collections.emptySet();
        }

        return alternativeRegistryName;
    }

    /**
     * Determines whether a plug-in is known to be buggy.  The UI might use this to warn the user
     * about the plug-in.  Such a warning improves the user experience, as the user won't wonder
     * whether they've set things up correctly when a plug-in doesn't work.
     * <p>
     * A buggy plug-in is different from a blacklisted plug-in, as a buggy plug-in may still
     * provide
     * some working functionality to the user.  An example of a buggy plug-in would be a third
     * party
     * location condition plug-in that doesn't implement proper hysteresis.  Such a plug-in may
     * work, but the
     * user would perceive the plug-in as not behaving well.
     * </p>
     */
    public static boolean isBuggy(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isBuggy;

        switch (type) {
            case CONDITION: {
                isBuggy = CONDITIONS_THAT_ARE_BUGGY.contains(registryName);
                break;
            }
            case SETTING: {
                isBuggy = SETTINGS_THAT_ARE_BUGGY.contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return isBuggy;
    }

    /**
     * <p>Determines whether a plug-in is blacklisted.  A blacklisted plug-in is not considered a
     * valid plug-in by the host and will be ignored by the host.</p>
     * <p>While preserving user choice is always important, some plug-ins are so malicious, buggy,
     * otherwise problematic that they must be disabled in order to protect the interests of users.
     * Before anyone panics, it should be noted that plug-ins are only blacklisted under
     * exceptional
     * circumstances.  This API is maintained so that the entire ecosystem can
     * quickly be protected if absolutely necessary.
     * The decision whether to blacklist a plug-in is fully at the discretion of two forty four
     * a.m. LLC, although the following guidelines identify potential candidates for blacklisting
     * and
     * the overall blacklisting process.
     * </p>
     * <p>
     * <ol>
     * <li>A plug-in that is malicious is considered a candidate for blacklisting.
     * <li>A plug-in that
     * is so buggy that it fails to function correctly most or all of the time is considered a
     * candidate
     * for
     * blacklisting.  For example, a plug-in designed to toggle Airplane mode for Android 2.3 will
     * not function on Android 4.2, due to new Android security restrictions.  Such a plug-in would
     * be blacklisted.</li>
     * <li>A plug-in that interferes with the normal functioning of the user's phone is
     * a candidate for blacklisting.  For example, a plug-in setting to reboot the user's device
     * could potentially be configured to cause a reboot loop effectively locking the user out of
     * his device.  If the user added this reboot plug-in to the Defaults in Locale
     * then the device would reboot as soon as it finished booting.  To avoid being blacklisted,
     * such a plug-in would need to implement mechanisms to avoid a
     * reboot loop, which might include a cool-down period after the last time it was fired or
     * suppressing firing it after a boot.</li>
     * <li>A plug-in that interferes with the normal functioning of the host is considered a
     * candidate for blacklisting.  For example, a security plug-in which prevents the host's UI
     * from launching by monitoring the Activity stack might be considered a candidate for
     * blacklisting.</li>
     * <li>A plug-in that is malicious, for example uploading private data without disclosure, is a
     * candidate for blacklisting.</li>
     * <li>A plug-in that contains serious security flaws is a candidate for blacklisting.  One
     * example would be a plug-in that stores unencrypted login
     * credentials in {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE}.  Plug-ins are
     * required to store such credentials in their own app private storage, rather than in the
     * Bundle.  Another example would be a plug-in that logs credentials to logcat.</li>
     * <li>A plug-in that can cost the user a large amount of money is a candidate for
     * blacklisting.
     * For example, a plug-in that automatically sends SMS messages could rack up charges with each
     * outgoing message.  Such a plug-in should implement throttling to limit the total number of
     * messages sent as a safeguard.</li>
     * </ol>
     * </p>
     * <p>Once a plug-in is identified as a candidate for blacklisting, multiple attempts will
     * be made
     * to contact the plug-in developer via email, Google Play, or whatever other mechanism the
     * developer provides for feedback.  If the developer does not respond within 30 days, the
     * plug-in may be blacklisted at the discretion of two forty four a.m. LLC.  If the
     * developer does respond and appropriately addresses
     * all concerns, then the plug-in will be unblacklisted.</p>
     *
     * @return True if the plug-in is blacklisted.  False otherwise.
     */
    public static boolean isBlacklisted(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isBlacklisted;
        switch (type) {
            case CONDITION: {
                isBlacklisted = CONDITIONS_THAT_ARE_BLACKLISTED.contains(registryName);
                break;
            }
            case SETTING: {
                isBlacklisted = SETTINGS_THAT_ARE_BLACKLISTED.contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return isBlacklisted;
    }

    /**
     * <p>Determines whether a plug-in has an extreme impact on battery life.  The UI might use
     * this
     * information to display a warning to the user, in order to prevent users from blaming the
     * host
     * app for
     * battery drain caused by a plug-in.
     */
    public static boolean isDrainsBattery(@NonNull final PluginType type,
            @NonNull final String registryName) {
        assertNotNull(type, "type"); //$NON-NLS-1$
        assertNotEmpty(registryName, "registryName"); //$NON-NLS-1$

        final boolean isDrainsBattery;

        switch (type) {
            case CONDITION: {
                isDrainsBattery = CONDITIONS_THAT_DRAIN_BATTERY.contains(registryName);
                break;
            }
            case SETTING: {
                isDrainsBattery = SETTINGS_THAT_DRAIN_BATTERY.contains(registryName);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }

        return isDrainsBattery;
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginCharacteristics() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
