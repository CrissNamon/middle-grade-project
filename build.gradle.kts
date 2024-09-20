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

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}