/*
 * android-plugin-client-sdk-for-locale
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

package com.twofortyfouram.locale.sdk.client.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.twofortyfouram.locale.annotation.ConditionResult;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.AndroidSdkVersion;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>Abstract superclass for a plug-in condition BroadcastReceiver implementation.</p>
 * <p>The plug-in receiver lifecycle is as follows:</p>
 * <ol>
 * <li>{@link #onReceive(Context, Intent)} is called by the Android
 * frameworks.
 * onReceive() will verify that the Intent is valid.  If the Intent is invalid, then the receiver
 * sets the result to {@link LocalePluginIntent#RESULT_CONDITION_UNKNOWN
 * RESULT_CONDITION_UNKNOWN} and
 * returns.  If the Intent appears to be valid, then the lifecycle continues.</li>
 * <li>{@link #isJsonValid(JSONObject)} is called to determine whether {@link
 * LocalePluginIntent#EXTRA_BUNDLE EXTRA_BUNDLE} is valid. If the Bundle is
 * invalid, then the
 * receiver
 * sets the result to {@link LocalePluginIntent#RESULT_CONDITION_UNKNOWN
 * RESULT_CONDITION_UNKNOWN} and
 * returns.  If the bundle is valid, then the lifecycle continues.</li>
 * <li>{@link #isAsync()} is called to determine whether the remaining work should be performed on
 * a
 * background thread.</li>
 * <li>{@link #getPluginConditionResult(Context, JSONObject)} is called to
 * determine the plug-in's status, and the return value of this method is set as the receiver's
 * result code.</li>
 * </ol>
 * <p>
 * Implementations of this BroadcastReceiver must be registered in the Android
 * Manifest with an Intent filter for
 * {@link LocalePluginIntent#ACTION_QUERY_CONDITION ACTION_QUERY_CONDITION}. The
 * BroadcastReceiver must be exported, enabled, and cannot have permissions
 * enforced on it.
 * </p>
 */
@ThreadSafe
public abstract class AbstractPluginConditionReceiver extends AbstractAsyncReceiver {

