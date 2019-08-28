import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.41"
	kotlin("plugin.spring") version "1.3.41"
}

group = "com.practice"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	compileOnly("org.springframework:spring-web")
	compileOnly("org.springframework:spring-context")
	compileOnly("org.apache.httpcomponents:httpclient")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("commons-io:commons-io")
}

dependencyManagement {
	dependencies {
		dependency("org.springframework:spring-web:5.0.5.RELEASE")
		dependency("org.springframework:spring-context:5.0.5.RELEASE")
		dependency("org.apache.httpcomponents:httpclient:4.5.5")
		dependency("com.fasterxml.jackson.core:jackson-databind:2.9.5")
		dependency("commons-io:commons-io:2.6")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
