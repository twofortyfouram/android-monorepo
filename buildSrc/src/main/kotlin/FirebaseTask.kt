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

import com.android.builder.model.Version
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

open class FirebaseTask : DefaultTask() {

    /**
     * If a library project then it doesn't exist.
     */
    @Optional
    @get:InputFile
    var apkFile: File? = null

    @get:InputFile
    lateinit var testApkFile: File

    @get:Input
    lateinit var testVariantName: String

    @get:Input
    var isCoverageEnabled: Boolean = true

    @get:Input
    var isOrchestratorEnabled: Boolean = false

    @get:Input
    lateinit var bucket: String

    @get:Input
    lateinit var args: String

    @get:Input
    lateinit var module: String

    @get:Input
    var retries: Int = 1

    @TaskAction
    fun doIt() {
        val uuid = UUID.randomUUID()
        println("About to run Firebase Test Lab apkFile=$apkFile testApkFile=$testApkFile module=$module isCoverageEnabled=$isCoverageEnabled isOrchestratorEnabled=$isOrchestratorEnabled bucket=$bucket args=$args uuid=$uuid")

        if (bucket.isEmpty()) {
            throw AssertionError("Bucket is empty")
        }

        runFirebaseTestLab(project.logger, temporaryDir, retries, apkFile, testApkFile, testVariantName, isCoverageEnabled, isOrchestratorEnabled, bucket, uuid, module, args)
    }

