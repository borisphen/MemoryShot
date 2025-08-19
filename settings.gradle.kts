// settings.gradle
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://androidx.dev/snapshots/builds/13617490/artifacts/repository")
        }
    }
}

rootProject.name = "MemoryShot"
include(":app")
include(":feature:feature-voice:presentation")
include(":feature:feature-voice:domain")
include(":feature:feature-voice:data")
include(":core:core-data")
include(":core:core-domain")
include(":core:core-ui")
include(":feature:feature-history:presentation")
include(":utils:util")
include(":utils:util-ui")
include(":utils:util-platform")
