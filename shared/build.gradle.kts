import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // Note: Using androidLibrary for now - the new plugin may not be available in AGP 8.11.2
    // Will migrate to com.android.kotlin.multiplatform.library when AGP 9.0 is released
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
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
    
    // Apply default hierarchy template
    applyDefaultHierarchyTemplate()
    
    jvm()
    
    js(IR) {
        browser()
    }
    
    // WASM target temporarily disabled - SQLDelight doesn't support WASM
    // Uncomment when SQLDelight adds WASM support or if database is not needed for WASM
    /*
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    */
    
    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            
            // DateTime - use version from catalog and export to dependent modules
            api(libs.kotlinx.datetime)
            
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // SQLDelight runtime - needed for all targets except wasm
            implementation(libs.sqldelight.runtime)

            implementation(libs.sqldelight.coroutines.extensions)
        }
        
        // Configure all source sets to use the experimental datetime API
        all {
            languageSettings {
                optIn("kotlinx.datetime.ExperimentalDateTime")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
        
        // Explicitly configure JS target
        jsMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.js)
            implementation(libs.sqldelight.runtime)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.android.driver)
        }
        
        jvmMain.dependencies {
            implementation(libs.ktor.client.jvm)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.jdbc.driver)
            // SQLite JDBC driver for JVM
            implementation("org.xerial:sqlite-jdbc:3.44.1.0")
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            // Coroutines test support
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${libs.versions.kotlinx.coroutines.get()}")
        }
        
        // JVM-only test dependencies
        val jvmTest by getting {
            dependencies {
                implementation(libs.mockk)
                implementation(libs.kotest.runner)
                implementation(libs.kotest.assertions)
            }
        }
    }
}

// No need to exclude kotlinx.datetime since we're using it directly
// The datetime-wrapper module is no longer required

android {
    namespace = "com.lifeline.app.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("LifelineDatabase") {
            packageName.set("com.lifeline.app.database")
            generateAsync.set(false) // Disabled to fix driver compatibility
            srcDirs("src/commonMain/sqldelight")
        }
    }
}
