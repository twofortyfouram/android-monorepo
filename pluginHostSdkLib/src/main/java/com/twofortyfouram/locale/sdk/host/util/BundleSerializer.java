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

package com.twofortyfouram.locale.sdk.host.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface BundleSerializer<T> {

    /**
     * @param bundle Bundle to test for serializability.
     * @return True if {@code bundle} can be serialized by this serializer.
     */
    boolean isSerializable(@NonNull final Bundle bundle);

    /**
     * @param bundle Bundle that is to be serialized.
     * @return A serialized representation of the Bundle.
     * @throws BundleSerializationException If the bundle cannot be serialized.
     * @see #deserialize(Object)
     */
    @NonNull
    T serialize(@NonNull final Bundle bundle) throws BundleSerializationException;

    /**
     * @param previouslySerialized Bundle that was serialized via {@link #serialize(Bundle)}
     * @return A deserialized representation of the Bundle.
     * @throws BundleSerializationException If the bundle cannot be deserialized.
     * @see #serialize(Bundle)
     */
    @NonNull
    Bundle deserialize(@NonNull final T previouslySerialized) throws BundleSerializationException;

    class BundleSerializationException extends RuntimeException {

        private static final long serialVersionUID = -4558002020384742956L;

        public BundleSerializationException(@NonNull final String message) {
            super(message);
        }

        public BundleSerializationException(@NonNull final Exception e) {
            super(e);
        }

        public BundleSerializationException(@NonNull final String message,
                @NonNull final Exception e) {
            super(message, e);
        }

    }
}
