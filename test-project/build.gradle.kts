import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
    alias(libs.plugins.ksp)
    kotlin("jvm") version "2.2.20"
    id("me.owdding.auto-mixins")
}

extraProperties["auto-mixins-skip-dependency-registration"] = Unit

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    mavenCentral()
}

dependencies {
    ksp(project(":processor"))
}

autoMixins {
    mixinPackage = "me.owdding.mixins"
    nameMapping.put("", "meow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlin {
    jvmToolchain(21)
}