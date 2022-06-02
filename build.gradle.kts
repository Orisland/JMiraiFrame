plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.11.0"
}

group = "org.orisland"
version = "1.0"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
    jcenter()
    gradlePluginPortal()
}

ext {
    var jacksonVersion = "2.12.3"
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
    runtimeOnly("com.lmax:disruptor:3.4.4")

    api("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation("junit:junit:4.12")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("cn.hutool:hutool-core:5.8.1")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

}
