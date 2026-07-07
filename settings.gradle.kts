pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        kotlin("jvm") version "2.3.21"
    }
}

// Minecraft 26.1+ exige Java 25+ pour lancer le serveur (voir build.gradle.kts) : ce plugin permet à Gradle
// de télécharger automatiquement un JDK 25 si aucun n'est déjà installé sur la machine.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "LoupGarouPlugin"