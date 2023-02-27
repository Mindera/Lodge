plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

group = "com.mindera.lodge"
version = "1.0.0"

kotlin {
    android()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependsOn(getByName("jvmMain"))
        }
        // Remove log pollution until Android support in KMP improves.
        // https://issuetracker.google.com/issues/152187160
        // https://youtrack.jetbrains.com/issue/KT-48436
        removeDomain("androidAndroidTestRelease")
        removeDomain("androidTestFixtures")
        removeDomain("androidTestFixturesDebug")
        removeDomain("androidTestFixturesRelease")

        val iosMain by creating {
            dependsOn(commonMain)
            getByName("iosX64Main").dependsOn(this)
            getByName("iosArm64Main").dependsOn(this)
            getByName("iosSimulatorArm64Main").dependsOn(this)
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
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.removeDomain(name: String) {
    try {
        remove(getByName(name))
    } catch (e: UnknownDomainObjectException) {
        //
    }
}
