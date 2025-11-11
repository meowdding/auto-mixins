import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import kotlin.io.path.inputStream

plugins {
    kotlin("jvm") version "2.2.20"
    id("net.kyori.blossom") version "2.1.0"
    `java-gradle-plugin`
    `maven-publish`
}

val properties = Properties().apply { load(projectDir.toPath().resolve("../gradle.properties").inputStream()) }
group = "me.owdding.auto-mixins"
version = properties["version"].toString()

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)
    implementation(libs.ksp.gradle)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
}

gradlePlugin {
    plugins {
        create("meowdding-auto-mixins") {
            id = "me.owdding.auto-mixins"
            implementationClass = "me.owdding.AutoMixinsPlugin"
        }
    }
}


publishing {
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/thatgravyboat/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("version", version.toString())
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    inputs.property("project_version_", version.toString())
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}