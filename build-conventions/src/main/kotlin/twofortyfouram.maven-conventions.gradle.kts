import java.net.URI


plugins.withId("org.gradle.maven-publish") {
    extensions.findByType<PublishingExtension>()?.apply {
        afterEvaluate {
            publications {
                create("release", MavenPublication::class) {
                    from(components["release"])
                }
            }
        }

        val twofortyfouramMonorepoMavenUrl: String by project
        val twofortyfouramMonorepoMavenUser: String by project
        val twofortyfouramMonorepoMavenToken: String by project

        if (twofortyfouramMonorepoMavenUser.isNotEmpty() && twofortyfouramMonorepoMavenToken.isNotEmpty()) {
            repositories {
                maven {
                    url = URI(twofortyfouramMonorepoMavenUrl)
                    credentials {
                        username = twofortyfouramMonorepoMavenUser
                        password = twofortyfouramMonorepoMavenToken
                    }
                }
            }
        }
    }
}