    companion object {
        private const val GCLOUD_YML_FILE_NAME = "gcloud.yml"
        private const val TEST_BUTLER_FILE_NAME = "test-butler-app-2.0.0.apk"
        private const val EMPTY_APK_FILE_NAME = "empty.apk"

        private const val GCLOUD_FILE_COPY_SLEEP_DELAY_MILLISECONDS = (5 * 1000).toLong()
        private const val RESULT_CODE_TEST_PASSED = 0
        private const val RESULT_CODE_TEST_FAILED = 10

        private const val SLEEP_DELAY_SECONDS = 3L

        private var LAST_GCLOUD_TIME: Long = 0

        /**
         * @param apkFile Optional path to the APK file.
         */
        fun runFirebaseTestLab(logger: org.gradle.api.logging.Logger, tempDir: File, retries: Int, apkFile: File?, testApkFile: File,
                               testVariant: String, isCoverageEnabled: Boolean, isOrchestratorEnabled: Boolean, bucket: String, uuid: UUID, module: String, device: String) {

            delay()

            var args = LinkedList<String>().apply {
                add("gcloud")
                add("firebase")
                add("test")
                add("android")
                add("run")
                add("--type=instrumentation") // TODO Support robo tests
                add("--results-dir=$uuid")
                add("--results-history-name=$module")
                add("--device=$device")
                add("--test=${testApkFile.path}")

                if (null == apkFile) {
                    // Library project, need to provide fake APK
                    // TODO don't use TestButler always, because it will fail for non-emulator devices
                    // TODO: For TestButler, try to download the APK instead

                    val fakeApkFile = extractResourceFile(tempDir, TEST_BUTLER_FILE_NAME)
                    add("--app=${fakeApkFile.path}")
                } else {
                    add("--app=$apkFile")
                }

                if (isOrchestratorEnabled) {
                    add("--use-orchestrator")
                }

                if (isCoverageEnabled) {
                    // gcloud environment variables are a hot mess.  No combination I've tried passed
                    // by command  line works.  The yml is reliable though.
                    // TODO: if possible convert to command line args

                    val gcloudYmlFile = extractResourceFile(tempDir, GCLOUD_YML_FILE_NAME)
                    add("${gcloudYmlFile.path}:coverage-environment-variables")
                }
            }

            var pb = ProcessBuilder(args)

            val process = pb.start().apply {
                errorStream.bufferedReader().forEachLine {
                    logger.log(LogLevel.LIFECYCLE, it)
                }
                inputStream.bufferedReader().forEachLine {
                    logger.log(LogLevel.LIFECYCLE, it)
                }
            }

            val resultCode = process.waitFor()
            if (RESULT_CODE_TEST_FAILED == resultCode && retries > 1) {
                // This is a hack for a flaky test that would be difficult to work around
                runFirebaseTestLab(logger, tempDir, retries - 1, apkFile, testApkFile, testVariant, isCoverageEnabled, isOrchestratorEnabled, bucket, uuid, module, device)
            } else if (RESULT_CODE_TEST_PASSED == resultCode || RESULT_CODE_TEST_FAILED == resultCode) {
                // There can be some propagation delays before result files become visible.
                // After discussion on the Firebase Test Lab Slack this seems to be a reasonable option.
                Thread.sleep((GCLOUD_FILE_COPY_SLEEP_DELAY_MILLISECONDS))

                copyTestResults(logger, bucket, uuid, module)
                if (isCoverageEnabled) {
                    copyCoverage(logger, bucket, uuid, module, testVariant)
                }

                if (FirebaseTestLabPlugin.FAIL_ON_ERROR && RESULT_CODE_TEST_FAILED == resultCode) {
                    throw RuntimeException("Test case failed")
                }
            } else {
                throw RuntimeException("Firebase Test Lab failed with $resultCode")
            }
        }

        private fun extractResourceFile(tempDir: File, fileName: String): File {
            val outputFile = File(tempDir, fileName)
            FirebaseTask::class.java.classLoader.getResourceAsStream(fileName).use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return outputFile
        }

        /**
         * Introduces a delay before next invocation of gcloud, because each gcloud invocation uses
         * a shared resource that will fail with too much concurrency.
         */
        @Synchronized
        private fun delay() {
            val currentTimeMillis = System.currentTimeMillis()
            val lastRunMillis = LAST_GCLOUD_TIME
            LAST_GCLOUD_TIME = currentTimeMillis
            val delta = currentTimeMillis - lastRunMillis

            if (delta < TimeUnit.SECONDS.toMillis(SLEEP_DELAY_SECONDS)) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(SLEEP_DELAY_SECONDS) - delta)
            }
        }

        private fun copyTestResults(logger: org.gradle.api.logging.Logger, bucket: String, uuid:
        UUID, module: String) {
            // TODO: [Case 17943] leverage gradle to determine the correct directory name
            val localDirPath = "$module/build/outputs/androidTest-results/connected/"
            val localDirFile = File(localDirPath)
            localDirFile.mkdirs()

            val pb = ProcessBuilder("gsutil", "-m", "cp", "gs://$bucket/$uuid/**test_result_*.xml",
                    "$module/build/outputs/androidTest-results/connected/$uuid-test-result.xml")
            println(pb.command())
            val process = pb.start()
            process.errorStream.bufferedReader().forEachLine {
                logger.log(LogLevel.LIFECYCLE, it)
            }
            process.inputStream.bufferedReader().forEachLine {
                logger.log(LogLevel.LIFECYCLE, it)
            }

            val resultCode = process.waitFor()

            println("Copy test results resultCode = $resultCode")
            if (0 != resultCode) {
                throw RuntimeException("Copying test results failed with $resultCode")
            }
        }

        private fun copyCoverage(logger: org.gradle.api.logging.Logger, bucket: String, uuid:
        UUID, module: String, testVariant: String) {
            // TODO: [Case 17943] leverage gradle to determine the correct directory name
            val coveragePath = coveragePath(module, testVariant)
            val localDirFile = File(coveragePath)
            localDirFile.mkdirs()

            val pb = ProcessBuilder("gsutil", "-m", "cp",
                    "gs://$bucket/$uuid/**/artifacts/coverage.ec",
                    "$coveragePath$uuid-coverage.ec")
            val process = pb.start()
            process.errorStream.bufferedReader().forEachLine {
                logger.log(LogLevel.LIFECYCLE, it)
            }
            process.inputStream.bufferedReader().forEachLine {
                logger.log(LogLevel.LIFECYCLE, it)
            }

            val resultCode = process.waitFor()
            println("Copy coverage resultCode = $resultCode")
            if (0 != resultCode) {
                throw RuntimeException("Copying test results failed with $resultCode")
            }
        }

        private fun coveragePath(module: String, testVariant: String): String {
            if (Version.ANDROID_GRADLE_PLUGIN_VERSION > "3.2.100") {
                return "$module/build/outputs/code_coverage/${testVariant}AndroidTest/connected/"
            }

            return "$module/build/outputs/code-coverage/connected/"
        }
    }

}