plugins {
    java
    // Kotlin uses ("") and strictly requires valid versions
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "verification-service"

java {
    toolchain {
        // Kotlin syntax uses .set()
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

repositories {
    mavenCentral()
}
//extra["springCloudVersion"] = "2024.0.0"

dependencies {
    // Fixed: Removed the accidental double 'dependencies { dependencies {' nesting
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Fixed: Kotlin syntax for configuring tests
tasks.withType<Test> {
    useJUnitPlatform()
}