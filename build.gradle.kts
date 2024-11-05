plugins {
    id("java")
    application
    id("io.freefair.lombok") version "8.10"
}

application {
    mainClass = "ru.danilarassokhin.game.GameApplication"
}

group = "ru.danilarassokhin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Dependency Injection
    implementation("tech.hiddenproject:progressive-api:0.7.11")
    implementation("tech.hiddenproject:progressive-injection:0.7.11")
    // Web
    implementation("io.netty:netty-all:4.1.113.Final")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    // Logging
    implementation("org.slf4j:slf4j-reload4j:2.0.16")
    // Utils
    implementation("tech.hiddenproject:aide-all:1.3")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    // Database
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.h2database:h2:2.3.232")
    //Camunda
    implementation("org.camunda.bpm:camunda-engine:7.22.0")

    //Tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

tasks.test {
    useJUnitPlatform()
}