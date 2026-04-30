// Configure Android SDK location from environment if not already set
val localPropertiesFile = File(settingsDir, "local.properties")
if (!localPropertiesFile.exists()) {
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null) {
        localPropertiesFile.writeText("sdk.dir=$androidHome\n")
    }
}

pluginManagement {
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
        mavenCentral()
    }
}

rootProject.name = "MeritMosaic"
include(":app")
