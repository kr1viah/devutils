plugins {
    id("dev.kikugie.loom-back-compat")
    id("maven-publish")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val mapi: String = project.property("deps.malilib_api") as String
val malilibApiVersion: String = when {
    sc.current.parsed >= "26.2" -> "$mapi-26.2"
    sc.current.parsed >= "26.1" -> "$mapi-26.1"
    sc.current.parsed >= "1.21.11" -> "$mapi-1.21.11"
    sc.current.parsed >= "1.21.9" -> "$mapi-1.21.10"
    sc.current.parsed >= "1.21.6" -> "$mapi-1.21.8"
    sc.current.parsed >= "1.21.2" -> "$mapi-1.21.5"
    sc.current.parsed >= "1.21" -> "$mapi-1.21"
    sc.current.parsed >= "1.20.6" -> "$mapi-1.20.6"
    sc.current.parsed >= "1.20.5" -> "$mapi-1.20.5"
    sc.current.parsed >= "1.20.3" -> "$mapi-1.20.4"
    sc.current.parsed >= "1.20.2" -> "$mapi-1.20.2"
    sc.current.parsed >= "1.20" -> "$mapi-1.20.1"
    sc.current.parsed >= "1.18" -> "$mapi-1.19.4"
    sc.current.parsed >= "1.17" -> "$mapi-1.17.1"
    sc.current.parsed >= "1.16" -> "$mapi-1.16.5"
    sc.current.parsed >= "1.14" -> "$mapi-1.15.2"
    else -> throw IllegalStateException()
}

//@formatter-off
val malilibVersion: String = when {
    sc.current.parsed >= "26.2"   -> "26.2"    +":"+ "0.29.3"
    sc.current.parsed >= "26.1"   -> "26.1.2"  +":"+ "0.28.9"
    sc.current.parsed >= "1.21.11"-> "1.21.11" +":"+ "0.27.16"
    sc.current.parsed >= "1.21.10"-> "1.21.10" +":"+ "0.26.8"
    sc.current.parsed >= "1.21.8" -> "1.21.8"  +":"+ "0.25.7"
    sc.current.parsed >= "1.21.5" -> "1.21.5"  +":"+ "0.24.3"
    sc.current.parsed >= "1.21.4" -> "1.21.4"  +":"+ "0.23.5"
    sc.current.parsed >= "1.21.3" -> "1.21.3"  +":"+ "0.22.8"
    sc.current.parsed >= "1.21.1" -> "1.21"    +":"+ "0.21.10"
    sc.current.parsed >= "1.20.6" -> "1.20.6"  +":"+ "0.19.2"
    sc.current.parsed >= "1.20.4" -> "1.20.4"  +":"+ "0.18.3"
    sc.current.parsed >= "1.20.2" -> "1.20.2"  +":"+ "0.17.0"
    sc.current.parsed >= "1.20.1" -> "1.20.1"  +":"+ "0.16.2"
    sc.current.parsed >= "1.19.4" -> "1.19.4"  +":"+ "0.15.4"
    sc.current.parsed >= "1.19.3" -> "1.19.3"  +":"+ "0.14.0"
    sc.current.parsed >= "1.19.2" -> "1.19.2"  +":"+ "0.13.0"
    sc.current.parsed >= "1.19"   -> "1.19.0"  +":"+ "0.13.0"
    sc.current.parsed >= "1.18.2" -> "1.18.2"  +":"+ "0.12.3-alpha.2"
    sc.current.parsed >= "1.18.1" -> "1.18.1"  +":"+ "0.11.8"
    sc.current.parsed >= "1.17.1" -> "1.17.1"  +":"+ "0.10.0-dev.26"
    sc.current.parsed >= "1.16.5" -> "1.16.5"  +":"+ "0.10.0-dev.21+arne.8"
    sc.current.parsed >= "1.16.4" -> "1.16.4"  +":"+ "0.10.0-dev.21+arne.7"
    sc.current.parsed >= "1.16.3" -> "1.16.3"  +":"+ "0.10.0-dev.21+arne.1"
    sc.current.parsed >= "1.16.2" -> "1.16.2"  +":"+ "0.10.0-dev.21+arne.1"
    sc.current.parsed >= "1.16.1" -> "1.16.1"  +":"+ "0.10.0-dev.21+arne.2"
    sc.current.parsed >= "1.16"   -> "1.16.0"  +":"+ "0.10.0-dev.21+beta.1"
    sc.current.parsed >= "1.15.2" -> "1.15.2"  +":"+ "0.10.0-dev.21+arne.4"
    sc.current.parsed >= "1.15.1" -> "1.15.1"  +":"+ "0.10.0-dev.20+beta.1"
    sc.current.parsed >= "1.15"   -> "1.15.0"  +":"+ "0.10.0-dev.20+beta.2"
    sc.current.parsed >= "1.14.4" -> "1.14.4"  +":"+ "0.10.0-dev.20+arne.3"
    sc.current.parsed >= "1.14.3" -> "1.14.3"  +":"+ "0.10.0-dev.20"
    sc.current.parsed >= "1.14.2" -> "1.14.2"  +":"+ "0.10.0-dev.20"
    sc.current.parsed >= "1.14"   -> "1.14.0"  +":"+ "0.10.0-dev.19"
    else -> throw IllegalStateException()
}
//@formatter-on

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://repo.repsy.io/kr1v/maven", "kr1v", "net.kr1v")
    maven("https://masa.dy.fi/maven/")
    maven("https://masa.dy.fi/maven/sakura-ryoko/")
    maven("https://maven.fallenbreath.me/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    include(modImplementation("net.kr1v:malilib-api:${malilibApiVersion}") {
        exclude("fi.dy.masa.malilib")
        exclude("io.github.prospector")
    })
    annotationProcessor("net.kr1v:malilib-api-processor:1.0.0")
    include(modImplementation("fi.dy.masa.malilib:malilib-fabric-$malilibVersion") {
        exclude("io.github.prospector")
    })
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    accessWidenerPath = sc.process(
        rootProject.file("src/main/resources/devutils.ct"),
        "build/processed.ct"
    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            put("minecraft", sc.current.version)
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "net.kr1v.devutils"
            artifactId = "devutils-${sc.current.version}"
            version = "${project.property("mod.version")}"

            from(components["java"])
        }
    }

    repositories {
        mavenLocal()

        val repsyToken = providers.environmentVariable("REPSY_TOKEN")
        val repsyUsername = providers.environmentVariable("REPSY_USERNAME")

        if (!repsyToken.isPresent || repsyToken.get().isEmpty()) {
            throw GradleException("Missing REPSY_TOKEN")
        }

        if (!repsyUsername.isPresent || repsyUsername.get().isEmpty()) {
            throw GradleException("Missing REPSY_USERNAME")
        }

        maven {
            name = "repsy"
            url = uri("https://repo.repsy.io/kr1v/maven/")

            credentials {
                username = repsyUsername.get()
                password = repsyToken.get()
            }
        }
    }
}