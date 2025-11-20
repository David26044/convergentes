// settings.gradle.kts (único archivo, sin duplicados)

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // (Opcional) No hace falta JitPack aquí porque MPAndroidChart es una dependencia, no un plugin.
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // 👈 necesario para MPAndroidChart
    }
}

rootProject.name = "convergentes"
include(":app")
