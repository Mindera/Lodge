plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kmmbridge.github)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("lodgeBuild") {
            id = "com.mindera.lodge.build"
            implementationClass = "LodgeBuildPlugin"
        }
    }
}
