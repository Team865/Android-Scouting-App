plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "ca.warp7.android.scouting"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "2020.1.0"
        resConfigs("en", "hdpi")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:preference-v7:28.0.0")
    implementation("com.google.zxing:core:3.4.0")
}
