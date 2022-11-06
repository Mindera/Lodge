@file:Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("com.android.library") version extra["google.versions.gradle"] as String
        kotlin("multiplatform") version extra["jetbrains.versions.kotlin"] as String
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("kotlinx") {
            from(files("toml/kotlinx.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Lodge"
