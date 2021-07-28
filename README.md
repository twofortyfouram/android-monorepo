# Overview
This repository contains multiple open source libraries.  Because some of the libraries are interdependent, putting them in a single repository (monorepo) makes maintaining these libraries and releasing them easier.

Many of these libraries have their own documentation.  A high level of the libraries are:

 * Plugin SDKs for Locale — A combination of API specification, client SDK, and host SDK.  See [Plug-in API.md](docs/Plug-in%20API.md)
 * Memento — A SQLite-based Content Provider implementation, with both an API and implementation layer.  See [Memento.md](docs/Memento.md)
 * Spackle — A hodgepodge of utilities, mostly intended to be internal support the other modules.  See [Spackle.md](docs/Spackle.md)
 * Annotation — Simple annotations to better document code
 * Assertion — Runtime assertions
 * Test — Fills in some gaps in the Android test framework. See [Test.md](docs/Test.md).

# Versioning
Because of interdependencies, any modification to a single library causes all libraries to be republished in lockstep with a new version number.  The new version number is semantic, based on whether the change was a bug fix, feature, or incompatible change.

Consider an example: memento depends on spackle.  Both are at version 1.0.0.  If a minor bug fix is made to spackle, both libraries will be released with version 1.0.1.  In other words, memento will be re-released although it didn't change (only its transitive dependency changed).

Consider another example: memento depends on spackle.  Both are at version 1.0.0.  If a minor bug fix is made to memento, both libraries will be released with version 1.0.1.  In other words, spackle will be re-released although it didn't change, and none of its dependencies changed.

Although this pattern may change in the future, this seems to be a reasonable balance given the need to continuosly improve these libraries.

# Building
The entire repo can be easily checked out and built locally by running `./gradlew assemble`.

# Contributing
Contributions are welcome, although contributors will need to sign a Contributor License Agreement (CLA).  Please contact up@244.am.
