import org.gradle.kotlin.dsl.*

plugins {
    kotlin("jvm") version "1.1.4-3"
    id("cn.bestwu.plugin-publish").version("0.0.12")
    id("cn.bestwu.idea") version "0.0.4"
}

group = "cn.bestwu.gradle"
version = "0.0.4"

kotlin {
    copyClassesToJavaOutput = true
}

repositories {
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.4-3")

    testCompile("org.jetbrains.kotlin:kotlin-test-junit:1.1.4-3")
}