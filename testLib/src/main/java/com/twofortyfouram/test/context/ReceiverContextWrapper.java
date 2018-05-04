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

package com.twofortyfouram.test.context;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Test context to capture all Intents broadcasts. Intents are not broadcast to the
 * rest of the system, although ordered broadcasts are delivered directly the result receiver if
 * provided. Intents
 * broadcast through this class are stored and can be retrieved via {@link
 * #getAndClearSentIntents()}.
 */
@NotThreadSafe
public final class ReceiverContextWrapper extends ContextWrapper {

    @NonNull
    private final LinkedList<SentIntent> mIntents = new LinkedList<>();

    public ReceiverContextWrapper(@NonNull final Context targetContext) {
        super(targetContext);
    }

    /**
     * @return This object, preventing clients from breaking out of the wrapped context.
     */
    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public void sendBroadcast(final Intent intent) {
        sendBroadcast(intent, null);
    }

    @Override
    public void sendBroadcast(final Intent intent, final String receiverPermission) {
        mIntents.add(new SentIntent(intent, receiverPermission, false, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendStickyBroadcast(final Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendStickyOrderedBroadcast(final Intent intent,
            final BroadcastReceiver resultReceiver, final Handler scheduler, final int initialCode,
            final String initialData, final Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendOrderedBroadcast(final Intent intent, final String receiverPermission) {
        mIntents.add(new SentIntent(intent, receiverPermission, false, true));
    }

    @Override
    public void sendOrderedBroadcast(final Intent intent, String receiverPermission,
            final BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
            String initialData, Bundle initialExtras) {
        mIntents.add(new SentIntent(intent, receiverPermission, false, true));

        scheduler.post(new Runnable() {

            private final Intent mIntent = intent;

            @Override
            public void run() {
                Log.v("testLibTest", "Runnable running");
                resultReceiver.onReceive(ReceiverContextWrapper.this, mIntent);
            }
        });
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
            String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler,
            int initialCode, String initialData, Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user,
            BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
            String initialData, Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComponentName startService(final Intent service) {
        mIntents.add(new SentIntent(service, null, false, false));
        return null;
    }

    @NonNull
    public Collection<SentIntent> getAndClearSentIntents() {
        try {
            return new LinkedList<>(mIntents);
        } finally {
            mIntents.clear();
        }
    }

    /**
     * @return Polls the oldest Intent sent through this context.  May be null if no Intents are
     * remaining to be polled.
     */
    @Nullable
    public SentIntent pollIntent() {
        return mIntents.poll();
    }

    /**
     * Represents an Intent that was sent through
     */
    @Immutable
    public static final class SentIntent {

        @NonNull
        private final Intent mIntent;

        @Nullable
        private final String mPermission;

        private final boolean mIsSticky;

        private final boolean mIsOrdered;

        private SentIntent(@NonNull final Intent intent, @Nullable final String permission,
                final boolean isSticky, final boolean isOrdered) {
            if (null == intent) {
                throw new AssertionError();
            }

            mIntent = new Intent(intent);
            mPermission = permission;
            mIsSticky = isSticky;
            mIsOrdered = isOrdered;
        }

        /**
         * @return The Intent that was broadcast through {@link ReceiverContextWrapper}.
         * Note that this method always returns a new copy, to prevent exposing the internals of
         * this class.
         */
        @NonNull
        public Intent getIntent() {
            return new Intent(mIntent);
        }

        /**
         * @return The permission enforced on the Intent or null if there was no permission.
         */
        @Nullable
        public String getPermission() {
            return mPermission;
        }

        /**
         * @return True if the Intent is sticky.
         */
        public boolean getIsSticky() {
            return mIsSticky;
        }

        /**
         * @return If the Intent is ordered.
         */
        public boolean getIsOrdered() {
            return mIsOrdered;
        }
    }
}
