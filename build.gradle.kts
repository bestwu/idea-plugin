
import org.gradle.kotlin.dsl.*

plugins {
    kotlin("jvm") version "1.1.4-3"
    id("cn.bestwu.plugin-publish").version("0.0.12")
}

group = "cn.bestwu.gradle"
version = "0.0.1"



kotlin {
    copyClassesToJavaOutput=true
}

repositories {
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.4-3")
}