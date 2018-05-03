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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.locale.api.LocalePluginIntent;
import com.twofortyfouram.locale.sdk.host.test.fixture.JsonBundleSerializerFixture;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public final class JsonBundleSerializerTest {

    @SmallTest
    @Test
    public void isJsonSerializable_true() {
        final Bundle bundle = JsonBundleSerializerFixture.newEmptySerializableBundle();

        assertThat(new JsonBundleSerializer().isSerializable(bundle), is(true));
    }

    @SmallTest
    @Test
    public void isJsonSerializable_too_many_extras() {
        final Bundle bundle = JsonBundleSerializerFixture.newEmptySerializableBundle();
        bundle.putString("foo", "bar"); //$NON-NLS

        assertThat(new JsonBundleSerializer().isSerializable(bundle), is(false));
    }

    @SmallTest
    @Test
    public void isJsonSerializable_missing_extra() {
        final Bundle bundle = new Bundle();

        assertThat(new JsonBundleSerializer().isSerializable(bundle), is(false));
    }

    @SmallTest
    @Test
    public void isJsonSerializable_wrong_type() {
        final Bundle bundle = new Bundle();
        bundle.putInt(LocalePluginIntent.EXTRA_STRING_JSON, 1);

        assertThat(new JsonBundleSerializer().isSerializable(bundle), is(false));
    }

    @SmallTest
    @Test
    public void isJsonSerializable_bad_json() {
        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, "beep"); //$NON-NLS

        assertThat(new JsonBundleSerializer().isSerializable(bundle), is(false));
    }

    @SmallTest
    @Test
    public void serialize() {
        final JSONObject obj = new JsonBundleSerializer()
                .serialize(JsonBundleSerializerFixture.newEmptySerializableBundle());

        assertThat(obj, notNullValue());
        assertThat(obj.length(), is(0));
    }


    @SmallTest
    @Test(expected = BundleSerializer.BundleSerializationException.class)
    public void serialize_missing_extra() {
        final Bundle bundle = new Bundle();

        new JsonBundleSerializer().serialize(bundle);
    }

    @SmallTest
    @Test(expected = BundleSerializer.BundleSerializationException.class)
    public void serialize_bad_json() {
        final Bundle bundle = new Bundle();
        bundle.putString(LocalePluginIntent.EXTRA_STRING_JSON, "beep"); //$NON-NLS

        new JsonBundleSerializer().serialize(bundle);
    }

    @SmallTest
    @Test
    public void deserialize() {
        final Bundle bundle = new JsonBundleSerializer()
                .deserialize(new JSONObject());

        assertThat(bundle, notNullValue());
        BundleAssertions.assertHasString(bundle, LocalePluginIntent.EXTRA_STRING_JSON, false, false);
    }
}
