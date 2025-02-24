// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Prefer settings repositories explicitly
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QTalk"
include(":app")
