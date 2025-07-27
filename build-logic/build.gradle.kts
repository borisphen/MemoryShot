// build-logic/build.gradle.kts
plugins {
    `kotlin-dsl` // Специальный плагин для создания Gradle-плагинов на Kotlin
}

group = "com.borisphen.memoryshot.buildlogic" // Произвольный group ID для ваших плагинов

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.gradleplugin.android)
    compileOnly(libs.gradleplugin.kotlin)
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        register("androidLibraryConvention") {
            id = "com.borisphen.memoryshot.android.library" // Уникальный ID вашего плагина
            implementationClass = "AndroidLibraryConventionPlugin" // Класс, который мы создали
        }
        register("androidApplicationConvention") {
            id = "com.borisphen.memoryshot.android.application" // Уникальный ID вашего плагина
            implementationClass = "AndroidApplicationConventionPlugin" // Класс, который мы создали
        }
        register("composeConvention") {
            id = "com.borisphen.memoryshot.compose" // Уникальный ID вашего плагина
            implementationClass = "ComposeConventionPlugin" // Класс, который мы создали
        }
    }
}