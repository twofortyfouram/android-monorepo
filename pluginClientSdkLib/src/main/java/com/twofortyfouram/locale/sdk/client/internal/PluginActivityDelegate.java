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

package com.twofortyfouram.locale.sdk.client.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.client.ui.activity.IPluginActivity;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.Immutable;

import org.json.JSONException;
import org.json.JSONObject;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * Activities that implement the {@link IPluginActivity} interface can delegate much of their
 * responsibility to this class.
 *
 * @param <T> Plug-in activity.
 */
/*
 * This class is intended to make the implementation of various plug-in Activities DRY.
 *
 * This class has no state, so therefore is immutable.
 */
@Immutable
public final class PluginActivityDelegate<T extends Activity & IPluginActivity> {

    /**
     * @param intent Intent to check.
     * @return True if intent is a Locale plug-in edit Intent.
     */
    public static boolean isLocalePluginEditIntent(@NonNull final Intent intent) {
        assertNotNull(intent, "intent"); //$NON-NLS-1$

        final String action = intent.getAction();

        return LocalePluginIntent.ACTION_EDIT_CONDITION.equals(action)
                || LocalePluginIntent.ACTION_EDIT_SETTING.equals(action);
    }

    public void onCreate(@NonNull final T activity, @Nullable final Bundle savedInstanceState) {
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        final Intent intent = activity.getIntent();

        if (isLocalePluginEditIntent(intent)) {
            if (BundleScrubber.scrub(intent)) {
                return;
            }

            final JSONObject previousJson = activity.getPreviousJson();

            Lumberjack
                    .v("Creating Activity with Intent=%s, savedInstanceState=%s, EXTRA_JSON=%s",
                            intent, savedInstanceState, previousJson); //$NON-NLS-1$
        }
    }

    public void onPostCreate(@NonNull final T activity, @Nullable final Bundle savedInstanceState) {
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        if (PluginActivityDelegate.isLocalePluginEditIntent(activity.getIntent())) {
            if (null == savedInstanceState) {
                final JSONObject previousJson = activity.getPreviousJson();
                final String previousBlurb = activity.getPreviousBlurb();
                if (null != previousJson && null != previousBlurb) {
                    activity.onPostCreateWithPreviousResult(previousJson, previousBlurb);
                }
            }
        }
    }

    public void finish(@NonNull final T activity, final boolean isCancelled) {
        if (PluginActivityDelegate.isLocalePluginEditIntent(activity.getIntent())) {
            if (!isCancelled) {
                final JSONObject resultJson = activity.getResultJson();

                if (null != resultJson) {
                    // TODO: consider checking the size of the serialized form

                    final String blurb = activity.getResultBlurb(resultJson);
                    assertNotNull(blurb, "blurb"); //$NON-NLS-1$

                    final JSONObject previousJson = activity.getPreviousJson();

                    // JSON string comparison is not ideal, although they will have both been
                    // processed by the same parser so hopefully ordering will be consistent.
                    // In the future this should be replaced with a more robust comparison.
                    // Worse case scenario if this comparison isn't accurate is that an additional
                    // save is performed that wasn't expected.
                    // TODO: Implement real JSON comparison
                    final String newResultJsonString = resultJson.toString();
                    final String oldResultJsonString = null != previousJson ? previousJson
                            .toString() : null;

                    if (!newResultJsonString.equals(oldResultJsonString)
                            || !blurb.equals(activity.getPreviousBlurb())) {
                        final Bundle resultBundle = new Bundle();
                        resultBundle.putString(LocalePluginIntent.EXTRA_STRING_JSON,
                                newResultJsonString);

                        final Intent resultIntent = new Intent();
                        resultIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE,
                                resultBundle);
                        resultIntent.putExtra(
                                LocalePluginIntent.EXTRA_STRING_BLURB,
                                blurb);

                        activity.setResult(Activity.RESULT_OK, resultIntent);
                    }

                }
            }
        }
    }

    @Nullable
    public final String getPreviousBlurb(@NonNull final T activity) {
        return activity.getIntent().getStringExtra(
                LocalePluginIntent.EXTRA_STRING_BLURB);
    }

    @Nullable
    public JSONObject getPreviousJson(@NonNull final T activity) {
        assertNotNull(activity, "activity"); //$NON-NLS-1$

        final Bundle bundle = activity.getIntent().getBundleExtra(
                LocalePluginIntent.EXTRA_BUNDLE);

        if (!BundleScrubber.scrub(bundle)) {
            if (null != bundle) {
                final String jsonString = bundle
                        .getString(LocalePluginIntent.EXTRA_STRING_JSON);
                if (null != jsonString) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonString);
                    } catch (final JSONException e) {
                        Lumberjack.e("Failed to parse %s as JSON", jsonString, e); //$NON-NLS
                    }

                    if (activity.isJsonValid(jsonObject)) {
                        return jsonObject;
                    }
                }
            }
        }

        return null;
    }
}
