plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

group = "me.owdding.auto-mixins"
version = rootProject.version

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.20-1.0.25")

    implementation(libs.gson)
}

tasks.test {
    useJUnitPlatform()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "processor"
            from(components["java"])
            version = project.version.toString()

            pom {
                this.name.set("processor")
                url.set("https://github.com/meowdding/auto-mixins")

                scm {
                    connection.set("git:https://github.com/meowdding/auto-mixins.git")
                    developerConnection.set("git:https://github.com/meowdding/auto-mixins.git")
                    url.set("https://github.com/meowdding/auto-mixins")
                }
            }
        }
    }
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

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}