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

import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Display;

import java.io.*;

@SuppressWarnings("deprecation")
public class MyMockContext extends Context {
    @Override
    public AssetManager getAssets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resources getResources() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageManager getPackageManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentResolver getContentResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Looper getMainLooper() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context getApplicationContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTheme(int resid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resources.Theme getTheme() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPackageName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPackageResourcePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPackageCodePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteSharedPreferences(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteFile(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFileStreamPath(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getDataDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFilesDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getNoBackupFilesDir() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getObbDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File[] getObbDirs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCacheDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getCodeCacheDir() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File[] getExternalCacheDirs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File[] getExternalMediaDirs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] fileList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getDir(String name, int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteDatabase(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getDatabasePath(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] databaseList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getWallpaper() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable peekWallpaper() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWallpaper() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startActivity(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startActivities(Intent[] intents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcast(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String receiverPermission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String receiverPermission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stopService(Intent service) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean bindService(Intent service, @NonNull ServiceConnection conn, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbindService(@NonNull ServiceConnection conn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName className, @Nullable String profileFile, @Nullable Bundle arguments) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> serviceClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkPermission(@NonNull String permission, int pid, int uid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkCallingPermission(@NonNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkSelfPermission(@NonNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforcePermission(@NonNull String permission, int pid, int uid, @Nullable String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceCallingPermission(@NonNull String permission, @Nullable String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String permission, @Nullable String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags, @Nullable String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context createDeviceProtectedStorageContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeviceProtectedStorage() {
        throw new UnsupportedOperationException();
    }
}
