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
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

// Le jar par défaut (mappings Mojang, tel que compilé) ne tourne PAS sur un serveur Paper/Purpur classique :
// il faut le jar reobfusqué (mappings serveur). On garde donc le chemin habituel build/libs/LoupGarouPlugin-1.0.0.jar
// pour le jar déployable (reobf), et on déplace le jar mojang-mappé sous un suffixe -dev pour éviter toute confusion.
tasks.jar {
    archiveClassifier.set("dev")
}

tasks.named<io.papermc.paperweight.tasks.RemapJar>("reobfJar") {
    outputJar.set(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}