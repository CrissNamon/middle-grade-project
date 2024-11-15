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
    //Web
    implementation("io.netty:netty-all:4.1.113.Final")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("org.glassfish.expressly:expressly:6.0.0-M1")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    //Util
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("tech.hiddenproject:aide-all:1.3")
    //Logging
    implementation("org.slf4j:slf4j-reload4j:2.0.16")
    //Database
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:10.20.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.20.1")
    implementation("net.ttddyy:datasource-proxy:1.10")
    //MapStruct
    implementation("org.mapstruct:mapstruct:1.6.2")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")
    //Camunda
    implementation("io.camunda:zeebe-client-java:8.6.5")
    implementation("io.camunda:zeebe-bpmn-model:8.6.5")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.1")
}

tasks.test {
    useJUnitPlatform()
}