plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11"

tasks.register("runClient_active_version") {
    group = "activeVersion"
    val wantedTask = "runClient"
    sc.current?.version?.let { version ->
        dependsOn(":$version:$wantedTask")
    }
}

tasks.register("build_active_version") {
    group = "activeVersion"
    val wantedTask = "build"
    sc.current?.version?.let { version ->
        dependsOn(":$version:$wantedTask")
    }
}