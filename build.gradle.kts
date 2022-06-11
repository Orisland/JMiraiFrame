plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "org.orisland"
version = "0.20"

repositories {
//    removeIf { it is MavenArtifactRepository && it.url.host == "dl.bintray.com" }
    mavenLocal()
//    maven(url = "https://maven.aliyun.com/repository/public")
//    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
//    maven(url = "https://repository.apache.org/content/groups/snapshots/")
    mavenCentral()
    gradlePluginPortal()
}

ext {
    var jacksonVersion = "2.12.3"
}


val miraiVersion = "2.11.0"
fun mirai(module: String) = "net.mamoe:mirai-$module:$miraiVersion"

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
    runtimeOnly("com.lmax:disruptor:3.4.4")

    api("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation("junit:junit:4.12")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("cn.hutool:hutool-core:5.8.1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    testImplementation("com.google.cloud:google-cloud-translate:2.1.13")
    implementation("org.mybatis:mybatis:3.5.9")
    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.1")
    implementation("cn.hutool:hutool-cron:5.8.1")
    implementation("org.jsoup:jsoup:1.15.1")
    implementation("io.github.biezhi:TinyPinyin:2.0.3.RELEASE")

}