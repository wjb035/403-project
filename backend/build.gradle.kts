plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

group = "com.basketball"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot SQL API connector"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

kotlin {
    jvmToolchain(17) 
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java", "src/main/kotlin"))
        }
		resources {
            srcDir("src/main/resources")
        }
    }
}

repositories {
	mavenCentral()   
	maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") 
    implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
	implementation("net.sf.extjwnl:extjwnl:2.0.5")
	implementation("net.sf.extjwnl:extjwnl-data-wn31:1.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
