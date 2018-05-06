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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.twofortyfouram.locale.api.LocalePluginIntent;

import net.jcip.annotations.Immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Contains dynamic configuration for a plug-in.
 */
@Immutable
public final class PluginConfiguration implements Parcelable {

    /**
     * Implements the {@link Parcelable} interface
     */
    @NonNull
    public static final Parcelable.Creator<PluginConfiguration> CREATOR
            = new Parcelable.Creator<PluginConfiguration>() {
        @Override
        public PluginConfiguration createFromParcel(final Parcel in) {
            assertNotNull(in, "in"); //$NON-NLS-1$

            final boolean isBackwardsCompatibilityEnabled = convertIntToBoolean(in.readInt());
            final boolean isRequiresConnectivity = convertIntToBoolean(in.readInt());
            final boolean isDisruptsConnectivity = convertIntToBoolean(in.readInt());
            final boolean isBuggy = convertIntToBoolean(in.readInt());
            final boolean isDrainsBattery = convertIntToBoolean(in.readInt());
            final boolean isBlacklisted = convertIntToBoolean(in.readInt());
            final List<String> alternatives = new LinkedList<>();

            in.readStringList(alternatives);

            return new PluginConfiguration(isBackwardsCompatibilityEnabled, isRequiresConnectivity,
                    isDisruptsConnectivity, isBuggy, isDrainsBattery, isBlacklisted, alternatives);
        }

        @Override
        public PluginConfiguration[] newArray(final int size) {
            return new PluginConfiguration[size];
        }

        private boolean convertIntToBoolean(final int i) {
            return i != 0;
        }
    };

    private final boolean mIsBackwardsCompatibilityEnabled;

    private final boolean mIsRequiresConnectivity;

    private final boolean mIsDisruptsConnectivity;

    private final boolean mIsDrainsBattery;

    private final boolean mIsBuggy;

    private final boolean mIsBlacklisted;

    @NonNull
    private final Set<String> mAlternatives;

    /**
     * Constructs a new plug-in configuration.
     *
     * @param isBackwardsCompatibilityEnabled If backwards compatibility for pre-1.0 plug-in API is
     *                                        enabled.
     * @param isRequiresConnectivity          If the plug-in requires Internet connectivity.
     * @param isDisruptsConnectivity          If the plug-in disrupts Internet connectivity.
     * @param isBuggy                         If the plug-in is known to be buggy.
     * @param isDrainsBattery                 If the plug-in is known to drain the battery.
     * @param isBlacklisted                   If the plug-in is to be disabled.
     * @param alternatives                    Alternatives to the plug-in, as a collection of
     *                                        registry names.
     */
    public PluginConfiguration(final boolean isBackwardsCompatibilityEnabled,
            final boolean isRequiresConnectivity,
            final boolean isDisruptsConnectivity, final boolean isBuggy,
            final boolean isDrainsBattery,
            final boolean isBlacklisted, @NonNull final Collection<String> alternatives) {
        assertNotNull(alternatives, "alternatives");

        mIsBackwardsCompatibilityEnabled = isBackwardsCompatibilityEnabled;
        mIsRequiresConnectivity = isRequiresConnectivity;
        mIsDisruptsConnectivity = isDisruptsConnectivity;
        mIsBuggy = isBuggy;
        mIsDrainsBattery = isDrainsBattery;
        mIsBlacklisted = isBlacklisted;

        if (alternatives.isEmpty()) {
            mAlternatives = Collections.emptySet();
        } else {
            mAlternatives = Collections.unmodifiableSet(new LinkedHashSet<>(alternatives));
        }
    }

    /**
     * Determines whether the plug-in requires backwards compatibility.
     * <p>The
     * beta version of the plug-in API for Locale released in January 2009 was
     * slightly different from the final version of the API released in December
     * 2009. Prior to Locale 1.0,
     * {@link LocalePluginIntent#EXTRA_BUNDLE} didn't exist and
     * plug-ins stored and retrieved their extras directly from the
     * {@link LocalePluginIntent#ACTION_EDIT_SETTING} or
     * {@link LocalePluginIntent#ACTION_FIRE_SETTING} Intents.
     * Although this backwards compatibility should only apply to Setting plug-ins
     * (since Conditions weren't supported until the API was finalized in Locale
     * 1.0), some plug-in developers copy-pasted their setting implementations to
     * create conditions so some conditions need backwards compatibility enabled as
     * well.</p>
     *
     * @return true if backwards compatibility is enabled for the plug-in.
     */
    public boolean isBackwardsCompatibilityEnabled() {
        return mIsBackwardsCompatibilityEnabled;
    }

    /*
     * @return true if the plug-in requires Internet connectivity.
     */
    public boolean isRequiresConnectivity() {
        return mIsRequiresConnectivity;
    }

