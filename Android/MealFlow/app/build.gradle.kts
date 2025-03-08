plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0" // تأكد من أن لديك نفس إصدار Kotlin
}

android {
    namespace = "com.example.mealflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mealflow"
        minSdk = 24
        targetSdk = 35
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.runtime.android)//new
    implementation(libs.androidx.navigation.compose)//new
    // Retrofit
    implementation(libs.retrofit)
    // Retrofit with Scalar Converter
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.lifecycle.viewmodel) // لاستخدام ViewModel العادي
    implementation(libs.lifecycle.viewmodel.compose) // لاستخدام ViewModel مع Jetpack Compose
    implementation(libs.gson) // مكتبة Gson
    implementation(libs.retrofit) // Retrofit
    implementation(libs.retrofit.gson) // Gson Converter لـ Retrofit
    implementation(libs.coroutines.android) // دعم Coroutines في Android
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio) // استخدم CIO كـ HTTP Client
    implementation(libs.ktor.client.serialization) // دعم JSON
    implementation(libs.ktor.serialization.kotlinx.json) // Kotlinx JSON
    implementation(libs.ktor.client.logging) // لتسجيل الطلبات (اختياري)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.slf4j)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.runtime.livedata)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}