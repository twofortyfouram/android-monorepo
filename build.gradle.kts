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

//allprojects {
//    gradle.projectsEvaluated {
//        tasks.withType(JavaCompile) {
//            options.compilerArgs << "-Xlint:all"
//        }
//    }
//}
