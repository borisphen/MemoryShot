import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
//    kotlin("kapt")
}

// Чтение ключей
val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val groqApiKey = System.getenv("GROQ_API_KEY") ?: localProperties.getProperty("GROQ_API_KEY")
?: throw GradleException(
    """
                GROQ_API_KEY не найден! Добавьте его:
                1. В переменные окружения (для CI)
                2. В local.properties (для локальной сборки)
            """.trimIndent()
)

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.borisphen.memoryshot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.borisphen.memoryshot"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "GROQ_API_KEY", "\"$groqApiKey\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "GROQ_API_KEY", "\"$groqApiKey\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

detekt {
    toolVersion = "1.23.3"
    config.setFrom(files("../config/detekt.yml"))
}

dependencies {
    implementation(project(":feature:feature-voice:domain"))
    implementation(project(":feature:feature-voice:presentation"))
    implementation(project(":feature:feature-voice:data"))
    implementation(project(":util"))
    implementation(project(":core:core-data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}