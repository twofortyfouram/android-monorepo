/*
 * android-annotation
 * https://github.com/twofortyfouram/android-monorepo
 * Copyright (C) 2008–2018 two forty four a.m. LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    id("twofortyfouram.maven-conventions")
}
apply(from = "../scripts.gradle")

val isTestOrchestrator = run {
    val isUseTestOrchestrator: String by project
    isUseTestOrchestrator.toBoolean()
}

group = "com.twofortyfouram"
version = run {
    val libraryVersionName: String by project
    libraryVersionName
}

dependencies {
    implementation(libs.jcip)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    if (isTestOrchestrator) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                classifier = "apk"
            }
        }
    }
}

android {
    resourcePrefix = "com_twofortyfouram_annotation_"
}

//android.libraryVariants.all { variant ->
//    createJavaDocTaskForVariant(variant, "com/twofortyfouram/annotation", "com_twofortyfouram_annotation")
//}

// There's no code to test with just annotations
fulladleModuleConfig {
    enabled.set(false)
}

publishing {
    publications {
        publications.withType<MavenPublication>().all {
            artifactId = com.twofortyfouram.MavenName.remap(name, "android-annotation")
        }
    }
}