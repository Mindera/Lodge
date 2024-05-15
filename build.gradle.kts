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
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val commonJvm by creating {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(commonJvm)
        }

        jvmMain {
            dependsOn(commonJvm)
        }

        create("appleMain") {
            dependsOn(commonMain.get())
            getByName("iosX64Main").dependsOn(this)
            getByName("iosArm64Main").dependsOn(this)
            getByName("iosSimulatorArm64Main").dependsOn(this)
        }

        commonTest {
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
