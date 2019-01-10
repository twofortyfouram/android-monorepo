/*
 * android-memento
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

package com.twofortyfouram.memento.contract;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.memento.util.Transactable;
import com.twofortyfouram.test.provider.MockableContentProvider;
import net.jcip.annotations.ThreadSafe;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.twofortyfouram.assertion.Assertions.assertNotNull;
import static com.twofortyfouram.test.matcher.ClassNotInstantiableMatcher.notInstantiable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
public final class TransactionContractTest {

    @SmallTest
    @Test
    public void nonInstantiable() {
        assertThat(TransactionContract.class, notInstantiable());
    }

    @SmallTest
    @Test
    public void runInTransaction() {
        @NonNull final Uri uri = Uri.parse("content://test/foo"); //$NON-NLS

        @NonNull final MockableContentProvider provider = MockableContentProvider.newMockProvider(ApplicationProvider.getApplicationContext(), uri.getAuthority());
        @NonNull final Bundle expectedCallResultBundle = new Bundle();
        expectedCallResultBundle.putBoolean(TestTransactable.EXTRA_RESULT_BOOLEAN, true);
        provider.addCallResult(expectedCallResultBundle);

        final Bundle expectedTransactableArgsBundle = new Bundle();
        expectedTransactableArgsBundle.putString("test_key", "test_value"); //$NON-NLS
        final Bundle actualCallResultBundle = TransactionContract.runInTransaction(provider.getContext(), uri, new TestTransactable(), expectedTransactableArgsBundle);

        assertThat(actualCallResultBundle, notNullValue());
        assertThat(actualCallResultBundle, notNullValue());
        BundleAssertions.assertKeyCount(actualCallResultBundle, 1);
        assertThat(TestTransactable.extractResult(actualCallResultBundle), is(true));

        assertThat(provider.getCallCount(), is(1));

        @NonNull final MockableContentProvider.CallParams callParams = provider.getCallParams();
        assertThat(callParams.getMethod(), is(TransactionContract.METHOD_RUN_IN_TRANSACTION));
        assertThat(callParams.getArg(), nullValue());

        final Bundle actualCallArgsBundle = callParams.getExtras();
        BundleAssertions.assertKeyCount(actualCallArgsBundle, 2);
        BundleAssertions.assertHasParcelable(actualCallArgsBundle, TransactionContract.EXTRA_BUNDLE_PARCELABLE_TRANSACTABLE, TestTransactable.class);

        final Bundle actualTransactableParamsBundle = actualCallArgsBundle.getBundle(TransactionContract.EXTRA_BUNDLE_TRANSACTABLE_DATA);
        BundleAssertions.assertHasString(actualTransactableParamsBundle, "test_key", "test_value");
    }

    @SmallTest
    @Test
    public void newCallBundle() {
        final Bundle transactableArgsBundle = new Bundle();
        transactableArgsBundle.putString("test_key", "test_value"); //$NON-NLS

        final Bundle result = TransactionContract.newCallBundle(new TestTransactable(), transactableArgsBundle);

        assertThat(result, notNullValue());
        BundleAssertions.assertKeyCount(result, 2);
        BundleAssertions.assertHasParcelable(result, TransactionContract.EXTRA_BUNDLE_PARCELABLE_TRANSACTABLE, TestTransactable.class);

        final Bundle transactableArgsBundle2 = result.getBundle(TransactionContract.EXTRA_BUNDLE_TRANSACTABLE_DATA);
        assertThat(transactableArgsBundle2, notNullValue());
        BundleAssertions.assertKeyCount(transactableArgsBundle2, 1);
        BundleAssertions.assertHasString(transactableArgsBundle2, "test_key", "test_value"); //$NON-NLS
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void newCallBundle_anonymous() {
        TransactionContract.newCallBundle(new Transactable() {
            @Nullable
            @Override
            public Bundle runInTransaction(@NonNull Context context, @NonNull Bundle bundle) {
                return null;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }
        }, new Bundle());
    }

    @SmallTest
    @Test(expected = AssertionError.class)
    public void newCallBundle_missing_parcelable_creator() {
        TransactionContract.newCallBundle(new TestTransactableMissingCreator(), new Bundle());
    }

    @ThreadSafe
    private static class TestTransactable implements Transactable {

        @NonNull
        public static final Creator<TestTransactable> CREATOR = new TestTransactableParcelableCreator();

        @NonNull
        private static final String EXTRA_RESULT_BOOLEAN = "extra_result_boolean"; //$NON-NLS

        @Nullable
        @Override
        public Bundle runInTransaction(@NonNull Context context, @NonNull Bundle bundle) {
            @NonNull final Bundle resultBundle = new Bundle();
            resultBundle.putBoolean(EXTRA_RESULT_BOOLEAN, true);

            return resultBundle;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }

        public static boolean extractResult(@NonNull final Bundle bundle) {
            assertNotNull(bundle, "bundle"); //$NON-NLS

            BundleAssertions.assertKeyCount(bundle, 1);
            BundleAssertions.assertHasBoolean(bundle, EXTRA_RESULT_BOOLEAN);

            return bundle.getBoolean(EXTRA_RESULT_BOOLEAN);
        }


        @ThreadSafe
        private static final class TestTransactableParcelableCreator
                implements Parcelable.Creator<TestTransactable> {

            @Override
            public TestTransactable createFromParcel(@NonNull final Parcel in) {
                return new TestTransactable();
            }

            @Override
            public TestTransactable[] newArray(final int size) {
                return new TestTransactable[size];
            }
        }
    }

    private static final class TestTransactableMissingCreator implements Transactable {

        @Nullable
        @Override
        public Bundle runInTransaction(@NonNull Context context, @NonNull Bundle bundle) {
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    }
}
