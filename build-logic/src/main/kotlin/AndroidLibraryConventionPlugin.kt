// build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // Apply standard Android Library and Kotlin Android plugins
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            // Configure the Android Library extension
            // Use extensions.configure<LibraryExtension> instead of direct android { ... }
            extensions.configure<LibraryExtension> {
                // Use versions from libs.versions.toml for consistency
                compileSdk = 34

                defaultConfig {
                    minSdk = 26
                    testOptions.targetSdk = 34
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                    debug { // It's good practice to have a debug type, even for libraries.
                        isMinifyEnabled = false
                    }
                }

                // Set Java compatibility to Java 17 (or your chosen version)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                // Kotlin options are now configured via tasks.withType<KotlinCompile>
                // This block is NOT directly in extensions.configure.

            } // <--- END of extensions.configure<LibraryExtension> block

            // Configure JVM Target for Kotlin compilation tasks (outside the android {} block)
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17) // Ensure this matches your Java version
                }
            }
        }
    }
}