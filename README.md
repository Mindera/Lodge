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

## iOS XCFramework (test artifact)
---
> **For evaluation only.** The XCFramework exists so that any team curious about Kotlin Multiplatform can try a real KMP artifact in an iOS app. It isn't a supported product: its API can change between releases and it comes with no compatibility guarantees.

Add it with Swift Package Manager, either in Xcode with **File > Add Package Dependencies** or in your `Package.swift`:

```swift
dependencies: [
    .package(url: "https://github.com/Mindera/Lodge.git", from: "1.0.0")
]
```

No authentication is needed. It needs iOS 12.0 or later, and the framework is static.

Only release tags can be used, not `main`: [KMMBridge](https://github.com/touchlab/KMMBridge) generates `Package.swift` for each release and commits it to that tag only. The binary is attached to the GitHub release, so it never enters the repository.

Then use it from Swift:

```swift
import Lodge

LOG.shared.add(appender: OSAppender())
LOG.shared.i(tag: "MyTag", text: "Hello World")
LOG.shared.d(tag: "MyTag", message: { "Built only when an appender accepts it" })
```

Before you try it:

- **Logs are written asynchronously.** Anything still queued when the process exits is lost, which can affect unit tests and short-lived extensions.
- **It includes its own Kotlin runtime.** If your app already uses a KMP framework, add Lodge to that shared module rather than using this XCFramework.

## Publishing
---
1. Bump `LIBRARY_VERSION` in `gradle.properties`, because GitHub Packages rejects a version that already exists.
2. Run the [Publish workflow](.github/workflows/publish.yml) from the Actions tab on `main`.

The workflow publishes to GitHub Packages, creates the GitHub release, attaches the XCFramework, and tags a commit containing the generated `Package.swift`. Don't create the tag or the release by hand.

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
