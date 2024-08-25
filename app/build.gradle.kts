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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ZXing Barcode Scanning library
    implementation(libs.zxing.android.embedded.v430) // Use a stable version

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

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Apache POI for Excel processing
    implementation(libs.poi.ooxml)
    implementation(libs.poi)       // Apache POI for Excel files
    implementation(libs.play.services.analytics.impl) // Apache POI for OOXML files (XLSX)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240800))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}