import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version ("3.1.1")
    java
}
// tests the frontend (result is in build)
val npmRunTests = tasks.register<NpmTask>("npmRunTests") {
    dependsOn(tasks.npmInstall)
    environment.put("CI", "true")
    args.set(listOf("run", "test"))
}

// build the frontend (result is in build)
val npmRunBuild = tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall, npmRunTests)
    outputs.dir("build/resources/main/static")
    args.set(listOf("run", "build"))
}
tasks.jar {
    dependsOn(npmRunBuild)
}

// start local development server
val npmStart = tasks.register<NpmTask>("npmStart") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("start"))
}

sourceSets {
    main {
        resources {
            setSrcDirs(listOf("dist"))
        }
    }
}

