import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.palantir.git-version") version "0.12.3"
	id("org.springframework.boot") version "2.3.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("au.com.dius.pact") version "4.1.2"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

group = "com.tossbank"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
    jcenter()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("au.com.dius.pact.consumer:junit5:4.1.0")
	testImplementation("io.rest-assured:rest-assured:3.3.0")
	implementation("com.google.code.gson:gson:2.8.5")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

pact {
	broker {
		pactBrokerUrl = "https://tossbank.pactflow.io/"
		pactBrokerToken = "<pact-broker-token>"
	}

	publish {
		tags = listOf("live")
	}
}
