
/*
 * https://github.com/twofortyfouram/android-monorepo
 * Copyright (C) 2008–2019 two forty four a.m. LLC
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

package com.twofortyfouram.warp

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.task
import java.io.File

/*
 * As a note: the implementation is not DRY because of repetitive logic between library
 * and app projects.  Not going to try for code re-use for now because this will likely evolve and
 * too much re-use now would probably be a mistake in terms of being able to rapidly make changes.
 */
open class FirebaseTestLabPlugin : Plugin<Project> {


    companion object {
        /**
         * Until a good option is developed to extract failures from the XML results, this is the easiest
         * way to fail the build on CI when a test doesn't pass.
         */
        const val FAIL_ON_ERROR = true

        private const val EXTENSION_NAME = "ftlOptions"
        private const val TASK_NAME = "ftlCheck"

        private const val ASSEMBLE_ANDROID_TEST_TASK = "assembleAndroidTest"

        private const val EXECUTION_ORCHESTRATOR_X = "androidx_test_orchestrator"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create<FirebaseTestLabExtension>(EXTENSION_NAME)

        project.afterEvaluate {
            val androidExtension = project.extensions.findByName("android")

            if (androidExtension is LibraryExtension) {
                configureLibrary(project, androidExtension, extension)
            } else if (androidExtension is AppExtension) {
                configureApplication(project, androidExtension, extension)
            }
        }
    }

    private fun configureLibrary(project: Project, androidExtension: LibraryExtension, pluginExtension: FirebaseTestLabExtension) {
        // TODO: Inject a fake empty APK file or TestButler APK

        var apkFile: File? = null
        var testApkFile: File? = null
        var testVariant: String? = null

        androidExtension.testVariants.forEach {
            testApkFile = it.outputs.first().outputFile
            testVariant = it.testedVariant.name
        }

        var testCoverageFlag = androidExtension.buildTypes.getByName(testVariant!!).isTestCoverageEnabled
        var orchestratorFlag = androidExtension.testOptions.execution == EXECUTION_ORCHESTRATOR_X

        val firebaseTestLabTask = project.task<FirebaseTask>(TASK_NAME) {
            this.testApkFile = testApkFile!!
            testVariantName = testVariant!!
            bucket = pluginExtension.bucket!!
            args = pluginExtension.args!!
            isCoverageEnabled = testCoverageFlag
            isOrchestratorEnabled = orchestratorFlag
            apkFile?.let {
                this.apkFile = it
            }
            module = project.name
            retries = pluginExtension.retries
        }
        firebaseTestLabTask.dependsOn(ASSEMBLE_ANDROID_TEST_TASK)
    }

    private fun configureApplication(project: Project, androidExtension: AppExtension, pluginExtension: FirebaseTestLabExtension) {
        var apkFile: File? = null
        var testApkFile: File? = null
        var testVariant: String? = null

        androidExtension.applicationVariants.forEach {
            if (androidExtension.testBuildType == it.name) {
                apkFile = it.outputs.first().outputFile
            }
        }
        androidExtension.testVariants.forEach {
            testApkFile = it.outputs.first().outputFile
            testVariant = it.testedVariant.name
        }

        var testCoverageFlag = androidExtension.buildTypes.getByName(testVariant!!).isTestCoverageEnabled

        var orchestratorFlag = androidExtension.testOptions.execution == EXECUTION_ORCHESTRATOR_X

        val firebaseTestLabTask = project.task<FirebaseTask>(TASK_NAME) {
            this.testApkFile = testApkFile!!
            testVariantName = testVariant!!
            bucket = pluginExtension.bucket!!
            args = pluginExtension.args!!
            isCoverageEnabled = testCoverageFlag
            isOrchestratorEnabled = orchestratorFlag
            apkFile?.let {
                this.apkFile = it
            }
            module = project.name
            retries = pluginExtension.retries
        }

        // Applications have two tasks: one for building the app APK and the other for the test APK
        val dependsOnApk = "assemble${androidExtension.testBuildType.capitalize()}"

        firebaseTestLabTask.dependsOn(dependsOnApk, ASSEMBLE_ANDROID_TEST_TASK)
    }
}