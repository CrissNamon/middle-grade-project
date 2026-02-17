plugins {
    id("java")
}

group = "ru.danilarassokhin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-messaging:7.0.3")
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.21")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}