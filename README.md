# Overview
This repository contains multiple open source libraries.  Because some of the libraries are interdependent, putting them in a single repository (monorepo) makes maintaining these libraries and releasing them easier.

Although details of each library are below, the high level is:

 * Annotation — Simple annotations to better document code.
 * Assertion — Runtime assertions.
 * Memento — A SQLite-based Content Provider implementation, with both an API and implementation layer.
 * Plugin SDKs for Locale — A combination of API specification, client SDK, and host SDK.
 * Spackle — A hodgepodge of utilities, mostly intended to be internal support the other modules.
 * Test — Fills in some gaps in the Android test framework.

# Versioning
Because of interdependencies, any modification to a single library causes all libraries to be republished in lockstep with a new version number.  The new version number is semantic, based on whether the change was a bug fix, feature, or incompatible change.

Consider an example: memento depends on spackle.  Both are at version 1.0.0.  If a minor bug fix is made to spackle, both libraries will be released with version 1.0.1.  In other words, memento will be re-released although it didn't change (only its transitive dependency changed).

Consider another example: memento depends on spackle.  Both are at version 1.0.0.  If a minor bug fix is made to memento, both libraries will be released with version 1.0.1.  In other words, spackle will be re-released although it didn't change, and none of its dependencies changed.

Although this pattern may change in the future, this seems to be a reasonable balance given the need to continuosly improve these libraries.

# Building
The entire repo can be easily checked out and built locally by running `./gradlew assemble`.

# Contributing
Contributions are welcome, although contributors will need to sign a Contributor License Agreement (CLA).  Please contact up@244.am.

# Annotation

# Assertion

# Memento
## Overview
Memento makes it easy to persistently store data for your Android app in a SQLite database via the ContentProvider interface.

A ContentProvider is just an abstraction to some underlying data storage mechanism.  The ContentProvider API combines REST and SQL, where data is accessed via URIs (REST-like) and arguments (SQL-like).  Although any storage mechanism could be used, SQLite is common.  ContentProvider is a great abstraction because Android provides many features on top of the ContentProvider interface such as inter-process communication, observer design patterns, permissions, syncing, and multi-threading facilities.

The reason to use this library, rather than roll your own, is that Memento dramatically simplifies the process of implementing and testing a ContentProvider that is DRY, thread-safe, and well tested.  Android ContentProvider design patterns usually combine the business logic and the data model, such that an implementation may have a `UriMatcher` and a repetitive switch statement within each of `query()`, `insert()`, `update()`, and `delete()`.  Within those methods, there are concerns not only about the data model but also SQL injection, permissions, transactions, thread-safety, observer notification, and so on.  Memento, on the other hand, takes care of implementing the business logic so that the developer only has to worry about implementing the data model.  In addition, Memento provides facilities to simplify data model creation.  Memento ensures minimal lock-in: because the app talks to the ContentProvider via the ContentResolver, that layer of indirection makes it easy to remove the dependency on the Memento library in the future.

Additional features include:

