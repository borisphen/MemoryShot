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
    }
}

rootProject.name = "MemoryShot"
include(":app")
include(":feature:feature-voice:presentation")
include(":feature:feature-voice:domain")
include(":feature:feature-voice:data")
include(":util")
include(":core:core-data")
include(":core:core-domain")
include(":core:core-ui")
include(":feature:feature-history:presentation")
 