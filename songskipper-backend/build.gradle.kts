import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.7.0"            // id is org.jetbrains.kotlin.jvm
	kotlin("plugin.spring") version "1.7.0"    // id is org.jetbrains.kotlin.plugin.spring
	kotlin("plugin.jpa") version "1.7.0"    // id is org.jetbrains.kotlin.plugin.jpa
	jacoco
	id("com.palantir.docker") version ("0.33.0")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":songskipper-frontend"))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	// h2 for testing
	implementation("com.h2database:h2")
	// postgres for prod
	runtimeOnly("org.postgresql:postgresql")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.1")
	implementation("se.michaelthelin.spotify:spotify-web-api-java:7.1.0")
	implementation("org.slf4j:slf4j-api")
	implementation("org.liquibase:liquibase-core")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.8")
	implementation("io.micrometer:micrometer-registry-prometheus")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.bootRun {
	args = listOf("--spring.profiles.active=dev")
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.dockerPrepare {
	dependsOn(tasks.bootJar.get(), tasks.jar.get())
}

docker {
	name = "ghcr.io/schmidti159/songskipper/backend:${project.version}"

	setDockerfile(File("docker/Dockerfile"))
	files("build/libs")
	buildArgs(mapOf(Pair("BUILD_VERSION", project.version as String)))
}