* Extensive unit testing, as well as real-world use in top apps on the Google Play Store
* Multi-process and multi-thread safety
* Atomic transactions for [applyBatch(ArrayList)](https://developer.android.com/reference/android/content/ContentProvider.html#applyBatch(java.util.ArrayList<android.content.ContentProviderOperation>)) and [bulkInsert(Uri, ContentValues[])](http://developer.android.com/reference/android/content/ContentProvider.html#bulkInsert(android.net.Uri,%20android.content.ContentValues[]))
* Automatic content change notifications via the ContentResolver as well as via [Intent.ACTION_PROVIDER_CHANGED](http://developer.android.com/reference/android/content/Intent.html#ACTION_PROVIDER_CHANGED) (security for ACTION_PROVIDER_CHANGED is also handled automatically)
* Support for LIMIT clauses via the query parameter [SearchManager.SUGGEST_PARAMETER_LIMIT](https://developer.android.com/reference/android/app/SearchManager.html#SUGGEST_PARAMETER_LIMIT)
* Support for [BaseColumns._COUNT](https://developer.android.com/reference/android/provider/BaseColumns.html#_COUNT) queries
* Enhanced security by ensuring _ID queries are not susceptible to SQL injection



## Usage
### Step by step
1. Define the contract classes for the ContentProvider.  (A contract usually class more or less represents a database table.)
1. Subclass `MementoContentProvider`, providing implementations for:
    1. `SqliteOpenHelper`: Opens and creates the database tables.  To simplify database table creation, consider using the helper classes `SqliteTableBuilder` and `SqliteColumnBuilder`.  Advanced users might also create indexes for improved performance via `SqliteIndexBuilder`.
    1. `SqliteUriMatcher`: Takes Uris and converts them into `SqliteUriMatch` objects that the ContentProvider uses whenever an operation (query, insert, update, delete, etc.) occurs.
1. Create an AndroidManifest entry for the ContentProvider.

### Example
An [example implementation](https://github.com/twofortyfouram/android-monorepo/tree/master/mementoImplLib/src/androidTest/java/com/twofortyfouram/memento/test) exists as part of the test suite.

### Further reading
The official Android [ContentProvider Developer Guide](https://developer.android.com/guide/topics/providers/content-providers.html).

<!--
## API Reference
JavaDocs for the library are published [here](http://twofortyfouram.github.io/android-memento).-->

## Compatibility
The library is compatible and optimized for Android API Level 19 and above.

<!--
## Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:
    
    dependencies {
        implementation group:'com.twofortyfouram', name:'android-memento-api', version:’[1.0.0,2.0[‘
        implementation group:'com.twofortyfouram', name:'android-memento-impl', version:’[1.0.0,2.0[‘
    }

## History
-->

# Plugin SDKs for Locale
## Overview
[Locale](https://play.google.com/store/apps/details?id=com.twofortyfouram.locale) allows developers to create plug-in conditions and settings through the [Locale Developer Platform](http://www.twofortyfouram.com/developer).

The plug-in architecture is implemented in three different layers (API, SDK, and Example), with each subsequent layer becoming more abstract and easier to work with.


## pluginHostSdkLib
### Creating a Host
The Locale host SDK offers a simplified way to implement an application that can host plug-ins.  The host has three primary responsibilities with regards to plug-ins: 1. discovering plug-ins, 2. editing plug-in instances, and 3. executing plug-in instances.

A plug-in app is represented by [Plugin](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/Plugin.html).  The data that the plug-in saved after editing is represented by [PluginInstanceData](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/PluginInstanceData.html).

### Discover
The [PluginRegistry](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/api/PluginRegistry.html) scans for installed plug-ins, which are represented by [Plugin](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/Plugin.html).  A host UI would most likely interact with the PluginRegistry through the [PluginRegistryLoader](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/ui/loader/PluginRegistryLoader.html) or [SupportPluginRegistryLoader](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/ui/loader/SupportPluginRegistryLoader.html).

### Edit
The host's UI displays to the user the Plugins that were discovered, using [Plugin.getActivityLabel()](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/Plugin.html#getActivityLabel-android.content.Context-) and [Plugin.getActivityIcon()](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/Plugin.html#getActivityIcon-android.content.Context-) to get human-readable information about the plug-in.

To edit a Plugin, the host instantiates a subclass of [AbstractPluginEditFragment](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/ui/fragment/AbstractPluginEditFragment.html) or [AbstractSupportPluginEditFragment](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/ui/fragment/AbstractSupportPluginEditFragment.html). AbstractPluginEditFragment takes care of launching the plug-in's "edit" Activity, processing the Activity result, serializing the plug-in's Bundle into a persistent form, and delivering a callback to subclasses via [handleSave(PluginInstanceData)](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/ui/fragment/IPluginEditFragment.html#handleSave-com.twofortyfouram.locale.sdk.host.model.Plugin-com.twofortyfouram.locale.sdk.host.model.PluginInstanceData-).

### Execute
The host fires PluginInstanceData to a plug-in, via one of the two controller objects [Condition](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/api/Condition.html) or [Setting](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/api/Setting.html).

PluginInstanceData has getters for [getType()](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/PluginType.html) and [getRegistryName()](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/model/PluginInstanceData.html#getRegistryName--).  Those two getters provide the keys necessary to look up the associated Plugin from the PluginRegistry, enabling the host to instantiate [Condition](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/api/Condition.html) or [Setting](https://twofortyfouram.github.io/android-plugin-host-sdk-for-locale/com/twofortyfouram/locale/sdk/host/api/Setting.html).

### Compatibility
The SDKs are compatible and optimized for Android API Level 19 and above.  Note, however, consuming the SDKs requires at least Android Gradle Plugin 1.3.0. (This is due to a manifest placeholder in the AAR's manifest.)


<!--
### Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:
    
    dependencies {
        compile group:'com.twofortyfouram', name:'android-plugin-host-sdk-for-locale', version:'[2.0.3,3.0['
    }


### History
* 1.0.0: Initial release
* 2.0.0: Update transitive dependency on [plug-in-client-sdk-for-locale](https://twofortyfouram.github.io/android-plugin-client-sdk-for-locale) to a major new version.  Although the host SDK APIs didn't change, the dependency change could break downstream clients hence a major new version.
* 2.0.1: Fix state management of AbstractPluginEditFragment and AbstractSupportPluginEditFragment.  Fix PluginPackageScanner bug detected by lint.
* 2.0.2: Fix detection of changing plug-ins after initial scan.
* 2.0.3: Fix compatibility with AAPT2 in Android Gradle Plugin 3.0.0-alpha2.
-->
## pluginClientSdkLib
This SDK is the middle layer of the Locale Developer Platform.

Although there are multiple ways to approach building a plug-in host or plug-in client, we do not recommend starting with this SDK layer.  Instead we strongly recommend starting with the main [Locale Developer Platform documentation](http://www.twofortyfouram.com/developer).

### Creating a Plug-in
#### Fundamentals
A plug-in implementation consists of two things:

1. Activity: for the [ACTION_EDIT_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_CONDITION) or [ACTION_EDIT_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_SETTING) Intent action.
1. BroadcastReceiver: for the [ACTION_QUERY_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_QUERY_CONDITION) or [ACTION_FIRE_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_FIRE_SETTING) Intent action.

At runtime the host launches the plug-in's Activity, the plug-in's Activity returns a Bundle to the host, and the host will send that Bundle back to the plug-in's BroadcastReceiver when it is time to query/fire the plug-in.  The host may also pass the Bundle back to the Activity in the future, if the user wishes to edit the plug-in's configuration again.


#### Step by Step
1. Add dependencies to build.gradle as described in the Usage section above.
1. Architect the contents of the plug-in's [EXTRA_BUNDLE](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#EXTRA_BUNDLE).  We recommend implementing a "BundleManager" object with static methods to verify the Bundle is correct, generate a new Bundle, and extract values from the Bundle.  
1. Implement the "Edit" Activity:
    1. Subclass [AbstractPluginActivity](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html) (or [AbstractFragmentPluginActivity](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractFragmentPluginActivity.html) for android-support-v4 compatibility) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#isBundleValid(android.os.Bundle)): Determines whether a Bundle is valid.
        1. [onPostCreateWithPreviousResult(android.os.Bundle previousBundle, java.lang.String previousBlurb)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#onPostCreateWithPreviousResult(android.os.Bundle,%20java.lang.String)): If the user is editing an old instance of the plug-in, this allows the Activity to restore state from that old plug-in configuration.
        1. [getResultBundle()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#getResultBundle()): When the Activity is finishing, this method will return the Bundle that represents the plug-in's state.  This Bundle will eventually be sent to the BroadcastReceiver when the plug-in is queried.
        1. [getResultBlurb(android.os.Bundle bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/ui/activity/AbstractPluginActivity.html#getResultBlurb(android.os.Bundle)): When the Activity is finishing, this method will return a concise, human-readable description of the plug-in's state that may be displayed in the host's UI.
    1. Add the AndroidManifest entry for the Activity.  *Note: It is very important that plug-in conditions and settings have a stable Activity class name.  The package and class names for the edit Activity are a plug-in's public API.  If they do not remain consistent, then saved instances of the plug-in created previously will be orphaned.  For more information, see Dianne Hackborn's blog post [Things That Cannot Change](http://android-developers.blogspot.com/2011/06/things-that-cannot-change.html).  To make maintaining a stable Activity class name easier, we recommend using an activity-alias for exposing the plug-in's edit Activity.  (It is permitted for the plug-in's BroadcastReceiver class name to change.)*
        1. Add an Intent filter for [ACTION_EDIT_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_CONDITION) or [ACTION_EDIT_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_EDIT_SETTING) Intent action.
        1. Add an Activity icon: This icon will be shown in the host's UI.  The ldpi version of the icon should be 27x27 pixels, the mdpi version should be 36x36 pixels, the hdpi version of the icon should be 48x48 pixels, the xhdpi version of the icon should be 72x72 pixels, and the xxhdpi version of the icon should be 108x108 pixels.  Note: THIS ICON IS SMALLER THAN THE LAUCHER ICON.  Providing a correctly scaled icon will improve performance when the host displays the plug-in's icon.
        1. Add an Activity label: The label is the name that will be displayed in the host's UI.

                <!-- This is the real Activity implementation but it is not exposed directly. -->
                <activity
                        android:name=".ui.activity.PluginActivityImpl"
                        android:exported="false"
                        android:label="@string/plugin_name"
                        android:uiOptions="splitActionBarWhenNarrow"
                        android:windowSoftInputMode="adjustResize">
                </activity>
                <!-- This is the activity-alias, which the host perceives as being the plug-in's Edit Activity.
                     This layer of indirection helps ensure the public API for the plug-in is stable.  -->
                <activity-alias
                        android:name=".ui.activity.PluginActivity"
                        android:exported="true"
                        android:icon="@drawable/ic_plugin"
                        android:label="@string/plugin_name"
                        android:targetActivity=".ui.activity.PluginActivityImpl">
                    <intent-filter>
                        <!-- For a plug-in setting, use EDIT_SETTING instead. -->
                        <action android:name="com.twofortyfouram.locale.intent.action.EDIT_CONDITION"/>
                    </intent-filter>
                </activity-alias>
1. Implement the BroadcastReceiver:
    * Condition: Subclass [AbstractPluginConditionReceiver](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#isBundleValid(android.os.Bundle)): 
        1. [isAsync()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#isAsync()): Determines whether the [getPluginConditionResult(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#getPluginConditionResult(android.content.Context,%20android.os.Bundle)) method should be executed in a background thread.
        1. [getPluginConditionResult(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginConditionReceiver.html#getPluginConditionResult(android.content.Context,%20android.os.Bundle)): Determines the state of the plug-in, which can be [RESULT_CONDITION_SATISFIED](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_SATISFIED), [RESULT_CONDITION_UNSATISFIED](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_UNSATISFIED), or [RESULT_CONDITION_UNKNOWN](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#RESULT_CONDITION_UNKNOWN).
    * Setting: Subclass [AbstractPluginSettingReceiver](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html) and provide implementations for:
        1. [isBundleValid(android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#isBundleValid(android.os.Bundle)): 
        1. [isAsync()](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#isAsync()): Determines whether the [firePluginSetting(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#firePluginSetting(android.content.Context,%20android.os.Bundle)) method should be executed in a background thread.
        1. [firePluginSetting(android.content.Context, android.os.Bundle)](http://twofortyfouram.github.io/android-plugin-client-sdk-for-locale/com/twofortyfouram/locale/sdk/client/receiver/AbstractPluginSettingReceiver.html#firePluginSetting(android.content.Context,%20android.os.Bundle)): Performs the plug-in setting's action.
1. Add the AndroidManifest entry for the BroadcastReceiver
    * Condition: Register the BroadcastReceiver with an Intent-filter for [ACTION_QUERY_CONDITION](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_QUERY_CONDITION):

                <receiver
                        android:name=".receiver.PluginConditionReceiverImpl"
                        android:exported="true">
                    <intent-filter>
                        <action android:name="com.twofortyfouram.locale.intent.action.QUERY_CONDITION"/>
                    </intent-filter>
                </receiver>
    * Setting: Register the BroadcastReceiver with an Intent-filter for [ACTION_FIRE_SETTING](http://twofortyfouram.github.io/android-plugin-api-for-locale/com/twofortyfouram/locale/api/Intent.html#ACTION_FIRE_SETTING):

                <receiver
                        android:name=".receiver.PluginSettingReceiverImpl"
                        android:exported="true">
                    <intent-filter>
                        <action android:name="com.twofortyfouram.locale.intent.action.FIRE_SETTING"/>
                    </intent-filter>
                </receiver>

### Compatibility
The library is compatible and optimized for Android API Level 19 and above.

<!-- 
### Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:

    dependencies {
        compile group:'com.twofortyfouram', name:'android-plugin-client-sdk-for-locale', version:'[4.0.3, 5.0['
    }

### History
* 1.0.0: Initial release
* 1.0.1: Fix diffing of plug-in edits.  Thanks @jkane001 for reporting this issue!
* 1.1.0: Support for Material Design and appcompat-v7
* 2.0.0: Update spackleLib dependency to 2.0.0
* 3.0.0
    * Remove AbstractLocalePluginActivity and AbstractLocaleFragmentPluginActivity.  These deprecated Activities implemented UI logic, while this SDK should only responsible for communicating with the host.
    * Rename AbstractPluginConditionReceiver.getPluginConditionState(Context, Bundle) to be more internally consistent.
* 4.0.0: Remove strings and resources that were previously used by AbstractLocalePluginActivity and AbstractLocaleFragmentPluginActivity.
* 4.0.1: Fix visibility of Activity onPostCreate().  Thanks @ddykhoff for reporting this issue!
* 4.0.2: Fix async plug-in settings.  Thanks @giech for reporting this issue!
* 4.0.3: Fix compatibility with AAPT2 in Android Gradle Plugin 3.0.0-alpha2.
-->

# Spackle
Spackle smooths things over and fills in the cracks.  This is a hodgepodge of utility classes that we reused several times and bundled them together.  We don't anticipate others to rely on this much directly, as our purpose in publishing it was more as an internal dependency for our other open source projects.  Put another way, we anticipate more churn in this library.

## Compatibility
The library is compatible and optimized for Android API Level 19 and above.

<!--
## Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:
    
    dependencies {
        compile group:'com.twofortyfouram', name:'android-spackle', version:'[3.0.0,4.0['
    }

## History
* 2.0.0: Initial release
* 2.0.1: PermissionCompat handles WRITE_SETTINGS and REQUEST_IGNORE_BATTERY_OPTIMIZATIONS on Android Marshmallow
* 2.0.2: ContextUtil avoids breaking out of test context
* 2.0.3: PermissionCompat implementation handles null arrays from PackageManager.  This is unlikely to impact usage, except during automated tests.
* 3.0.0: Added IClock interface, ProcessUtil, and SignatureUtil.  Deleted TraceCompat.
-->

# Test
Test implements a variety of classes to fill in gaps in the Android test framework.  This library is intended only to be used for test purposes, so should only be used in an `androidTestCompile` context.

<!--
# API Reference
JavaDocs for the library are published [here](http://twofortyfouram.github.io/android-test).
-->

# Compatibility
The library is compatible and optimized for Android API Level 19 and above.

<!--
# Download
## Gradle
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        jcenter()
    }

And the dependencies section would look something like this:

    dependencies {
        androidTestImplementation group:'com.twofortyfouram', name:'android-test', version:'[3.0.0,4.0['
        androidTestImplementation group:'com.twofortyfouram', name:'android-assertion', version:'[1.1.1,2.0['
    }

Note that the testLib has a `compileOnly` scope dependency on the assertion library, hence the compile dependency needed for any app using the library.

# History
* 1.0.0: Initial release
* 1.0.1: Disable running ProGuard, to fix RuntimeInvisibleParameterAnnotations error
* 1.0.2: Update Android Gradle plugin, which changed the generated BuildConfig
* 1.0.5: Reupload artifacts with source and JavaDoc for inclusion in jCenter
* 2.0.0: Changed interface of ActivityTestUtil for the new AndroidJUnitRunner
* 3.0.0: Changed interface of FeatureContextWrapper to support Android Marshmallow
-->