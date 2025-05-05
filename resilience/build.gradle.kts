plugins {
    id("java")
    id("io.freefair.lombok") version "8.10"
}

group = "ru.danilarassokhin.resilience"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("tech.hiddenproject:progressive-api:0.7.11")
    implementation(project(":dependency-injection"))
    implementation("io.github.resilience4j:resilience4j-all:2.2.0")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.github.ben-manes.caffeine:jcache:3.1.8")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}