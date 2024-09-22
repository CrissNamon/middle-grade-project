plugins {
    id("java")
    application
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
    implementation("io.netty:netty-all:4.1.113.Final")
    implementation("tech.hiddenproject:aide-all:1.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.6.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.6.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.6.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}