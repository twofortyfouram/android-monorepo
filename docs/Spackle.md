# Spackle
Spackle smooths things over and fills in the cracks.  This is a hodgepodge of utility classes that we reused several times and bundled them together.  We don't anticipate others to rely on this much directly, as our purpose in publishing it was more as an internal dependency for our other open source projects.  Put another way, we anticipate more churn in this library.

## Compatibility
The library is compatible and optimized for Android API Level 19 and above.


## Download
The library is published as an artifact to jCenter.  To use the library, the jCenter repository and the artifact need to be added to your build script.

The build.gradle repositories section would look something like the following:

    repositories {
        maven("https://maven.pkg.github.com/twofortyfouram/android-monorepo")
    }

And the dependencies section would look something like this:
    
    dependencies {
        implementation("com.twofortyfouram:android-spackle:9.0.0")
    }
<!--
## History
* 2.0.0: Initial release
* 2.0.1: PermissionCompat handles WRITE_SETTINGS and REQUEST_IGNORE_BATTERY_OPTIMIZATIONS on Android Marshmallow
* 2.0.2: ContextUtil avoids breaking out of test context
* 2.0.3: PermissionCompat implementation handles null arrays from PackageManager.  This is unlikely to impact usage, except during automated tests.
* 3.0.0: Added IClock interface, ProcessUtil, and SignatureUtil.  Deleted TraceCompat.
-->