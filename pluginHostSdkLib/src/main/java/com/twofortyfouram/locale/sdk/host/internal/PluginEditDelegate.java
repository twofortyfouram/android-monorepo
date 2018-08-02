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


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.model.IPlugin;
import com.twofortyfouram.locale.sdk.host.model.PluginErrorEdit;
import com.twofortyfouram.locale.sdk.host.model.PluginInstanceData;
import com.twofortyfouram.locale.sdk.host.ui.fragment.AbstractPluginEditFragment;
import com.twofortyfouram.locale.sdk.host.ui.fragment.IPluginEditFragment;
import com.twofortyfouram.locale.sdk.host.util.BinaryBundleSerializer;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.bundle.BundleScrubber;

import net.jcip.annotations.ThreadSafe;

import java.util.EnumSet;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.log.Lumberjack.formatMessage;

/**
 * Delegate for plug-in edit behavior.
 */
@ThreadSafe
public final class PluginEditDelegate {

    @NonNull
    public static EnumSet<PluginErrorEdit> isIntentValid(@Nullable final Intent intent,
            @NonNull final IPlugin plugin) {
        final EnumSet<PluginErrorEdit> errors = EnumSet.noneOf(PluginErrorEdit.class);

        if (null == intent) {
            errors.add(PluginErrorEdit.INTENT_NULL);
        } else {
            if (BundleScrubber.scrub(intent)) {
                errors.add(PluginErrorEdit.PRIVATE_SERIALIZABLE);
            }

            final Bundle intentExtras = intent.getExtras();

            if (null == intentExtras) {
                errors.add(PluginErrorEdit.BLURB_MISSING);
                errors.add(PluginErrorEdit.BUNDLE_MISSING);
            } else {
                final Bundle localeBundle = intentExtras
                        .getBundle(LocalePluginIntent.EXTRA_BUNDLE);

                if (BundleScrubber.scrub(localeBundle)) {
                    errors.add(PluginErrorEdit.PRIVATE_SERIALIZABLE);
                }

                if (null == localeBundle) {
                    if (plugin.getConfiguration().isBackwardsCompatibilityEnabled()) {
                        final Bundle newBundle = new Bundle(intent.getExtras());
                        newBundle.remove(LocalePluginIntent.EXTRA_STRING_BLURB);
                        intent.putExtra(LocalePluginIntent.EXTRA_BUNDLE,
                                newBundle);
                    }

                    errors.add(PluginErrorEdit.BUNDLE_MISSING);
                }

                try {
                    BundleAssertions.assertHasString(intentExtras,
                            LocalePluginIntent.EXTRA_STRING_BLURB, false, true);
                } catch (final AssertionError e) {
                    errors.add(PluginErrorEdit.BLURB_MISSING);
                }
            }
        }

        return errors;
    }


    @NonNull
    public static Intent getPluginStartIntent(@NonNull final IPlugin plugin,
            @Nullable final PluginInstanceData pluginInstanceData,
            @Nullable final String breadcrumb) {
        assertNotNull(plugin, "plugin"); //$NON-NLS-1$

        final Intent intent = new Intent(plugin.getType().getActivityIntentAction());
        intent.setClassName(plugin.getPackageName(), plugin.getActivityClassName());
        intent.putExtra(LocalePluginIntent.EXTRA_STRING_BREADCRUMB, breadcrumb);

        if (null != pluginInstanceData) {
            final Bundle bundle = pluginInstanceData.getBundle();

            intent.putExtra(LocalePluginIntent.EXTRA_BUNDLE, bundle);
            intent.putExtra(LocalePluginIntent.EXTRA_STRING_BLURB,
                    pluginInstanceData.getBlurb());

            if (plugin.getConfiguration().isBackwardsCompatibilityEnabled()) {
                intent.putExtras(bundle);
            }
        }

        return intent;
    }

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
        if (null != previousPluginInstanceData) {
            if (plugin.getType() != previousPluginInstanceData.getType()) {
                throw new AssertionError(Lumberjack.formatMessage(
                        "plugin.getType()=%s while previousPluginInstanceData.getType()=%s",
                        //$NON-NLS-1$

                        plugin.getType(), previousPluginInstanceData.getType()));
            }
        }

        final Bundle argsBundle = new Bundle();
        argsBundle.putParcelable(IPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN, plugin);

        if (null != previousPluginInstanceData) {
            argsBundle.putParcelable(
                    IPluginEditFragment.ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA,
                    previousPluginInstanceData);
        }

        return argsBundle;
    }

    @Nullable
    public static byte[] serializeBundle(@NonNull final Bundle bundle,
            @NonNull final EnumSet<PluginErrorEdit> errors) {
        assertNotNull(bundle, "bundle"); //$NON-NLS-1$

        byte[] serializedBundle = null;
        try {
            serializedBundle =
                    new BinaryBundleSerializer()
                            .serialize(bundle);
        } catch (final Exception e) {
            errors.add(PluginErrorEdit.BUNDLE_NOT_SERIALIZABLE);
            serializedBundle = null;
        }

        if (null != serializedBundle) {
            if (PluginInstanceData.MAXIMUM_BUNDLE_SIZE_BYTES < serializedBundle.length) {
                errors.add(PluginErrorEdit.BUNDLE_TOO_LARGE);
                serializedBundle = null;
            }
        }

        return serializedBundle;
    }

    @NonNull
    public static IPlugin getCurrentPlugin(@NonNull final Bundle bundle) {
        final Parcelable currentPluginArg = bundle.getParcelable(
                IPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN);
        if (null == currentPluginArg) {
            throw new IllegalArgumentException(formatMessage(
                    "Arg %s is missing",
                    IPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN)); //$NON-NLS-1$
        }
        if (currentPluginArg instanceof IPlugin) {
            //noinspection CastToConcreteClass
            return (IPlugin) currentPluginArg;
        } else {
            throw new IllegalArgumentException(formatMessage(
                    "Arg %s is the wrong type",
                    IPluginEditFragment.ARG_EXTRA_PARCELABLE_CURRENT_PLUGIN)); //$NON-NLS-1$
        }
    }

    @Nullable
    public static PluginInstanceData getPreviousPluginInstanceData(@NonNull final Bundle bundle) {
        final Parcelable previousPluginInstanceDataArg = bundle.getParcelable(
                IPluginEditFragment.ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA);

        if (null != previousPluginInstanceDataArg) {
            if (previousPluginInstanceDataArg instanceof PluginInstanceData) {
                //noinspection CastToConcreteClass
                return (PluginInstanceData) previousPluginInstanceDataArg;
            } else {
                throw new IllegalArgumentException(formatMessage(
                        "Arg %s is the wrong type", //$NON-NLS-1$
                        IPluginEditFragment.ARG_EXTRA_PARCELABLE_PREVIOUS_PLUGIN_INSTANCE_DATA));
            }
        }

        return null;
    }
}
