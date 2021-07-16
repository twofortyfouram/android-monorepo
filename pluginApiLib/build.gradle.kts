/*
 * android-plugin-api-for-locale
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

    implementation("net.jcip:jcip-annotations:${JCIP_ANNOTATION_VERSION_MATCHER}")
    implementation("androidx.annotation:annotation:${ANDROID_ANNOTATION_VERSION_MATCHER}")

    androidTestImplementation("androidx.test:core:${ANDROID_TEST_CORE_VERSION_MATCHER}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${ESPRESSO_VERSION_MATCHER}")
    androidTestImplementation("androidx.test.ext:junit:${ANDROID_TEST_JUNIT_VERSION_MATCHER}")

    if (isTestOrchestrator) {
        androidTestUtil("androidx.test:orchestrator:${ANDROID_TEST_ORCHESTRATOR_VERSION_MATCHER}") {
            artifact {
                classifier = "apk"
            }
        }
    }
}

android {
}

//android.libraryVariants.all { variant ->
//    task("${variant.name}Javadoc", type: Javadoc) {
//        description "Generates Javadoc for $variant.name."
//        def androidHome = System.getenv("ANDROID_HOME")
//        source = variant.javaCompile.source
//        classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
//        classpath += variant.javaCompile.classpath
//        classpath += variant.javaCompile.outputs.files
//        options.linksOffline("https://developer.android.com/reference/", "${androidHome}/docs/reference")
//        options.links("http://jcip.net.s3-website-us-east-1.amazonaws.com/annotations/doc/")
//        exclude "**/R.java"
//    }
//}
//
//// Generates the JavaDoc as a JAR for uploading an artifact
//android.libraryVariants.all { variant ->
//    task("${variant.name}JavadocJar", type: Jar, dependsOn: "${variant.name}Javadoc") {
//        classifier = "javadoc"
//        from tasks["${variant.name}Javadoc"].destinationDir
//    }
//
//    project.artifacts.add("archives", tasks["${variant.name}JavadocJar"]);
//}
//
//
//// Generates the source as a JAR for uploading an artifact
//android.libraryVariants.all { variant ->
//    task("${variant.name}SourceJar", type: Jar) {
//        classifier = "sources"
//        from variant.javaCompile.source
//    }
//
//    project.artifacts.add("archives", tasks["${variant.name}SourceJar"]);
//}

publishing {
    publications {
        publications.withType<MavenPublication>().all {
            artifactId = com.twofortyfouram.MavenName.remap(name, "android-plugin-api-for-locale")
        }
    }
}