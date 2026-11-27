@file:Suppress("UnstableApiUsage")

pluginManagement {
    // Standalone Lodge resolves `com.mindera.lodge.build` from its own default implementation.
    // When Lodge is embedded in another build, that build's pluginManagement supplies the plugin instead.
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Lodge"
