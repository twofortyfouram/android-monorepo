# Test
Test implements a variety of classes to fill in gaps in the Android test framework.  This library is intended only to be used for test purposes, so should only be used in an `androidTestCompile` context.

<!--
# API Reference
JavaDocs for the library are published [here](http://twofortyfouram.github.io/android-test).
-->

# Compatibility
The library is compatible and optimized for Android API Level 19 and above.


# Download
## Gradle
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
        androidTestImplementation("com.twofortyfouram:android-test:9.0.0")
        androidTestImplementation("com.twofortyfouram:android-assertion:9.0.0")
    }


Note that the testLib has a `compileOnly` scope dependency on the assertion library, hence the implementation dependency needed for any user of the library.
<!--
# History
* 1.0.0: Initial release
* 1.0.1: Disable running ProGuard, to fix RuntimeInvisibleParameterAnnotations error
* 1.0.2: Update Android Gradle plugin, which changed the generated BuildConfig
* 1.0.5: Reupload artifacts with source and JavaDoc for inclusion in jCenter
* 2.0.0: Changed interface of ActivityTestUtil for the new AndroidJUnitRunner
* 3.0.0: Changed interface of FeatureContextWrapper to support Android Marshmallow
-->