import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
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
            isShrinkResources = true
            isUseProguard = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isUseProguard = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation("com.google.firebase:firebase-firestore:18.0.1")

    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha02")
    implementation("androidx.viewpager2:viewpager2:1.0.0-alpha01")
}

apply {
    plugin("com.google.gms.google-services")
}