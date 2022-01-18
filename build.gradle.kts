import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jmailen.kotlinter") version "3.6.0"
    id("com.avast.gradle.docker-compose") version "0.14.9"
    kotlin("jvm") version "1.5.32"
    kotlin("plugin.spring") version "1.5.32"
    kotlin("plugin.jpa") version "1.5.32"
    kotlin("plugin.allopen") version "1.5.32"
    kotlin("plugin.noarg") version "1.5.32"
    kotlin("kapt") version "1.5.32"
}

group = "io.mustelidae.otter.neotropical"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
}

ext["log4j2.version"] = "2.17.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude("io.undertow", "undertow-websockets-jsr")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("com.querydsl:querydsl-jpa:5.0.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.1")

    testImplementation("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.25")

}

configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
}

noArg {
    invokeInitializers = true
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.11"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                val output =
                    "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n${"-".repeat(repeatLength)}\n|  $output  |\n${"-".repeat(repeatLength)}")
                println("\nElapsed: ${(result.endTime - result.startTime) / 1000} sec\n ")
            }
        }
    })
}