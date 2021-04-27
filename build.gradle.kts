buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    dependencies {
        val androidGradlePluginVersion: String by project
    
        classpath("com.android.tools.build:gradle:${androidGradlePluginVersion}")
    }
}

plugins {
    kotlin("jvm")
    id("com.github.ben-manes.versions")
    id("com.osacky.fulladle")
}

fladle {
    val twofortyfouramFirebaseTestLabServiceAccountKeyPath: String by project

    serviceAccountCredentials.set(File(twofortyfouramFirebaseTestLabServiceAccountKeyPath))
    devices.addAll(mapOf("model" to "NexusLowRes", "version" to "29"))
}

// This should be refactored to a convention plugin under build-conventions
subprojects {
    pluginManager.withPlugin("com.android.library") {
        project.the<com.android.build.gradle.LibraryExtension>().apply {
            configureBaseExtension()

            compileSdk = run {
                val androidCompileSdkVersion: String by project
                androidCompileSdkVersion.toInt()
            }

            defaultConfig {
                minSdk = run {
                    val androidMinSdkVersion: String by project
                    androidMinSdkVersion.toInt()
                }
                targetSdk = run {
                    val androidTargetSdkVersion: String by project
                    androidTargetSdkVersion.toInt()
                }

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("proguard-consumer.txt")

                val isTestOrchestrator = run {
                    val isUseTestOrchestrator: String by project
                    isUseTestOrchestrator.toBoolean()
                }
                if (isTestOrchestrator) {
                    testInstrumentationRunnerArguments.put("clearPackageData", "true")
                }
            }

//            lint {
//                lintConfig = File("lint.xml")
//            }
//
//            testCoverage {
//                val jacocoVersion: String by project
//
//                version = jacocoVersion
//            }
        }
    }
}

fun com.android.build.gradle.BaseExtension.configureBaseExtension() {
    val androidCompileSdkVersion: String by project
    compileSdkVersion(androidCompileSdkVersion)

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    buildTypes {
        getByName("debug").apply {
            isTestCoverageEnabled = run {
                val isCoverageEnabled: String by project
                isCoverageEnabled.toBoolean()
            }
        }
    }

    signingConfigs {
        getByName("debug").apply {
            storeFile = File("${rootProject.projectDir}/tools/debug.keystore")
        }
    }

    testOptions {
        val isTestOrchestrator = run {
            val isUseTestOrchestrator: String by project
            isUseTestOrchestrator.toBoolean()
        }

        animationsDisabled = true
        if (isTestOrchestrator) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }
}