plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

group = "babou.speartech"
version = "2.0.0"

base {
    archivesName = "spear-tech"
}

repositories {
    maven("https://maven.meteordev.org/releases") {
        name = "Meteor Releases"
    }

    maven("https://maven.meteordev.org/snapshots") {
        name = "Meteor Snapshots"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:26.2")
    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("meteordevelopment:meteor-client:26.2-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.processResources {
    val values = mapOf(
        "version" to project.version,
        "minecraft_version" to "~26.2",
        "jdk_version" to "25"
    )

    inputs.properties(values)

    filesMatching("fabric.mod.json") {
        expand(values)
    }
}
