#!/bin/bash

# When the master branch build passes, attempt to upload artifacts.  Artifacts will usually be rejected because the server doesn't allow artifacts to be replaced.  Success occurs if the artifact version was incremented.  These rejections don't fail the build though.

BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "Branch is $BRANCH"
echo "Build status is $TDDIUM_BUILD_STATUS"

if [[ 'passed' == "$TDDIUM_BUILD_STATUS" && 'master' == "$BRANCH" ]]; then
    ./gradlew assembleRelease uploadArchives -PIS_MINIFY_ENABLED=false
    gradle_exit_code=$?
    echo "Gradle exit code $gradle_exit_code"
fi

exit 0
