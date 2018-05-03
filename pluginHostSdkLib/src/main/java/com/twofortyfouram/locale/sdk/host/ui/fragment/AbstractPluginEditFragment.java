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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.internal.PluginEditDelegate;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginErrorEdit;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.bundle.BundleComparer;

import net.jcip.annotations.NotThreadSafe;

import java.util.EnumSet;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;

/**
 * UI-less Fragment to handle communication between the host UI and the plug-in
 * UI. This Fragment will handle launching the plug-in's edit screen, process
 * the Activity result, and deliver a callback to subclasses via
 * {@link IPluginEditFragment#handleSave(IPlugin, PluginInstanceData)}.
 * <p>
 * After a plug-in is edited, this Fragment will remove itself automatically.
 * <p>
 * When starting this fragment, the argument
 * {@link IPluginEditFragment#ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN} is required. The optional
 * argument {@link IPluginEditFragment#ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA} is used
 * if an old plug-in
 * instance is being edited.
 */
@NotThreadSafe
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class AbstractPluginEditFragment extends Fragment implements IPluginEditFragment {

    /**
     * Type: {@code boolean}.
     */
    @NonNull
    private static final String STATE_BOOLEAN_IS_SAVED_INSTANCE =
            AbstractSupportPluginEditFragment.class.getName() + ".state.BOOLEAN_IS_SAVED_INSTANCE";
    //$NON-NLS

    /**
     * Request code for launching a plug-in "edit" Activity.  Subclasses cannot use this request
     * code.
     */
    protected static final int REQUEST_CODE_EDIT_PLUGIN = 1;

    /**
     * The plug-in that is currently being edited.
     */
    @Nullable
    private IPlugin mPlugin = null;

    /**
     * Optional previous instance data of the plug-in being edited.
     */
    @Nullable
    private PluginInstanceData mPreviousPluginInstanceData = null;

    /**
     * Builds a new instance of the Fragment's required and optional arguments.
     *
     * @param plugin                     The plug-in to edit with this Fragment.
     * @param previousPluginInstanceData The optional previously saved plug-in
     *                                   instance.
     * @return Args necessary for starting {@link AbstractPluginEditFragment}.
     */
    @NonNull
    public static Bundle newArgs(@NonNull final IPlugin plugin,
            @Nullable final PluginInstanceData previousPluginInstanceData) {
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$

        return PluginEditDelegate.newArgs(plugin, previousPluginInstanceData);
    }

    /*
     * Suppress warning, because this will be used until the SDK targets API 23 or greater.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        final Bundle args = getArguments();
        if (null == args) {
            throw new IllegalArgumentException("arguments are missing"); //$NON-NLS-1$
        }

        mPlugin = PluginEditDelegate.getCurrentPlugin(args);
        mPreviousPluginInstanceData = PluginEditDelegate.getPreviousPluginInstanceData(args);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState) {
            String breadcrumb = null;
            final ActionBar ab = requireActivity().getActionBar();
            if (null != ab) {
                final CharSequence subtitle = ab.getSubtitle();
                if (null != subtitle) {
                    breadcrumb = subtitle.toString();
                }
            }

            final Intent i = PluginEditDelegate.getPluginStartIntent(mPlugin,
                    mPreviousPluginInstanceData, breadcrumb);
            try {
                startActivityForResult(i, REQUEST_CODE_EDIT_PLUGIN);
            } catch (final ActivityNotFoundException e) {
                handleErrorsInternal(mPlugin,
                        EnumSet.of(PluginErrorEdit.ACTIVITY_NOT_FOUND_EXCEPTION));
            } catch (final SecurityException e) {
                handleErrorsInternal(mPlugin, EnumSet.of(PluginErrorEdit.SECURITY_EXCEPTION));
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // It appears necessary to put *something* in the bundle so that savedInstanceState will be
        // non-null in onCreate() if the fragment is re-created.
        outState.putBoolean(STATE_BOOLEAN_IS_SAVED_INSTANCE, true); //$NON-NLS
    }

    /**
     * Processes sub-activity results for plug-ins being edited {@inheritDoc}
     */
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
            @Nullable final Intent intent) {

        if (REQUEST_CODE_EDIT_PLUGIN == requestCode) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Lumberjack.always("Received result code RESULT_OK"); //$NON-NLS-1$

                    final EnumSet<PluginErrorEdit> errors = PluginEditDelegate
                            .isIntentValid(intent, mPlugin);
                    if (errors.isEmpty()) {
                        final Bundle newBundle = intent
                                .getBundleExtra(LocalePluginIntent.EXTRA_BUNDLE);
                        final String newBlurb = intent
                                .getStringExtra(
                                        LocalePluginIntent.EXTRA_STRING_BLURB);

                        Bundle previousBundle = null;
                        String previousBlurb = null;
                        if (null != mPreviousPluginInstanceData) {
                            previousBundle = mPreviousPluginInstanceData.getBundle();
                            previousBlurb = mPreviousPluginInstanceData.getBlurb();
                        }

                        handleSaveInternal(newBundle, newBlurb,
                                previousBundle, previousBlurb);
                    } else {
                        handleErrorsInternal(mPlugin, errors);
                    }

                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Lumberjack.always("Received result code RESULT_CANCELED"); //$NON-NLS-1$
                    handleCancelInternal(mPlugin);
                    break;
                }
                default: {
                    Lumberjack.always("ERROR: Received illegal result code %d", //$NON-NLS-1$
                            resultCode);
                    handleErrorsInternal(mPlugin,
                            EnumSet.of(PluginErrorEdit.UNKNOWN_ACTIVITY_RESULT_CODE));
                    /*
                     * Although this shouldn't happen, don't throw an exception
                     * because bad 3rd party apps could give bad result codes
                     */

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * Called when a plug-in is canceled.
     *
     * @param plugin The plug-in.
     */
    private void handleCancelInternal(@NonNull final IPlugin plugin) {
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$
        Lumberjack.v("Plug-in canceled"); //$NON-NLS-1$
        handleCancel(plugin);

        removeSelf();
    }

    /**
     * Called when a plug-in has an error.
     *
     * @param plugin The plug-in
     * @param errors The errors that occurred.
     */
    private void handleErrorsInternal(@NonNull final IPlugin plugin,
            @NonNull final EnumSet<PluginErrorEdit> errors) {
        Lumberjack.v("Encountered errors: %s", errors); //$NON-NLS-1$

        handleErrors(plugin, errors);

        removeSelf();
    }

    /**
     * Internal implementation to handle when a plug-in is ready to be saved.
     *
     * @param newBundle The new plug-in Bundle.
     * @param newBlurb  The new plug-in blurb.
     * @param oldBundle The old plug-in Bundle. This parameter may be
     *                  {@code null} if this is a new plug-in instance, and not
     *                  {@code null} if this is an old plug-in instance that is being
     *                  edited.
     * @param oldBlurb  The old plug-in blurb. This parameter may be {@code null}
     *                  if this is a new plug-in instance, and not {@code null} if
     *                  this is an old plug-in instance that is being edited.
     */
    private void handleSaveInternal(@NonNull final Bundle newBundle, @NonNull final String newBlurb,
            @Nullable final Bundle oldBundle, @Nullable final String oldBlurb) {

        if (!newBlurb.equals(oldBlurb) || !BundleComparer.areBundlesEqual(newBundle, oldBundle)) {
            final EnumSet<PluginErrorEdit> errors = EnumSet.noneOf(PluginErrorEdit.class);

            // Kind of icky that the serialization work is being done just for validation, but
            // this isn't really going to be that slow so it shouldn't matter much.
            final byte[] serializedBundle = PluginEditDelegate.serializeBundle(newBundle, errors);
            if (null != serializedBundle) {
                handleSave(mPlugin,
                        new PluginInstanceData(mPlugin.getType(), mPlugin.getRegistryName(),
                                newBundle, newBlurb)
                );
                removeSelf();
            } else {
                handleErrorsInternal(mPlugin, errors);
            }
        } else {
            removeSelf();
        }
    }

    private void removeSelf() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
