plugins {
    id("java")
}

group = "won"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val springVersion = "7.0.7"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:${springVersion}")
    testImplementation("org.springframework:spring-test:${springVersion}")
    implementation("org.springframework:spring-web:${springVersion}")
    implementation("org.springframework:spring-webmvc:${springVersion}")

    implementation("org.aspectj:aspectjweaver:1.9.22")

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.test {
    useJUnitPlatform()
}