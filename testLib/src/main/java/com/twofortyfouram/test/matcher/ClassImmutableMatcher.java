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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.twofortyfouram.test.internal.Assertions.assertNotNull;

/**
 * Matcher to verify that a class is probably immutable.  Typical use case is for model objects
 * with final fields.  Does not verify that mutable fields are correctly encapsulated.
 */
@ThreadSafe
public final class ClassImmutableMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    @NonNull
    public static ClassImmutableMatcher immutable() {
        return new ClassImmutableMatcher();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("An immutable class"); //$NON-NLS
    }

    @Override
    protected boolean matchesSafely(@NonNull final Class<?> cls,
            @NonNull final Description description) {
        assertNotNull(cls, "cls"); //$NON-NLS
        assertNotNull(description, "description"); //$NON-NLS

        final Collection<Field> nonFinalFields = getNonFinalFields(cls);
        if (!nonFinalFields.isEmpty()) {
            description.appendText(
                    String.format(Locale.US, "Class has non-final fields %s",
                            nonFinalFields)); //$NON-NLS
            return false;
        }

        return true;
    }

    /*package*/
    static Collection<Field> getNonFinalFields(@NonNull final Class<?> cls) {
        assertNotNull(cls, "cls"); //$NON-NLS

        final List<Field> nonFinalFields = new LinkedList<>();

        for (final Field field : cls.getDeclaredFields()) {

            final boolean isFinalModifier;
            {
                final int modifiers = field.getModifiers();
                isFinalModifier = Modifier.isFinal(modifiers);
            }

            if (!isFinalModifier) {
                nonFinalFields.add(field);
            }
        }

        return nonFinalFields;
    }

}
