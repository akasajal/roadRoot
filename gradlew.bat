@rem Gradle startup script for Windows
@rem Reference: https://docs.gradle.org/current/userguide/gradle_wrapper.html
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal
set DIRNAME=%~dp0
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%
@rem Execute Gradle
"%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
