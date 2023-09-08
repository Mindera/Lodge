@file:Suppress(
    "DSL_SCOPE_VIOLATION", // Remove when KTIJ-19369 is fixed. https://youtrack.jetbrains.com/issue/KTIJ-19369
)

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
}

group = "com.mindera.lodge"
version = "1.0.0"

kotlin {
    jvmToolchain(8)
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val androidMain by getting {
            dependsOn(getByName("jvmMain"))
        }

        val iosMain by creating {
            dependsOn(commonMain)
            getByName("iosX64Main").dependsOn(this)
            getByName("iosArm64Main").dependsOn(this)
            getByName("iosSimulatorArm64Main").dependsOn(this)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.mindera.lodge"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    buildFeatures {
        buildConfig = false
    }
}
