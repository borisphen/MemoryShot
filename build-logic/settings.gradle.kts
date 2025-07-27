pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // Можно добавить, если вашим плагинам в build-logic нужны плагины из Plugin Portal
    }
    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}