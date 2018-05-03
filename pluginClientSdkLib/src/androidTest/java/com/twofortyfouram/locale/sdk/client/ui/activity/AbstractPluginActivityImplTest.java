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

package com.twofortyfouram.locale.sdk.client.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.Assertions;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.client.test.condition.ui.activity.PluginJsonValues;
import com.twofortyfouram.spackle.bundle.BundleComparer;
import com.twofortyfouram.test.espresso.UiTestPrerequesites;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Superclass for Activity unit tests that provides facilities to make testing
 * easier.
 */
@RunWith(AndroidJUnit4.class)
public abstract class AbstractPluginActivityImplTest<T extends Activity & IPluginActivity> extends
        UiTestPrerequesites{

    @NonNull
    protected ActivityTestRule<T> mActivity;

    @NonNull
    protected abstract Class<T> getActivityClass();

    @Before
    public void setUp() throws Exception {
        mActivity = new ActivityTestRule<>(getActivityClass());
    }

    @MediumTest
    @Test
    public void testNewCondition_cancel_because_null_bundle() {
        final T activity = mActivity.launchActivity(getDefaultStartIntent(PluginType.CONDITION));

        assertIsBundleValidCount(0);
        assertOnPostCreateWithPreviousResultCount(0);
        assertNull(getPreviousJsonOnMainSync(activity));

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(0);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    @MediumTest
    @Test
    public void testNewSetting_cancel_because_null_bundle() {
        final T activity = mActivity.launchActivity(getDefaultStartIntent(PluginType.SETTING));

        assertIsBundleValidCount(0);
        assertOnPostCreateWithPreviousResultCount(0);
        assertNull(getPreviousJsonOnMainSync(activity));

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(0);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    @MediumTest
    @Test
    public void testNewCondition_save() {
        final T activity = mActivity.launchActivity(getDefaultStartIntent(PluginType.CONDITION));

        assertIsBundleValidCount(0);
        assertOnPostCreateWithPreviousResultCount(0);
        assertNull(getPreviousJsonOnMainSync(activity));

        final JSONObject json = PluginJsonValues.generateJson(getContext(),
                "some_new_test_value");  //$NON-NLS-1$

        final String blurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(json, blurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(json), blurb);
    }

    @MediumTest
    @Test
    public void testNewSetting_save() {
        final T activity = mActivity.launchActivity(getDefaultStartIntent(PluginType.SETTING));

        assertIsBundleValidCount(0);
        assertOnPostCreateWithPreviousResultCount(0);
        assertNull(getPreviousJsonOnMainSync(activity));

        final JSONObject json = PluginJsonValues.generateJson(getContext(),
                "some_new_test_value");  //$NON-NLS-1$

        final String blurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(json, blurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(json), blurb);
    }

    @MediumTest
    @Test
    public void testOldCondition_save_bundle_and_blurb_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.CONDITION);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final JSONObject newJson = PluginJsonValues
                .generateJson(getContext(), "Some new blurb"); //$NON-NLS-1$

        final String newBlurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(newJson, newBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(newJson), newBlurb);
    }

    @MediumTest
    @Test
    public void testOldCondition_save_bundle_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.CONDITION);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());

        final String oldBlurb = "Some old blurb"; //$NON-NLS-1$
        assertEquals(oldBlurb, getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final JSONObject newJson = PluginJsonValues
                .generateJson(getContext(), "some_new_test_value"); //$NON-NLS-1$

        setActivityJsonAndBlurb(newJson, oldBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(newJson), oldBlurb);
    }

    @MediumTest
    @Test
    public void testOldCondition_save_blurb_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.CONDITION);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final String newBlurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(oldJson, newBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(oldJson), newBlurb);
    }

    @MediumTest
    @Test
    public void testOldSetting_save_bundle_and_blurb_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.SETTING);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final JSONObject newJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_new_test_value"); //$NON-NLS-1$

        final String newBlurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(newJson, newBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(newJson), newBlurb);
    }

    @MediumTest
    @Test
    public void testOldSetting_save_bundle_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.SETTING);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());

        final String oldBlurb = "Some old blurb"; //$NON-NLS-1$
        assertEquals(oldBlurb, getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final JSONObject newJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_new_test_value"); //$NON-NLS-1$

        setActivityJsonAndBlurb(newJson, oldBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(newJson), oldBlurb);
    }

    @MediumTest
    @Test
    public void testOldSetting_save_blurb_changed() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.SETTING);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        final String newBlurb = "Some new blurb"; //$NON-NLS-1$

        setActivityJsonAndBlurb(oldJson, newBlurb);

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_OK, bundleFromJson(oldJson), newBlurb);
    }

    @MediumTest
    @Test
    public void testOldCondition_diffing_cancel() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.CONDITION);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        setActivityJsonAndBlurb(oldJson, "Some old blurb"); //$NON-NLS-1$

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    @MediumTest
    @Test
    public void testOldSetting_diffing_cancel() {
        final Bundle bundle = generateBundle(getContext(), "some_old_test_value");  //$NON-NLS-1$

        final Intent activityIntent = getDefaultStartIntent(PluginType.SETTING);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(1);

        final JSONObject oldJson = PluginJsonValues
                .generateJson(getContext(),
                        "some_old_test_value"); //$NON-NLS-1$
        assertEquals(oldJson.toString(), getPreviousJsonOnMainSync(activity).toString());
        assertEquals("Some old blurb", getPreviousBlurbOnMainSync(activity)); //$NON-NLS-1$

        setActivityJsonAndBlurb(oldJson, "Some old blurb"); //$NON-NLS-1$

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(1);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    @MediumTest
    @Test
    public void testOldCondition_bad_bundle() throws JSONException {
        final JSONObject json = PluginJsonValues
                .generateJson(getContext(), "some_old_test_value");  //$NON-NLS-1$
        json.put("extra_key", "extra_value");

        final Intent activityIntent = getDefaultStartIntent(PluginType.CONDITION);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundleFromJson(json));
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(0); // This is key for this test!

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(0);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    @MediumTest
    @Test
    public void testOldSetting_bad_bundle() throws JSONException {
        final JSONObject json = PluginJsonValues
                .generateJson(getContext(), "some_old_test_value");  //$NON-NLS-1$
        json.put("extra_key", "extra_value");

        final Intent activityIntent = getDefaultStartIntent(PluginType.SETTING);
        activityIntent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundleFromJson(json));
        activityIntent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                "Some old blurb"); //$NON-NLS-1$

        final T activity = mActivity.launchActivity(activityIntent);

        assertIsBundleValidCount(2);
        assertOnPostCreateWithPreviousResultCount(0); // This is key for this test!

        getInstrumentation().runOnMainSync(activity::finish);

        assertGetResultBundleCount(1);
        assertGetBlurbCount(0);

        assertActivityResult(Activity.RESULT_CANCELED, null, null);
    }

    protected abstract void setActivityJsonAndBlurb(@Nullable final JSONObject json,
            @Nullable final String blurb);

    /**
     * Asserts the Activity result is correct.
     * <p/>
     * {@link Activity#finish()} must be called prior to calling this method.
     *
     * @param bundle The bundle to verify exists. Null indicates that no bundle
     *               should be present (not that a null bundle should be present).
     * @param blurb  The blurb to verify exists. Null indicates that no blurb
     *               should be present (not that a null blurb should be present).
     */
    private void assertActivityResult(final int resultCode, @Nullable final Bundle bundle,
            @Nullable final String blurb) {

        assertEquals(resultCode,
                mActivity.getActivityResult().getResultCode());

        if (Activity.RESULT_OK == resultCode) {
            final Intent result = mActivity.getActivityResult().getResultData();
            assertNotNull(result);

            final Bundle extras = result.getExtras();
            BundleAssertions.assertKeyCount(extras, 2);

            BundleAssertions.assertHasString(extras,
                    LocalePluginIntent.EXTRA_STRING_BLURB, blurb);

            final Bundle pluginBundle = extras
                    .getBundle(LocalePluginIntent.EXTRA_BUNDLE);
            assertTrue(BundleComparer.areBundlesEqual(bundle, pluginBundle));
        } else if (Activity.RESULT_CANCELED == resultCode) {
            assertNull(mActivity.getActivityResult().getResultData());
        }
    }

    /**
     * @param expectedCount Expected number of calls to
     *                      {@link AbstractAppCompatPluginActivity#getResultBlurb(JSONObject)}}.
     */
    protected abstract void assertGetBlurbCount(final int expectedCount);

    /**
     * @param expectedCount Expected number of calls to
     *                      {@link AbstractAppCompatPluginActivity#getResultJson()}.
     */
    protected abstract void assertGetResultBundleCount(final int expectedCount);

    /**
     * @param expectedCount Expected number of calls to
     *                      {@link AbstractAppCompatPluginActivity#isJsonValid(JSONObject)}
     *                      .
     */
    protected abstract void assertIsBundleValidCount(final int expectedCount);

    /**
     * @param expectedCount Expected number of calls to
     *                      {@link AbstractAppCompatPluginActivity#onPostCreateWithPreviousResult(JSONObject,
     *                      String)}
     *                      .
     */
    protected abstract void assertOnPostCreateWithPreviousResultCount(final int expectedCount);

    /**
     * @param type Plug-in type.
     * @return The default Intent to start the plug-in Activity. The Intent will
     * contain
     * {@link LocalePluginIntent#EXTRA_STRING_BREADCRUMB}
     * .
     */
    @NonNull
    private static Intent getDefaultStartIntent(@NonNull final PluginType type) {
        Assertions.assertNotNull(type, "type"); //$NON-NLS-1$
        final Intent i = new Intent(type.getActivityIntentAction());

        i.putExtra(LocalePluginIntent.EXTRA_STRING_BREADCRUMB,
                "Edit Situation"); //$NON-NLS-1$

        return i;
    }

    @NonNull
    private static Bundle generateBundle(@NonNull final Context context,
            @NonNull final String value) {
        final Bundle result = new Bundle();
        result.putString(LocalePluginIntent.EXTRA_STRING_JSON,
                PluginJsonValues.generateJson(context, value).toString());

        return result;
    }

    @NonNull
    private static Bundle bundleFromJson(@NonNull final JSONObject object) {
        final Bundle result = new Bundle();
        result.putString(LocalePluginIntent.EXTRA_STRING_JSON,
                object.toString());

        return result;
    }

    @NonNull
    private String getPreviousBlurbOnMainSync(@NonNull final T activity) {
        final AtomicReference<String> result = new AtomicReference<>();
        getInstrumentation().runOnMainSync(() -> {
            result.set(activity.getPreviousBlurb());
        });
        return result.get();
    }

    @Nullable
    private JSONObject getPreviousJsonOnMainSync(@NonNull final T activity) {
        // Note that although the reference is thread safe, JSONObject is mutable and not thread safe.
        final AtomicReference<String> result = new AtomicReference<>();
        getInstrumentation().runOnMainSync(() -> {
            final JSONObject previousJson = activity.getPreviousJson();
            result.set(null != previousJson ? previousJson.toString() : null);
        });
        try {
            final String data = result.get();
            return null != data ? new JSONObject(data) : null;
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
