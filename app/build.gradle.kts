/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val releaseKeystore = System.getenv("ANDROID_KEYSTORE_FILE")?.let(::file) ?: rootProject.file("keystore.jks")
val releaseStorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD").orEmpty()
val releaseKeyAlias = System.getenv("ANDROID_KEY_ALIAS").orEmpty()
val releaseKeyPassword = System.getenv("ANDROID_KEY_PASSWORD").orEmpty()
val releaseSigningValues = listOf(releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
val hasReleaseSigningConfiguration = releaseSigningValues.any(String::isNotBlank)

require(!hasReleaseSigningConfiguration || releaseSigningValues.all(String::isNotBlank)) {
    "Release signing requires ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, and ANDROID_KEY_PASSWORD."
}
require(!hasReleaseSigningConfiguration || releaseKeystore.isFile) {
    "Release keystore does not exist: $releaseKeystore"
}

android {
    signingConfigs {
        if (hasReleaseSigningConfiguration) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    namespace = "com.eaxor.easy2share"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.eaxor.easy2share"
        minSdk = 35
        targetSdk = 37
        versionCode = 4
        versionName = "0.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            applicationIdSuffix = ".debug"
            multiDexEnabled = false
            optimization {
                enable = false
                isDebuggable = true
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            multiDexEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = true
                isDebuggable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            // Netty jars ship overlapping metadata files that break APK resource merging.
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/services/reactor.blockhound.integration.BlockHoundIntegration"
            excludes += "META-INF/services/reactor.blockhound.integration.BlockHoundIntegration."
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.viewfinder.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.accompanist.permissions)
    implementation(libs.ktor.server.netty)
    implementation(libs.conscrypt.android)
    implementation(libs.ktor.server)
    implementation(libs.ktor.websockets)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.contentnegotiation)
    implementation(libs.slf4j.simple)
    implementation(libs.mlKit)
    implementation(libs.camera2)
    implementation(libs.cameraLifecycle)
    implementation(libs.cameraView)
    implementation(libs.cameraCore)
    implementation(libs.camera.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.switchy)

    debugImplementation(libs.leakcanary)

    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.konsist)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
