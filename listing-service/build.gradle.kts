plugins {
    java
    // Kotlin uses ("") and version "..."
    id("org.springframework.boot") version "3.5.8" // 3.5.8 does not exist yet!
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "listing-service"

java {
    toolchain {
        // Kotlin uses .set() or = for properties
        languageVersion.set(JavaLanguageVersion.of(21))
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
    // Kotlin requires parentheses ("...") and double quotes
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Correct artifact is 'spring-boot-starter-web', NOT 'webmvc'
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Kotlin DSL syntax for testing
tasks.withType<Test> {
    useJUnitPlatform()
}