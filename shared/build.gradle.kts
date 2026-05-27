import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    kotlin("plugin.serialization") version "1.9.23"
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    androidLibrary {
        namespace = "com.bsu.wangbo.weather.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        minSdk = 26

        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("io.ktor:ktor-client-core:3.0.0")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation("io.ktor:ktor-client-okhttp:3.0.0")
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-cio:3.0.0")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}