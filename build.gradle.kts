import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "2.1.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("com.palantir.docker") version "0.20.1"
}

group = "software.engineering.task"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.json", "json", "20140107")
    compile("com.google.code.gson:gson:2.8.5")
    compile("org.apache.httpcomponents:httpclient:4.5.7")
    compile("org.cryptacular:cryptacular:1.2.3")
    compile("redis.clients:jedis:2.9.0")
    compile("org.springframework.data:spring-data-redis:2.0.3.RELEASE")
    compile("com.itextpdf:itextpdf:5.5.10")
    compile("org.apache.pdfbox:pdfbox:2.0.4")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<Copy>("unpack") {
    dependsOn(tasks.named<BootJar>("bootJar"));

    from(zipTree(tasks.named<BootJar>("bootJar").get().outputs.files.singleFile))
    into("build/dependency")
}

docker {
    name = tasks.named<BootJar>("bootJar").get().baseName
    copySpec.from(tasks["unpack"].outputs).into("dependency2121")
    buildArgs(mapOf("DEPENDENCY" to "dependency2121"))
}

tasks["build"].finalizedBy(tasks["docker"])