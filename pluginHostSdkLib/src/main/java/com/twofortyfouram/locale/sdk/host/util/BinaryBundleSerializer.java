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

package com.twofortyfouram.locale.sdk.host.util;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.bundle.BundleKeyComparator;

import net.jcip.annotations.ThreadSafe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.log.Lumberjack.formatMessage;

/**
 * For plug-ins implementing the Plug-in API for Locale 1.0.0.
 *
 * This class serializes/deserializes a {@code Bundle} into a persistent
 * {@code byte[]} form.
 * <p>
 * Note: this class does not support Bundles in a circular object graph and the
 * result of attempting to do so is undefined. Android's {@code Parcelable}
 * interface doesn't support circular object graphs either, so the implications
 * of this limitation are likely minor.
 */
@ThreadSafe
public final class BinaryBundleSerializer {
    //@formatter:off
    /* The serialized form consists of:
    *     1. An int primitive representing the version of the serialized stream.
    *     2. A series of (control code, type, key, value) tuples representing the data of the Bundle.
    *     3. The control code indicates what should happen next:
    *         a. CONTROL_CODE_END indicates the end of the serialized stream.
    *         b. CONTROL_CODE_CONTINUE indicates more items to deserialize from the stream.
    *     4. The TYPE indicates what type of item comes next:
    *         a. TYPE_SERIALIZABLE indicates a plain serialized Java Object:
    *             i.  Key is the String key that identifies the item in the Bundle.
    *             ii. Value is the serialized Object data, that should be deserialized based on the previous TYPE code.
    *                 Currently, only TYPE_SERIALIZABLE exists.  The simplicity of this design is that even primitives
    *                 are properly handled, as they are converted to Object instances via autoboxing.  This design also
    *                 allows for other types, such as optimizing for primitives, in a future version.
    *         b. TYPE_BUNDLE indicates a recursively stored Bundle, and the following data starts at step 1 again.
    */
    //@formatter:on

    /**
     * Version number of the serialized form. If this changes, it represents an incompatible change
     * in the serialized form.
     */
    private static final int VERSION_1 = 1;

    /**
     * Indicates the end of the serialized stream.
     */
    private static final int CONTROL_CODE_END = 0;

    /**
     * Indicates the continuation of the serialized stream.
     */
    private static final int CONTROL_CODE_CONTINUE = 1;

    /**
     * The following part of the stream is read in as a {@code Serializable}.
     */
    private static final int TYPE_SERIALIZABLE = 0;

    /**
     * The following part of the stream is read in as a recursive byte[] that represents a
     * {@code Bundle}.
     */
    private static final int TYPE_BUNDLE = 1;

