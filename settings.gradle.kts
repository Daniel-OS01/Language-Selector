pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        // Multiple repository sources for Shizuku and other dependencies
        maven("https://maven.rikka.app/releases")
        maven("https://maven.rikka.app/snapshots")
        maven("https://repo1.maven.org/maven2")
    }
}

rootProject.name = "language_selector"
include(":app")
include(":hidden_api")
