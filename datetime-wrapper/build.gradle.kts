import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }
    
    iosArm64()
    iosSimulatorArm64()
    
    applyDefaultHierarchyTemplate()
    
    jvm()
    
    js(IR) {
        browser()
    }
    
    sourceSets {
        commonMain.dependencies {
            // This module is a thin wrapper around kotlinx.datetime
            // It ensures kotlinx.datetime is only compiled once for JS
            api(libs.kotlinx.datetime)
        }
        
        // Enable experimental datetime API for all source sets
        all {
            languageSettings {
                optIn("kotlinx.datetime.ExperimentalDateTime")
            }
        }
    }
}

// Ensure consistent version of kotlinx-datetime is used across all modules
configurations.all {
    resolutionStrategy {
        // Force the version of kotlinx-datetime to ensure consistency
        force("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.get()}")
        
        // Sort the dependency resolution to ensure consistent builds
        sortArtifacts(ResolutionStrategy.SortOrder.CONSUMER_FIRST)
        preferProjectModules()
    }
}

android {
    namespace = "com.lifeline.app.datetime"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

