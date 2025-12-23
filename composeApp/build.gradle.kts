import org.gradle.api.JavaVersion
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // iOS targets
    jvm()

    js(IR) {
        browser()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()
    
    // Configure all source sets to use the experimental datetime API
    sourceSets.all {
        languageSettings {
            optIn("kotlinx.datetime.ExperimentalDateTime")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            // implementation(libs.decompose.compose)

            api(projects.shared)
            // Add decompose dependencies
            implementation(libs.decompose)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.instance.keeper)
            implementation(libs.essenty.back.handler)
        }
        
        jsMain.dependencies {
            // JS-specific dependencies
            implementation(libs.ktor.client.js)
            implementation(libs.sqldelight.runtime)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            // kotlinx.datetime provided by shared module's androidMain
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // kotlinx.datetime provided by shared module's jvmMain
        }

        // jsMain dependencies moved above to commonMain section
    }
}

android {
    namespace = "com.lifeline.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.lifeline.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    // Corrected to use the compose plugin helper
    debugImplementation(compose.uiTooling)
}

// Exclude kotlinx.datetime from JS compilation in composeApp module
// datetime-wrapper module is the ONLY module that should compile kotlinx.datetime for JS
// We exclude it from JS compile classpath to prevent duplicate compilation
// Types are still available through datetime-wrapper's compiled klib
configurations.matching {
    it.name.contains("js", ignoreCase = true) &&
    it.name.contains("compileClasspath", ignoreCase = true)
}.configureEach {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-datetime")
}

compose.desktop {
    application {
        mainClass = "com.lifeline.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.lifeline.app"
            packageVersion = "1.0.0"
        }
    }
}