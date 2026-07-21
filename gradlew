#!/bin/sh
##############################################################################
# Gradle wrapper script for Unix
##############################################################################
GRADLE_OPTS="${GRADLE_OPTS:-"-Xdock:name=Gradle" "-Xdock:icon=$APP_HOME/media/gradle.icns"}"
APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
exec "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
