plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.pharmacistassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pharmacistassistant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Use the latest version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Google ML Kit Barcode Scanning library
    implementation(libs.barcode.scanning)

    // CameraX libraries
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.camera.view)

    // Core library
    implementation(libs.androidx.core.ktx.v1131)

    // AppCompat library
    implementation(libs.androidx.appcompat)

    // ConstraintLayout library
    implementation(libs.androidx.constraintlayout)

    // Lifecycle libraries
    implementation(libs.androidx.lifecycle.runtime.ktx.v284)

    // Activity Compose
    implementation(libs.androidx.activity.compose.v191)

    // Jetpack Compose libraries
    implementation(platform(libs.androidx.compose.bom.v20240800))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.poi.ooxml)
    implementation(libs.poi)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.runtime.livedata)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material3)
    implementation (libs.androidx.material3.window.size)
    implementation (libs.androidx.material3.adaptive.navigation.suite)
    implementation (libs.androidx.compose.ui.ui)
    implementation (libs.androidx.compose.ui.ui.tooling)
    implementation (libs.ui.graphics)
    implementation(libs.androidx.material)
    implementation(libs.zxing.android.embedded)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.sqlite.ktx)
    implementation(libs.material)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240800))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
