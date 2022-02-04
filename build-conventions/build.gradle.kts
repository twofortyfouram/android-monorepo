plugins {
    `kotlin-dsl`
}

// Per conversation in the KotlinLang Slack, Gradle uses Java 8 compatibility internally
// for all build scripts.
// https://kotlinlang.slack.com/archives/C19FD9681/p1636632870122900?thread_ts=1636572288.117000&cid=C19FD9681
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    val rootProperties = getRootProperties()

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProperties.getProperty("kotlinVersion")}")
    implementation("com.android.tools.build:gradle:${rootProperties.getProperty("androidGradlePluginVersion")}")
}

// A slightly gross way to use the root gradle.properties as the single source of truth for version numbers
fun getRootProperties() = org.jetbrains.kotlin.konan.properties.loadProperties(File(project.projectDir.parentFile, "gradle.properties").path)