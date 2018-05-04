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

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;

public interface BatchContractProxy {

    static Bundle newCallBundle(
            @NonNull @Size(min = 1) final ArrayList<ArrayList<ContentProviderOperation>> operationGroups) {
        return BatchContract.newCallBundle(operationGroups);
    }
}
