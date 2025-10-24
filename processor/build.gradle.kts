plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

group = "me.owdding"
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
        fun createDefault(name: String, task: Jar, suffix: String = "") = create<MavenPublication>(name) {
            artifactId = "AutoMixins"
            artifact(task)
            version = project.version.toString() + suffix

            pom {
                this.name.set("AutoMixins")
                url.set("https://github.com/meowdding/auto-mixins")

                scm {
                    connection.set("git:https://github.com/meowdding/auto-mixins.git")
                    developerConnection.set("git:https://github.com/meowdding/auto-mixins.git")
                    url.set("https://github.com/meowdding/auto-mixins")
                }
            }
        }
        createDefault("maven", tasks.jar.get())
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