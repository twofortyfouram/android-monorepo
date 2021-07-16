# Publishing
The repository is set up to publish artifacts to a Maven repository, with three parts:

1. `gradle.properties` defines the repo URL and authentication
2. `twofortyfouram.maven-conventions.gradle.kts` Configures the 
3. Each build script defines the artifact package, id, and version.

To publish artifacts to a remote maven repository:
1. Configure gradle properties for `twofortyfouramMonorepoMavenUrl`, `twofortyfouramMonorepoMavenUser`, and `twofortyfouramMonorepoMavenToken`
2. Run `./gradlew publish`

A manual GitHub build action is also configured within the repository, which eliminates the need to configure tokens.  Before running this action, be sure to bump the version number in `gradle.properties`.

For development, artifacts can also be published to a local repository:
1. Run `./gradlew publishToMavenLocal`
1. On macOS and Linux, packages will be published under `~/.m2`