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

import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.memento.internal.QueryStringUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class MementoContractTest {

    @Test
    @SmallTest
    public void addSuppressNotification() {
        final Uri.Builder builder = new Uri.Builder();

        final Uri.Builder result = MementoContract.addSuppressNotification(builder);

        assertThat(QueryStringUtil.isSuppressNotification(result.build()), is(true));
    }

    @Test
    @SmallTest
    public void addSuppressNotification_same_object() {
        final Uri.Builder builder = new Uri.Builder();

        final Uri.Builder result = MementoContract.addSuppressNotification(builder);

        assertThat(builder, sameInstance(result));
    }

    @Test
    @SmallTest
    public void addLimit_same_object() {
        final Uri.Builder builder = new Uri.Builder();

        final Uri.Builder result = MementoContract.addLimit(builder, 5);

        assertThat(builder, sameInstance(result));
    }

    @Test
    @SmallTest
    public void addLimit() {
        final Uri.Builder builder = new Uri.Builder();

        final Uri.Builder result = MementoContract.addLimit(builder, 5);

        assertThat(QueryStringUtil.getLimit(result.build()), is("5")); //$NON-NLS
    }
}
