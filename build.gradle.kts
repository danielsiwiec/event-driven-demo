import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.springframework.boot") version "2.6.4" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version "1.7.20" apply false
    kotlin("plugin.spring") version "1.7.20" apply false
}

subprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
    }

    group = "com.dansiwiec"
    version = "1.0.0"

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
}

configure(subprojects.filter({ it.path.startsWith(":services:") })) {
    apply {
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    val implementation by configurations
    val testImplementation by configurations

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.kafka:spring-kafka")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        testImplementation("org.springframework.kafka:spring-kafka-test")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.awaitility:awaitility:4.2.0")
        testImplementation("org.testcontainers:testcontainers:1.17.5")
        testImplementation("org.testcontainers:junit-jupiter:1.17.5")
        testImplementation("org.testcontainers:kafka:1.17.5")
        testImplementation("org.mockito:mockito-inline:4.8.1")
        testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner:3.1.4")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
        testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        testLogging.showStandardStreams = true
    }
}