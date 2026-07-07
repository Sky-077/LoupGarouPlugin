plugins {
    java
    kotlin("jvm")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "fr.dmall"
version = "1.0.0"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Le serveur réel tourne en Purpur 26.1.2 (pas 1.21.8 comme supposé au départ, voir CLAUDE.md).
    // Depuis Minecraft 26.1, Paper ne supporte plus le remappage vers les mappings Spigot (reobf) : les
    // plugins tournent directement en mappings Mojang. Pas de reobfJar à partir de cette version.
    paperweight.paperDevBundle("26.1.2.build.+")
    implementation(kotlin("stdlib-jdk8"))
}


java {
    toolchain {
        // Minecraft 26.1+ exige Java 25+ pour lancer/patcher le serveur (voir paperweight.paperDevBundle
        // ci-dessus) ; Gradle télécharge automatiquement ce JDK si besoin (foojay-resolver, settings.gradle.kts).
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}