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

package com.twofortyfouram.memento.service;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public final class AbstractContentProviderOperationServiceTest {

    @SmallTest
    @Test
    public void newExtras() {
        final Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority("bork")
                .build();
        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newAssertQuery(uri).withExpectedCount(1).build());
        final ArrayList<ArrayList<ContentProviderOperation>> opsGroups
                = new ArrayList<>();
        opsGroups.add(ops);

        final Bundle bundle = AbstractContentProviderOperationService.newExtras(uri, opsGroups);

        BundleAssertions.assertHasKey(bundle,
                AbstractContentProviderOperationService.EXTRA_SERIALIZABLE_ARRAY_LIST_OF_ARRAY_LIST_OF_OPERATIONS);
        BundleAssertions.assertHasKey(bundle,
                AbstractContentProviderOperationService.EXTRA_PARCELABLE_URI);
        BundleAssertions.assertKeyCount(bundle, 2);
    }

}
