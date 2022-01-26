import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version ("3.1.1")
    java
    id("com.palantir.docker") version ("0.32.0")
}

// tests the frontend (result is in build)
val npmRunTests = tasks.register<NpmTask>("npmRunTests") {
    dependsOn(tasks.npmInstall)
    outputs.dir("coverage")
    inputs.file("package-lock.json")
    inputs.dir("src")
    environment.put("CI", "true")
    args.set(listOf("run", "test"))
}

// build the frontend (result is in build)
val npmRunBuild = tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall, npmRunTests)
    outputs.dir("build/resources/main/static")
    inputs.dir("src")
    inputs.dir("public")
    inputs.file("package-lock.json")
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

tasks.dockerPrepare {
    dependsOn(npmRunBuild.get(), tasks.processResources.get())
}
docker {
    name = "ghcr.io/schmidti159/songskipper/frontend:${project.version}"

    setDockerfile(File("docker/Dockerfile"))
    files("build/resources/main", "docker/nginx-default.conf.template")

}

