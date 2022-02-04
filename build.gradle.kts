plugins {
    id("com.android.application") apply (false)
    id("com.android.library") apply (false)
    id("com.github.ben-manes.versions")
    id("com.osacky.fulladle")
    id("org.jetbrains.kotlin.android") apply (false)
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Unstable")
                }
            }
        }
    }
}

val UNSTABLE_KEYWORDS = listOf("alpha", "beta", "rc", "m", "ea", "build")

fun isNonStable(version: String): Boolean {
    val versionLowerCase = version.toLowerCase()

    return UNSTABLE_KEYWORDS.any { versionLowerCase.contains(it) }
}

// Firebase Test Lab has min and max values that might differ from our project's
// These are determined by `gcloud firebase test android models list`
@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val FIREBASE_TEST_LAB_MIN_API = 23
@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val FIREBASE_TEST_LAB_MAX_API = 30

fladle {
    val twofortyfouramFirebaseTestLabServiceAccountKeyPath: String by project

    val minSdkVersion = run {
        val androidMinSdkVersion: String by project
        androidMinSdkVersion.toInt().coerceAtLeast(FIREBASE_TEST_LAB_MIN_API).toString()
    }
    val targetSdkVersion = run {
        val androidTargetSdkVersion: String by project
        androidTargetSdkVersion.toInt().coerceAtMost(FIREBASE_TEST_LAB_MAX_API).toString()
    }

    serviceAccountCredentials.set(File(twofortyfouramFirebaseTestLabServiceAccountKeyPath))
    devices.addAll(
        mapOf("model" to "NexusLowRes", "version" to minSdkVersion),
        mapOf("model" to "NexusLowRes", "version" to targetSdkVersion)
    )
    debugApk.set(project.provider { "${projectDir}/emptyApp/build/outputs/apk/debug/*.apk" })
}
