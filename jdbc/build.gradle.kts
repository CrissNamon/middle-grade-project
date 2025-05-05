plugins {
    id("java")
    id("io.freefair.lombok") version "8.10"
}

group = "ru.danilarassokhin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("tech.hiddenproject:progressive-api:0.7.11")
    implementation(project(":dependency-injection"))
    implementation(project(":resilience"))
    implementation(project(":utils"))
    implementation("io.github.resilience4j:resilience4j-all:2.2.0")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("tech.hiddenproject:aide-all:1.3")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}