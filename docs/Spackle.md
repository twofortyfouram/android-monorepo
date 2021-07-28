# Spackle
Spackle smooths things over and fills in the cracks.  This is a hodgepodge of utility classes that we reused several times and bundled them together.  We don't anticipate others to rely on this much directly, as our purpose in publishing it was more as an internal dependency for our other open source projects.  Put another way, we anticipate more churn in this library.

## Compatibility
The library is compatible and optimized for Android API Level 19 and above.


## Download
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