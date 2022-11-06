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
