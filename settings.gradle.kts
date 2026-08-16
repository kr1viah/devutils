import shadow.com.google.gson.Gson
import shadow.com.google.gson.JsonObject
import java.io.InputStreamReader
import java.net.URI

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10-alpha.5"
    id("dev.kikugie.loom-back-compat") version "0.4.2"

    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

val metaUrl = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"

val input = InputStreamReader(URI.create(metaUrl).toURL().openStream())
val json = Gson().fromJson(input, JsonObject::class.java)!!

val all = json.get("versions").asJsonArray
    .map { it.asJsonObject }
    .filter { it["type"].asString == "release" }
    .map { it["id"].asString }
    .dropLastWhile { it != "1.14.4" }

stonecutter {
    create(rootProject) {
        all.forEach { version(it) }
        vcsVersion = "26.2"
    }
}

rootProject.name = "devutils"
