# Plugin SDKs for Locale
## Overview
[Locale X](https://play.google.com/store/apps/details?id=com.twofortyfouram.locale.x) allows developers to create plug-in conditions and settings through the [Locale Developer Platform](http://www.twofortyfouram.com/developer).

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


### Download
The library is published as an artifact on GitHub Package Registry.  To use the library, the repository and the artifact need to be added to your build script.

The dependencies are currently published to GitHub Package Registry which does not allow for anonymous access.  Until we get these packages published to Maven Central use the following steps:

1. Generate a new [Personal Access Token](https://github.com/settings/tokens/new)
1. Put in a name for the token, such as `package registry`
1. Check the box for `read:packages` only
1. Click Generate Token
1. Save the token, which you'll need to use below

The build.gradle.kts repositories section would look something like the following:

    repositories {
        maven {
            // GitHub Package Registry does not allow anonymous access
            url = java.net.URI("https://maven.pkg.github.com/twofortyfouram/android-monorepo")
            credentials  {
                username = "YOUR_GITHUB_USERNAME"
                password = "YOUR_GITHUB_TOKEN"
            }
        }
    }

And the dependencies section would look something like this:
    
    dependencies {
        implementation("com.twofortyfouram:android-plugin-host-sdk-for-locale:9.0.0")
    }
<!--
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
