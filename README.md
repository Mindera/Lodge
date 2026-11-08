# Lodge
A simple, lightweight, strait to the point logging library for Kotlin Multiplatform Mobile.

[![Pure Kotlin](https://img.shields.io/badge/100%25-kotlin-blue.svg)](https://kotlinlang.org/)

Out of the box, the library provides platform-specific loggers for Logcat, NSLog and OSLog, but its easy to extend and configure more.

## Usage
---

Two easy steps:

 1. Install any `Appender` you want on your application.
 2. Call `LOG`'s methods everywhere throughout your app.

 ### Example

```kotlin
LOG.i("MyTag", "Hello World")
```

### Appender
---
You can control what will be logged based on severity.
For example, if you only want an `Appender` to receive logs `WARN` and up, you can configure it by setting the `minLogLevel` of the `Appender`. 

## Installation
---
Lodge is published to [GitHub Packages](https://github.com/Mindera/Lodge/packages), which requires authentication even for public packages. Use a personal access token with the `read:packages` scope.

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Mindera/Lodge")
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

```kotlin
commonMain {
    dependencies {
        implementation("com.mindera.lodge:Lodge:1.0.0")
    }
}
```

## Publishing
---
Publishing a GitHub release runs the [Publish workflow](.github/workflows/publish.yml). Bump `version` in `build.gradle.kts` first, because GitHub Packages rejects a version that already exists.

# License


    Copyright (c) 2022 Mindera - Software Craft

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
