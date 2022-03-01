plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.0"
}

group = "org.orisland"
version = "1.0-SNAPSHOT"

repositories {
    removeIf { it is MavenArtifactRepository && it.url.host == "dl.bintray.com" }
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    maven(url = "https://repository.apache.org/content/groups/snapshots/")
    mavenCentral()
//    jcenter()
    gradlePluginPortal()
}

ext {
    var jacksonVersion = "2.12.3"
}

val log4jVersion = "2.17.1"
fun log4j(module: String) = "org.apache.logging.log4j:log4j-$module:$log4jVersion"
val miraiVersion = "2.10.0"
fun mirai(module: String) = "net.mamoe:mirai-$module:$miraiVersion"

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    implementation(mirai("logging-log4j2"))
    runtimeOnly(log4j("core"))
    implementation(log4j("api"))
    runtimeOnly("com.lmax:disruptor:3.4.4")


    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.13.1")
    // https://mvnrepository.com/artifact/com.alibaba/fastjson
    implementation ("com.alibaba:fastjson:1.2.79")

    api("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation("junit:junit:4.12")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    implementation("org.projectlombok:lombok:1.18.22")

    annotationProcessor("org.projectlombok:lombok:1.18.22")
}