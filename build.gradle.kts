plugins {
    java
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "CapstoneProject"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Pure security regression suites run with mocks and never require production credentials.
subprojects {
    plugins.withId("org.springframework.boot") {
        tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") { archiveFileName.set("app.jar") }
    }
    plugins.withId("java") {
        val sources = extensions.getByType<SourceSetContainer>()
        tasks.register<Test>("securityTest") {
            testClassesDirs = sources["test"].output.classesDirs
            classpath = sources["test"].runtimeClasspath
            useJUnitPlatform()
            filter {
                includeTestsMatching("*SecurityTest")
                isFailOnNoMatchingTests = false
            }
        }
    }
}
tasks.register("securityRegression") { dependsOn(subprojects.map { "${it.path}:securityTest" }) }
