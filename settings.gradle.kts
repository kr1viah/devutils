import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

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
    id("dev.kikugie.stonecutter") version "0.9.7"
    id("dev.kikugie.loom-back-compat") version "0.4.2"

    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.google.code.gson:gson:2.13.1")
    }
}

object Versions {
    val ALL_LIST: List<String>

    init {
        val metaUrl = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
        val gson: com.google.gson.Gson = com.google.gson.Gson()

        val url = URI.create(metaUrl)
        val json: JsonObject

        try {
            InputStreamReader(
                url.toURL().openStream(), StandardCharsets.UTF_8
            ).use { reader ->
                json = gson.fromJson(reader, JsonObject::class.java)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        val versions: JsonArray = json.get("versions").getAsJsonArray()

        val allVersionsStrings = versions.asList().stream()
            .map { obj: JsonElement -> obj.getAsJsonObject() }
            .filter { version: JsonObject -> version.get("type").asString == "release" }
            .map { version: JsonObject -> version.get("id").asString }
            .collect(Collectors.toList())
            .reversed()

        ALL_LIST = allVersionsStrings
            .subList(60, allVersionsStrings.size)
            .reversed()
    }
}


stonecutter {
    create(rootProject) {
        for (version in Versions.ALL_LIST) {
            version(version)
        }
        versions("1.21.1", "1.21.11")
        version("26.2.x", "26.2")
        vcsVersion = "26.2.x"
    }
}

rootProject.name = "devutils"
