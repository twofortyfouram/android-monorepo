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

        lint {
            lintConfig = File("lint.xml")
        }

        val jacocoVersionString = run {
            val jacocoVersion: String by project
            jacocoVersion
        }
        testCoverage {
            jacocoVersion = jacocoVersionString
        }
    }
}

//    afterEvaluate {
//        pluginManager.withPlugin("com.android.application") {
//            project.the<com.android.build.gradle.AppExtension>().apply {
//                configureBaseExtension()
//            }
//        }
//    }


fun com.android.build.gradle.BaseExtension.configureBaseExtension() {
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
