import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * Default Lodge build convention, used when Lodge is built on its own.
 *
 * Deliberately pinned to Kotlin 1.9 and AGP 8 targeting Java 8 and minSdk 16, so Lodge stays usable from older projects.
 * Owns toolchains, platform targets and Android configuration. Dependencies, source sets and library identity stay in the module script.
 */
class LodgeBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.library")
        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(11)
            androidTarget {
                compilations.all { kotlinOptions.jvmTarget = "1.8" }
            }
            jvm {
                compilations.all { kotlinOptions.jvmTarget = "1.8" }
            }
            iosX64()
            iosArm64()
            iosSimulatorArm64()
            targets.withType(KotlinNativeTarget::class.java).configureEach {
                compilations.getByName("main").cinterops.create("oslog")
            }
        }
        extensions.configure<LibraryExtension> {
            namespace = "com.mindera.lodge"
            compileSdk = 33
            defaultConfig {
                minSdk = 16
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
            buildFeatures {
                buildConfig = false
            }
        }
    }
}
