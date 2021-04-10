pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val gradleVersionsPluginVersion: String by settings
        val fulladleVersion: String by settings

        kotlin("jvm") version(kotlinVersion)
        id("com.github.ben-manes.versions") version(gradleVersionsPluginVersion)
        id("com.osacky.fulladle") version(fulladleVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(":emptyApp")

include(":annotationLib")
include(":assertionLib")
include(":mementoApiLib")
include(":mementoImplLib")
include(":pluginApiLib")
include(":pluginClientSdkLib")
include(":pluginHostSdkLib")
include(":spackleLib")
include(":testLib")
