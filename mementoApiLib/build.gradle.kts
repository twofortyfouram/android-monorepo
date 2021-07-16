/*
 * android-memento
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
    val LIBRARY_VERSION_NAME: String by project
    LIBRARY_VERSION_NAME
}

dependencies {
    val JCIP_ANNOTATION_VERSION_MATCHER: String by project
    val ANDROID_ANNOTATION_VERSION_MATCHER: String by project
    val ANDROID_DATABASE_VERSION_MATCHER: String by project
    val ANDROID_LIFECYCLE_VERSION_MATCHER: String by project
    val ANDROID_TEST_CORE_VERSION_MATCHER: String by project
    val ESPRESSO_VERSION_MATCHER: String by project
    val ANDROID_TEST_JUNIT_VERSION_MATCHER: String by project
    val ANDROID_TEST_ORCHESTRATOR_VERSION_MATCHER: String by project

    compileOnly("androidx.lifecycle:lifecycle-livedata-core:${ANDROID_LIFECYCLE_VERSION_MATCHER}")

    api("androidx.sqlite:sqlite:${ANDROID_DATABASE_VERSION_MATCHER}")
    implementation("androidx.annotation:annotation:${ANDROID_ANNOTATION_VERSION_MATCHER}")
    implementation("net.jcip:jcip-annotations:${JCIP_ANNOTATION_VERSION_MATCHER}")
    implementation(project(":annotationLib"))
    implementation(project(":assertionLib"))
    implementation(project(":spackleLib"))

    androidTestImplementation("androidx.sqlite:sqlite-framework:${ANDROID_DATABASE_VERSION_MATCHER}")
    androidTestImplementation("androidx.lifecycle:lifecycle-livedata-core:${ANDROID_LIFECYCLE_VERSION_MATCHER}")

    androidTestImplementation("androidx.test:core:${ANDROID_TEST_CORE_VERSION_MATCHER}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${ESPRESSO_VERSION_MATCHER}")
    androidTestImplementation("androidx.test.ext:junit:${ANDROID_TEST_JUNIT_VERSION_MATCHER}")
    androidTestImplementation(project(":testLib"))

    if (isTestOrchestrator) {
        androidTestUtil("androidx.test:orchestrator:${ANDROID_TEST_ORCHESTRATOR_VERSION_MATCHER}") {
            artifact {
                classifier = "apk"
            }
        }
    }
}

android {
    resourcePrefix = "com_twofortyfouram_memento_api_"
}

// android.libraryVariants.all { variant ->
//     createJavaDocTaskForVariant(variant, "com/twofortyfouram/memento", "com_twofortyfouram_memento")
// }

publishing {
    publications {
        publications.withType<MavenPublication>().all {
            artifactId = com.twofortyfouram.MavenName.remap(name, "android-memento-api")
        }
    }
}