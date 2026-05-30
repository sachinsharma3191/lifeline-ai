plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
}

// Configure Kotlin compiler options for all subprojects
// Using JVM_21 to match Java 21 (JVM_25 is not yet widely supported)
// Note: Using modern configuration approach instead of deprecated allprojects
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}

tasks.register<Copy>("syncSharedConfig") {
    group = "lifeline"
    description = "Copy language-agnostic UI config into Android/KMP and iOS resource folders"
    from("shared-config")
    into("shared/src/commonMain/resources/config")
    outputs.dir("shared/src/commonMain/resources/config")
}

tasks.register<Copy>("syncSharedConfigIos") {
    group = "lifeline"
    description = "Copy language-agnostic UI config into the iOS app bundle resources"
    from("shared-config")
    into("iosApp/iosApp/Resources/Config")
    outputs.dir("iosApp/iosApp/Resources/Config")
}

tasks.named("syncSharedConfig") {
    finalizedBy("syncSharedConfigIos")
}

project(":shared").tasks.matching { it.name.startsWith("compile") }.configureEach {
    dependsOn(rootProject.tasks.named("syncSharedConfig"))
}