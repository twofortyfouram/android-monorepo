enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            val androidActivityVersion: String by settings
            val androidAnnotationVersion: String by settings
            val androidAppcompatVersion: String by settings
            val androidCoreVersion: String by settings
            val androidDatabaseVersion: String by settings
            val androidFragmentVersion: String by settings
            val androidLegacySupportVersion: String by settings
            val androidLifecycleVersion: String by settings
            val androidTestRulesVersion: String by settings
            val androidTestOrchestratorVersion: String by settings
            val androidTestJunitVersion: String by settings
            val espressoVersion: String by settings
            val jcipVersion: String by settings
            val kotlinVersion: String by settings

            alias("androidx-appcompat").to("androidx.appcompat:appcompat:${androidAppcompatVersion}")
            alias("androidx-supportv4").to("androidx.legacy:legacy-support-v4:${androidLegacySupportVersion}")

            alias("androidx-activity").to("androidx.activity:activity-ktx:${androidActivityVersion}")
            alias("androidx-core").to("androidx.core:core-ktx:${androidCoreVersion}")
            alias("androidx-annotation").to("androidx.annotation:annotation:${androidAnnotationVersion}")

            alias("androidx-fragment-base").to("androidx.fragment:fragment:${androidFragmentVersion}")
            alias("androidx-fragment-ktx").to("androidx.fragment:fragment-ktx:${androidFragmentVersion}")
            bundle("androidx-fragment", listOf("androidx-fragment-base", "androidx-fragment-ktx"))

            alias("androidx-livedata-ktx").to("androidx.lifecycle:lifecycle-livedata-ktx:${androidLifecycleVersion}")
            alias("androidx-livedata-core-ktx").to("androidx.lifecycle:lifecycle-livedata-core-ktx:${androidLifecycleVersion}")
            bundle("androidx-livedata", listOf("androidx-livedata-ktx", "androidx-livedata-core-ktx"))

            alias("androidx-viewmodel-ktx").to("androidx.lifecycle:lifecycle-viewmodel-ktx:${androidLifecycleVersion}")
            alias("androidx-viewmodel-savedstate").to("androidx.lifecycle:lifecycle-viewmodel-savedstate:${androidLifecycleVersion}")
            bundle("androidx-viewmodel", listOf("androidx-viewmodel-ktx", "androidx-viewmodel-savedstate"))

            alias("androidx-sqlite-api").to("androidx.sqlite:sqlite:${androidDatabaseVersion}")
            alias("androidx-sqlite-framework").to("androidx.sqlite:sqlite-framework:${androidDatabaseVersion}")

            alias("kotlin").to("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

            alias("jcip").to("net.jcip:jcip-annotations:${jcipVersion}")

            alias("androidx-espresso-core").to("androidx.test.espresso:espresso-core:${espressoVersion}")
            alias("androidx-espresso-intents").to("androidx.test.espresso:espresso-intents:${espressoVersion}")
            alias("androidx-espresso-contrib").to("androidx.test.espresso:espresso-contrib:${espressoVersion}")
            alias("androidx-espresso-remote").to("androidx.test.espresso:espresso-remote:${espressoVersion}")
            alias("androidx-junit").to("androidx.test.ext:junit:${androidTestJunitVersion}")
            alias("androidx-test-fragment").to("androidx.fragment:fragment-testing:${androidFragmentVersion}")
            alias("androidx-test-rules").to("androidx.test:rules:${androidTestRulesVersion}")
            alias("androidx-test-orchestrator").to("androidx.test:orchestrator:${androidTestOrchestratorVersion}")

            bundle("androidx-espresso", listOf("androidx-espresso-core", "androidx-espresso-intents", "androidx-espresso-contrib", "androidx-junit"))
        }
    }
}

includeBuild("build-conventions")

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
