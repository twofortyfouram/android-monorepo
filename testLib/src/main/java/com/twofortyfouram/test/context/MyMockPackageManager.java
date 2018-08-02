package com.twofortyfouram.test.context;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.*;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Replacement for the MockPackageManager removed by Android P.
 */
/*
 * There are enough tests using this, combined with a desire to avoid requiring Mockito from this library.
 */
@SuppressWarnings("deprecation")
public class MyMockPackageManager extends PackageManager {
    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws
            PackageManager.NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int flags) throws
            PackageManager.NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] names) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent getLaunchIntentForPackage(@NonNull String packageName) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Intent getLeanbackLaunchIntentForPackage(@NonNull String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getPackageGids(@NonNull String packageName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getPackageGids(String packageName, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkPermission(String permName, String pkgName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPermissionRevokedByPolicy(@NonNull String permName, @NonNull String pkgName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addPermission(PermissionInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePermission(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int checkSignatures(int uid1, int uid2) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String[] getPackagesForUid(int uid) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getNameForUid(int uid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInstantApp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInstantApp(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInstantAppCookieMaxBytes() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public byte[] getInstantAppCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearInstantAppCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateInstantAppCookie(@Nullable byte[] cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public List<SharedLibraryInfo> getSharedLibraries(int flags) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ChangedPackages getChangedPackages(int sequenceNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSystemFeature(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSystemFeature(String name, int version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(@Nullable ComponentName caller, @Nullable Intent[] specifics, Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProviderInfo resolveContentProvider(String name, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationBanner(ApplicationInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return null;
    }

    @Override
    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return null;
    }

    @Override
    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        return null;
    }

    @Override
    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        return null;
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return null;
    }

    @Override
    public Resources getResourcesForActivity(ComponentName activityName) throws NameNotFoundException {
        return null;
    }

    @Override
    public Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException {
        return null;
    }

    @Override
    public Resources getResourcesForApplication(String appPackageName) throws NameNotFoundException {
        return null;
    }

    @Override
    public void verifyPendingInstall(int id, int verificationCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInstallerPackageName(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addPackageToPreferred(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removePackageFromPreferred(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PackageInfo> getPreferredPackages(int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearPackagePreferredActivities(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPreferredActivities(@NonNull List<IntentFilter> outFilters, @NonNull List<ComponentName> outActivities, String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getApplicationEnabledSetting(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSafeMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApplicationCategoryHint(@NonNull String packageName, int categoryHint) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public PackageInstaller getPackageInstaller() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canRequestPackageInstalls() {
        throw new UnsupportedOperationException();
    }
}
