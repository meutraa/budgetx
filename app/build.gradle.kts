import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
//    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.github.ben-manes.versions") version "0.20.0"
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "host.lost.budgetx"
        minSdkVersion(28)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "0.1"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

    implementation("com.google.firebase:firebase-core:16.0.7")
    implementation("com.google.firebase:firebase-firestore:18.0.0")

    implementation("com.firebaseui:firebase-ui-firestore:4.3.1")


    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha02")

    implementation("org.jetbrains.anko:anko-commons:0.10.8")
    implementation("org.jetbrains.anko:anko-sdk25:0.10.8")
    implementation("org.jetbrains.anko:anko-constraint-layout:0.10.8")
    implementation("org.jetbrains.anko:anko-design:0.10.8")
//    implementation("org.jetbrains.anko:anko-cardview-v7:0.10.8")
    implementation("org.jetbrains.anko:anko-recyclerview-v7:0.10.8")
}

apply {
    plugin("com.google.gms.google-services")
}