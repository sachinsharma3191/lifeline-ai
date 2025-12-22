import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
    
    iosArm64()
    iosSimulatorArm64()
    
    // Apply default hierarchy template to create iosMain source set
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
            
            // DateTime
            implementation(libs.kotlinx.datetime)
            
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // SQLDelight (excluded from wasmJs - not supported)
            // Note: SQLDelight doesn't support WASM, so we add it per-target
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.android.driver)
        }
        
        // Configure iOS source set - ios() creates iosMain automatically
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.native.driver)
        }
        
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.sqldelight.runtime)
            // Try adding sqljs-driver directly - if this fails, JS database support will be disabled
            // Uncomment the line below if sqljs-driver becomes available:
            // implementation("app.cash.sqldelight:sqljs-driver:${libs.versions.sqldelight.get()}")
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
