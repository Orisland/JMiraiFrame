plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.8.0-M1"
}

group = "org.orisland"
version = "1.0-SNAPSHOT"

//repositories {
//    mavenLocal()
//    maven("https://maven.aliyun.com/repository/public")
//    mavenCentral()
////    jcenter()
//}

repositories {
    removeIf { it is MavenArtifactRepository && it.url.host == "dl.bintray.com" }
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
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
// https://mvnrepository.com/artifact/com.github.luues.tool/tool-setting
    implementation("com.github.luues.tool:tool-setting:1.0.0.0.RELEASE")
    // https://mvnrepository.com/artifact/org.yaml/snakeyaml
    implementation("org.yaml:snakeyaml:1.29")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    // https://mvnrepository.com/artifact/com.baidu.aip/java-sdk
    implementation("com.baidu.aip:java-sdk:4.16.2")

    implementation("com.aliyun.oss:aliyun-sdk-oss:3.10.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("javax.activation:activation:1.1.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.13.1")
    // https://mvnrepository.com/artifact/com.alibaba/fastjson
    implementation ("com.alibaba:fastjson:1.2.79")


}