    /*
     * The multiple return statements in this method are a little gross, but the
     * alternative of nested if statements is even worse :/
     */
    @Override
    public final void onReceive(final Context context, final Intent intent) {
        if (BundleScrubber.scrub(intent)) {
            return;
        }

        Lumberjack.v("Received %s", intent); //$NON-NLS-1$

        if (!isOrderedBroadcast()) {
            Lumberjack.e("Broadcast is not ordered"); //$NON-NLS-1$
            return;
        }

        if (!LocalePluginIntent.ACTION_QUERY_CONDITION.equals(intent
                .getAction())) {
            Lumberjack
                    .e("Intent action is not %s",
                            LocalePluginIntent.ACTION_QUERY_CONDITION); //$NON-NLS-1$
            setResultCode(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
            return;
        }
        /*
         * Ignore implicit intents, because they are not valid. It would be
         * meaningless if ALL plug-in condition BroadcastReceivers installed
         * were asked to handle queries not intended for them.
         */
        if (!new ComponentName(context, this.getClass().getName()).equals(intent
                .getComponent())) {
            Lumberjack.e("Intent is not explicit"); //$NON-NLS-1$
            setResultCode(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
            abortBroadcast();
            return;
        }

        final Bundle bundle = intent
                .getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE);
        if (BundleScrubber.scrub(intent)) {
            return;
        }

        if (null == bundle) {
            Lumberjack.e("%s is missing",
                    LocalePluginIntent.EXTRA_BUNDLE); //$NON-NLS-1$
            setResultCode(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
            return;
        }

        final JSONObject jsonObject;
        {
            final String jsonString = bundle.getString(LocalePluginIntent.EXTRA_STRING_JSON);

            if (null == jsonString) {
                Lumberjack.v("%s is missing", LocalePluginIntent.EXTRA_STRING_JSON); //$NON-NLS
                return;
            }

            try {
                jsonObject = new JSONObject(jsonString);
            } catch (final JSONException e) {
                Lumberjack.e("%s=%s is invalid",
                        LocalePluginIntent.EXTRA_STRING_JSON, jsonString); //$NON-NLS-1$
                return;
            }
        }

        if (!isJsonValid(jsonObject)) {
            Lumberjack.e("%s is invalid",
                    LocalePluginIntent.EXTRA_STRING_JSON); //$NON-NLS-1$
            setResultCode(LocalePluginIntent.RESULT_CONDITION_UNKNOWN);
            return;
        }

        if (isAsync() && AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
            final AsyncCallback callback = new AsyncCallback() {
                @NonNull
                private final Context mContext = context;

                @NonNull
                private final JSONObject mJson = jsonObject;

                @Override
                public int runAsync() {
                    final int pluginResult = getPluginConditionResult(mContext, mJson);
                    assertResult(pluginResult);
                    return pluginResult;
                }

            };

            goAsyncWithCallback(callback, isOrderedBroadcast());
        } else {
            final int pluginState = getPluginConditionResult(context, jsonObject);
            assertResult(pluginState);
            setResultCode(pluginState);
        }
    }

    /**
     * @param result One of the acceptable plug-in result codes.
     * @throws AssertionError if {@code result} is not one of the three
     *                        acceptable values.
     */
    @VisibleForTesting
    /* package */ static void assertResult(final int result) {
        if (LocalePluginIntent.RESULT_CONDITION_SATISFIED != result
                && LocalePluginIntent.RESULT_CONDITION_UNSATISFIED != result
                && LocalePluginIntent.RESULT_CONDITION_UNKNOWN != result) {
            throw new AssertionError(
                    Lumberjack
                            .formatMessage(
                                    "result=%d is not one of [%d, %d, %d]", result, //$NON-NLS-1$
                                    LocalePluginIntent.RESULT_CONDITION_SATISFIED,
                                    LocalePluginIntent.RESULT_CONDITION_UNSATISFIED,
                                    LocalePluginIntent.RESULT_CONDITION_UNKNOWN)
            );
        }
    }

    /**
     * <p>Gives the plug-in receiver an opportunity to validate the JSON, to ensure that a malicious
     * application isn't attempting to pass an invalid object.</p>
     * <p>
     * This method will be called on the BroadcastReceiver's Looper (normatively the main thread)
     * </p>
     *
     * @param json The plug-in's JSON previously returned by the edit
     *             Activity.  {@code json} should not be mutated by this method.
     * @return true if {@code json} appears to be valid.  False if {@code json} appears to be
     * invalid.
     */
    @MainThread
    protected abstract boolean isJsonValid(@NonNull final JSONObject json);

    /**
     * Configures the receiver whether it should process the Intent in a
     * background thread. Plug-ins should return true if their
     * {@link #getPluginConditionResult(Context, JSONObject)} method performs any
     * sort of disk IO (ContentProvider query, reading SharedPreferences, etc.).
     * or other work that may be slow.
     * <p>
     * Asynchronous BroadcastReceivers are not supported prior to Honeycomb, so
     * with older platforms broadcasts will always be processed on the BroadcastReceiver's Looper
     * (which for Manifest registered receivers will be the main thread).
     *
     * @return True if the receiver should process the Intent in a background
     * thread. False if the plug-in should process the Intent on the
     * BroadcastReceiver's Looper (normatively the main thread).
     */
    @MainThread
    protected abstract boolean isAsync();

    /**
     * Determines the state of the plug-in.
     *
     * If {@link #isAsync()} returns true, this method will be called on a
     * background thread. If {@link #isAsync()} returns false, this method will
     * be called on the main thread. Regardless of which thread this method is
     * called on, this method MUST return within 10 seconds per the requirements
     * for BroadcastReceivers.
     *
     * @param context BroadcastReceiver context.
     * @param json    The plug-in's JSON previously returned by the edit
     *                Activity.
     * @return One of the Locale plug-in query results:
     * {@link LocalePluginIntent#RESULT_CONDITION_SATISFIED
     * RESULT_CONDITION_SATISFIED}
     * ,
     * {@link LocalePluginIntent#RESULT_CONDITION_UNSATISFIED
     * RESULT_CONDITION_UNSATISFIED}
     * , or
     * {@link LocalePluginIntent#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     * . If {@code json} is invalid, implementations must return
     * {@link LocalePluginIntent#RESULT_CONDITION_UNKNOWN
     * RESULT_CONDITION_UNKNOWN}
     */
    @AnyThread
    @ConditionResult
    protected abstract int getPluginConditionResult(@NonNull final Context context,
            @NonNull final JSONObject json);

}