    /**
     * Serializes an instance of {@code Bundle} to a {@code byte[]}.
     * <p>
     * Note: all values of the {@code Bundle} must be either a primitive or implement
     * {@code Serializable}.
     */
    @NonNull
    public byte[] serialize(@NonNull final Bundle bundleToSerialize)
            throws BundleSerializer.BundleSerializationException {
        assertNotNull(bundleToSerialize, "bundleToSerialize"); //$NON-NLS-1$

        ObjectOutputStream objectOut = null; // needs to be closed
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); // closing has no effect
        try {
            objectOut = new ObjectOutputStream(byteOut);

            objectOut.writeInt(VERSION_1);

            /*
             * Converting to a TreeSet forces the keys to be sorted, which provides consistent
             * ordering of the output results. This is NOT a public interface commitment that the
             * serialized form will be sorted by the keys, although this sorting does make it easier
             * to perform regression tests.
             */
            final Set<String> keys = new TreeSet<>(new BundleKeyComparator());
            keys.addAll(bundleToSerialize.keySet());

            int count = 0;
            final int size = keys.size();

            if (0 == size) {
                objectOut.writeInt(CONTROL_CODE_END);
            } else {
                objectOut.writeInt(CONTROL_CODE_CONTINUE);
            }

            for (final String key : keys) {
                final Object value = bundleToSerialize.get(key);

                Lumberjack.d("Serializing %s", key); //$NON-NLS-1$

                if (value instanceof Bundle) {
                    objectOut.writeInt(TYPE_BUNDLE);

                    objectOut.writeObject(key);

                    /*
                     * Oh boy, recursive!
                     */
                    objectOut.writeObject(serialize((Bundle) value));
                } else if (value instanceof Serializable || null == value) {
                    objectOut.writeInt(TYPE_SERIALIZABLE);

                    objectOut.writeObject(key);
                    objectOut.writeObject(bundleToSerialize.get(key));
                } else {
                    throw new BundleSerializer.BundleSerializationException(
                            formatMessage(
                                    "Key \"%s\"'s value %s isn't Serializable.  Only primitives or objects implementing Serializable can be stored.  Parcelable is not stable for long-term storage.",
                                    //$NON-NLS-1$
                                    key, bundleToSerialize.get(key)
                            )
                    );
                }

                count++;
                if (count == size) {
                    objectOut.writeInt(CONTROL_CODE_END);
                } else {
                    objectOut.writeInt(CONTROL_CODE_CONTINUE);
                }
            }
        } catch (final IOException e) {
            // if this happens, we've really screwed the pooch and really can't recover
            throw new BundleSerializer.BundleSerializationException(
                    "IOException when serializing to byte[]", e); //$NON-NLS-1$
        } finally {
            try {
                if (null != objectOut) {
                    objectOut.close();
                    objectOut = null;
                }
            } catch (final IOException e) {
                throw new BundleSerializer.BundleSerializationException(
                        "IOException when closing ObjectOutputStream",
                        e); //$NON-NLS-1$
            }
        }
        return byteOut.toByteArray();
    }

    /**
     * Deserializes an instance of {@code Bundle} from a {@code byte[]}.
     *
     * @param bytesToDeserialize A {@code byte[]} representing the output of the
     *                           {@link #serialize(Bundle)} method of this class.
     * @return Deserialized instance of the {@code Bundle} previously sent through
     * {@link #serialize(Bundle)}
     */
    @NonNull
    public Bundle deserialize(@NonNull final byte[] bytesToDeserialize)
            throws BundleSerializer.BundleSerializationException {
        assertNotNull(bytesToDeserialize, "bytesToDeserialize"); //$NON-NLS-1$

        ObjectInputStream objectIn = null; // needs to be closed
        try {
            objectIn = new ObjectInputStream(new ByteArrayInputStream(bytesToDeserialize));

            final Bundle returnBundle = new Bundle();

            /*
             * Switch on the version number, which is the first item of the serialized stream
             */
            final int version = objectIn.readInt();
            switch (version) {
                case VERSION_1: {
                    while (CONTROL_CODE_CONTINUE == objectIn.readInt()) {
                        final int type = objectIn.readInt();
                        switch (type) {
                            case TYPE_SERIALIZABLE: {
                                final String key = (String) objectIn.readObject();
                                final Serializable value = (Serializable) objectIn.readObject();
                                returnBundle.putSerializable(key, value);

                                break;
                            }
                            case TYPE_BUNDLE: {
                                final String key = (String) objectIn.readObject();
                                final Bundle value = deserialize((byte[]) objectIn
                                        .readObject());

                                returnBundle.putBundle(key, value);

                                break;
                            }
                            default: {
                                throw new BundleSerializer.BundleSerializationException(
                                        formatMessage(
                                                "Type %d unrecognized", type)); //$NON-NLS-1$
                            }
                        }
                    }
                    break;
                }
                default: {
                    throw new BundleSerializer.BundleSerializationException(formatMessage(
                            "Version %d unrecognized", version)); //$NON-NLS-1$
                }
            }

            Lumberjack.d("Deserialized bundle is: %s", returnBundle); //$NON-NLS-1$
            return returnBundle;

        } catch (final ClassNotFoundException e) {
            throw new BundleSerializer.BundleSerializationException(e);
        } catch (final IOException e) {
            // if this happens, we've really screwed the pooch and really can't recover
            throw new BundleSerializer.BundleSerializationException(
                    "IOException when deserializing", e); //$NON-NLS-1$
        } finally {
            try {
                if (null != objectIn) {
                    objectIn.close();
                    objectIn = null;
                }
            } catch (final IOException e) {
                throw new BundleSerializer.BundleSerializationException(
                        "IOException when closing ObjectInputStream",
                        e); //$NON-NLS-1$
            }
        }
    }
}