    /**
     * @return true if the plug-in disrupts Internet connectivity.
     */
    public boolean isDisruptsConnectivity() {
        return mIsDisruptsConnectivity;
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
     *
     * @return true if the plug-in is known to be buggy.
     */
    public boolean isBuggy() {
        return mIsBuggy;
    }


    /**
     * <p>Determines whether a plug-in has an extreme impact on battery life.  The UI might use
     * this information to display a warning to the user, in order to prevent users from blaming
     * the
     * host app for battery drain caused by a plug-in.
     *
     * @return true if the plug-in is known to drain the battery.
     */
    public boolean isDrainsBattery() {
        return mIsDrainsBattery;
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
     * a.m. LLC, although the following guidelines cover the blacklisting process.  two forty
     * four a.m. LLC's ability to blacklist does not
     * imply that two forty four a.m. LLC is actively or continuously testing plug-ins, nor does it
     * imply two forty four a.m. LLC has any responsibility to police plug-ins.
     * </p>
     * <ol>
     * <li>A plug-in that is malicious is considered a candidate for blacklisting.
     * <li>A plug-in that
     * is so buggy that it fails to function correctly most or all of the time is considered a
     * candidate
     * for
     * blacklisting.  For example, a plug-in designed to toggle Airplane mode for Android 2.3 will
     * not function on Android 4.2, due to new Android security restrictions.  Such a plug-in could
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
     * credentials in {@link LocalePluginIntent#EXTRA_BUNDLE}.  Plug-ins are
     * required to store such credentials in their own app private storage, rather than in the
     * Bundle.  Another example would be a plug-in that logs credentials to logcat.</li>
     * <li>A plug-in that can cost the user a large amount of money is a candidate for
     * blacklisting.
     * For example, a plug-in that automatically sends SMS messages could rack up charges with each
     * outgoing message.  Such a plug-in should implement throttling to limit the total number of
     * messages sent as a safeguard.</li>
     * </ol>
     * <p>Once a plug-in is identified as a candidate for blacklisting, multiple attempts will
     * be made
     * to contact the plug-in developer via email, Google Play, or whatever other mechanism the
     * developer provides for feedback.  If the developer does not respond within 30 days, the
     * plug-in may be blacklisted at the discretion of two forty four a.m. LLC.  If the
     * developer does respond and appropriately addresses
     * all concerns, then the plug-in will be unblacklisted.</p>
     *
     * @return True if the plug-in is blacklisted.
     */
    public boolean isBlacklisted() {
        return mIsBlacklisted;
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
     * iteration ordering and may have been wrapped in call to {@link Collections#unmodifiableSet(java.util.Set)}.
     */
    @NonNull
    public Collection<String> getAlternatives() {
        return mAlternatives;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(convertBooleanToInt(mIsBackwardsCompatibilityEnabled));
        dest.writeInt(convertBooleanToInt(mIsRequiresConnectivity));
        dest.writeInt(convertBooleanToInt(mIsDisruptsConnectivity));
        dest.writeInt(convertBooleanToInt(mIsBuggy));
        dest.writeInt(convertBooleanToInt(mIsDrainsBattery));
        dest.writeInt(convertBooleanToInt(mIsBlacklisted));
        dest.writeStringList(new LinkedList<>(mAlternatives));
    }

    private static int convertBooleanToInt(final boolean bool) {
        return bool ? 1 : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PluginConfiguration that = (PluginConfiguration) o;

        if (mIsBackwardsCompatibilityEnabled != that.mIsBackwardsCompatibilityEnabled) {
            return false;
        }
        if (mIsBlacklisted != that.mIsBlacklisted) {
            return false;
        }
        if (mIsBuggy != that.mIsBuggy) {
            return false;
        }
        if (mIsDisruptsConnectivity != that.mIsDisruptsConnectivity) {
            return false;
        }
        if (mIsDrainsBattery != that.mIsDrainsBattery) {
            return false;
        }
        if (mIsRequiresConnectivity != that.mIsRequiresConnectivity) {
            return false;
        }
        if (!mAlternatives.equals(that.mAlternatives)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (mIsBackwardsCompatibilityEnabled ? 1 : 0);
        result = 31 * result + (mIsRequiresConnectivity ? 1 : 0);
        result = 31 * result + (mIsDisruptsConnectivity ? 1 : 0);
        result = 31 * result + (mIsDrainsBattery ? 1 : 0);
        result = 31 * result + (mIsBuggy ? 1 : 0);
        result = 31 * result + (mIsBlacklisted ? 1 : 0);
        result = 31 * result + mAlternatives.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PluginConfiguration{" +
                "mIsBackwardsCompatibilityEnabled=" + mIsBackwardsCompatibilityEnabled +
                ", mIsRequiresConnectivity=" + mIsRequiresConnectivity +
                ", mIsDisruptsConnectivity=" + mIsDisruptsConnectivity +
                ", mIsDrainsBattery=" + mIsDrainsBattery +
                ", mIsBuggy=" + mIsBuggy +
                ", mIsBlacklisted=" + mIsBlacklisted +
                ", mAlternatives=" + mAlternatives +
                '}';
    }
}
