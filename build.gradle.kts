plugins {
    // Resolved by the owning build: build-logic when standalone, the host build's pluginManagement when embedded.
    alias(libs.plugins.lodge.build)
}

group = "com.mindera.lodge"
version = providers.gradleProperty("LIBRARY_VERSION").get()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val commonJvm by creating {
            dependsOn(commonMain.get())
        }

        // Targets are chosen by the build convention, so only wire up the source sets that exist.
        matching { it.name == "androidMain" || it.name == "jvmMain" }.configureEach {
            dependsOn(commonJvm)
        }

        val appleMain = create("appleMain") {
            dependsOn(commonMain.get())
        }
        matching { it.name in setOf("iosX64Main", "iosArm64Main", "iosSimulatorArm64Main") }.configureEach {
            dependsOn(appleMain)
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
