plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.rajatt7z.retailx"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rajatt7z.ReTailX"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.osmdroid.android)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.coil)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}