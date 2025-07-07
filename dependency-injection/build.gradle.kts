plugins {
    id("java")
    id("io.freefair.lombok") version "8.10"
}

group = "ru.danilarassokhin.injection"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":utils"))
    implementation("tech.hiddenproject:progressive-api:0.7.11")
    implementation("tech.hiddenproject:progressive-injection:0.7.11")
    implementation("org.slf4j:slf4j-reload4j:2.0.16")
    implementation("org.reflections:reflections:0.10.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}