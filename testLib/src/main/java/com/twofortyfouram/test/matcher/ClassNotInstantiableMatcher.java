/*
 * android-test
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

package com.twofortyfouram.test.matcher;


import androidx.annotation.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.twofortyfouram.test.internal.Assertions.assertNotNull;

/**
 * Matcher to verify that a class is not instantiable.  Typical use case is for utility classes
 * that have a private default constructor that throws an {@link AssertionError}.
 */
@ThreadSafe
public final class ClassNotInstantiableMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    @NonNull
    public static ClassNotInstantiableMatcher notInstantiable() {
        return new ClassNotInstantiableMatcher();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A non-instantiable class"); //$NON-NLS
    }

    @Override
    protected boolean matchesSafely(@NonNull final Class<?> cls,
            @NonNull final Description description) {
        assertNotNull(cls, "cls"); //$NON-NLS
        assertNotNull(description, "description"); //$NON-NLS

        if (0 > getNumberOfPublicConstructors(cls)) {
            description.appendText("Class has public constructors"); //$NON-NLS
            return false;
        }

        if (isPrivateConstructorAccessible(cls)) {
            description.appendText(
                    "Class can be instantiated through a private constructor"); //$NON-NLS
            return false;
        }

        return true;
    }

    private static int getNumberOfPublicConstructors(@NonNull final Class<?> cls) {
        assertNotNull(cls, "cls"); //$NON-NLS

        return cls.getConstructors().length;
    }

    private static boolean isPrivateConstructorAccessible(@NonNull final Class<?> cls) {
        assertNotNull(cls, "cls"); //$NON-NLS

        try {
            final Constructor<?> constructor = cls.getDeclaredConstructor();

            constructor.setAccessible(true);

            try {
                constructor.newInstance();
                return true;
            } catch (final IllegalArgumentException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            } catch (final InvocationTargetException e) {
                if (e.getCause() instanceof UnsupportedOperationException) {
                    /*
                     * Expected exception, because the private constructor should throw
                     * UnsupportedOperationException.
                     */
                } else {
                    throw new RuntimeException(e);
                }
            }
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
