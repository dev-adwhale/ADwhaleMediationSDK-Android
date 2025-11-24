plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "kr.co.adwhale.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "kr.co.adwhale.sample"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // --- 기존 View 기반 라이브러리 (AdWhale SDK가 의존 가능성 있음) ---
//    implementation("androidx.appcompat:appcompat:1.3.1")
//    implementation("com.google.android.material:material:1.4.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- AdWhale Mediation sdk ---
    implementation("net.adwhale.sdk.mediation:adwhale-mediation-sdk:2.7.2")

    // cauly adapter sdk
    implementation("net.adwhale.sdk.cauly.adapter:cauly-sdk:3.5.41.0")

    // admize adapter sdk
    implementation("net.adwhale.sdk.admize.adapter:admize-sdk:1.0.8.0")

    // adfit adapter sdk
    implementation("net.adwhale.sdk.adfit.adapter:adfit-sdk:3.17.2.5")

    // admob adapter sdk
    implementation("net.adwhale.sdk.admob.adapter:admob-sdk:24.3.0.2")

    // levelplay adapter sdk
    implementation("net.adwhale.sdk.levelplay.adapter:levelplay-sdk:8.7.0.6")

    // --- AdMob Ad Inspector용 ---
    implementation("com.google.android.gms:play-services-ads:24.3.0")

    // --- Lifecycle Process (ProcessLifecycleOwner 용) ---
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.2")
}