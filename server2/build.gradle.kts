import au.com.dius.pact.provider.PactVerification.ANNOTATED_METHOD
import au.com.dius.pact.provider.gradle.PactBrokerConsumerConfig
import au.com.dius.pact.provider.gradle.PactBrokerConsumerConfig.latestTags
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
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
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("au.com.dius.pact.provider:junit5spring:4.1.2")
    testImplementation("au.com.dius.pact.provider:junit5:4.1.2")
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

// pactPublish, pactVerify 등의 gradle task에서 이 설정을 이용한다.
pact {
    broker {
        pactBrokerUrl = "https://tossbank.pactflow.io/"
        pactBrokerToken = "oyyVdOKk6IQIhv8YybVXhg"
    }

    // See https://docs.pact.io/implementation_guides/jvm/provider/gradle/#2-define-the-pacts-between-your-consumers-and-providers
    serviceProviders {
        create("server2-producer") {
            verificationType = ANNOTATED_METHOD
            packagesToScan = listOf("com.tossbank.pact.samples.server2")

            fromPactBroker(closureOf<PactBrokerConsumerConfig> {
                this.selectors = latestTags("live")
            })
        }

        create("server2") {
            // API provider 설정. ./gradlew pactVerify 하면 이 서버가 pact를 준수하는지 검사한다.
            protocol = "http"
            host = "localhost"
            port = "8080"
            path = "/"

            fromPactBroker(closureOf<PactBrokerConsumerConfig> {
                this.selectors = latestTags("live")
            })
        }
    }
}
