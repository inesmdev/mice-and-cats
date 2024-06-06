plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "foop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.13")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.4.12")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}