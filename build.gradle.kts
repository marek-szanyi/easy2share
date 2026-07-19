// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

    val kotlinVersion = libs.versions.kotlin.get()

    subprojects {
        configurations.all {
            resolutionStrategy {
                force("org.jetbrains.kotlin:kotlin-metadata-jvm:$kotlinVersion")
            }
        }

        apply(plugin = "com.diffplug.spotless")
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                target("**/*.kt")
                targetExclude("${layout.buildDirectory}/**/*.kt")
                ktlint()
                licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
            }
            kotlinGradle {
                target("*.gradle.kts")
                targetExclude("${layout.buildDirectory}/**/*.kt")
                ktlint()
                // Look for the first line that doesn't have a block comment (assumed to be the license)
                licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
            }
        }
    }