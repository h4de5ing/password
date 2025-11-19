plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "x.x.p455w0rd"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "x.x.p455w0rd"
        minSdk = 26
        targetSdk = 36
        versionCode = 20
        versionName = "2.0"
        multiDexEnabled = true
    }
    signingConfigs {
        create("release") {
            storeFile = file("../hello_android.jks")
            keyAlias = "android"
            keyPassword = "android"
            storePassword = "android"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.8")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.greengerong:opencsv.utils:1.2")
    implementation("com.github.getActivity:XXPermissions:16.8")
    implementation("com.github.h4de5ing.filepicker:filepicker:1.0-20230411")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material.icons.extended)

    implementation(libs.androidx.activity)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
}