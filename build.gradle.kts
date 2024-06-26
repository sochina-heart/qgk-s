plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.allopen") version "1.9.23"
    id("io.quarkus")
}

extra["bouncy"] = "1.77"

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-mutiny")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkiverse.mybatis:quarkus-mybatis-plus:2.2.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-jackson")
    implementation("io.quarkus:quarkus-arc")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    implementation("org.bouncycastle:bcprov-jdk18on:${property("bouncy")}")
    implementation("org.bouncycastle:bcpkix-jdk18on:${property("bouncy")}")
}

group = "com.sochina"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_21.toString()
    kotlinOptions.javaParameters = true
}
