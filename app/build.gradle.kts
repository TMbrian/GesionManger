plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.gestionmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gestionmanager"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    // Elimina los archivos duplicados de META-INF
    packaging {
        resources {
            // Añade estas exclusiones
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

configurations.all {
    resolutionStrategy {
        // Fuerza versiones compatibles
        force("com.google.dagger:dagger:2.52")
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    }
}

dependencies {
    // ================================
    // ANDROIDX CORE
    // ================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ================================
    // MATERIAL DESIGN
    // ================================
    implementation(libs.material)

    // ================================
    // LIFECYCLE & VIEWMODEL
    // ================================
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ================================
    // COROUTINES
    // ================================
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ================================
    // COMPOSE
    // ================================
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)

    // ================================
    // NAVIGATION
    // ================================
    implementation(libs.androidx.navigation.compose)

    // ================================
    // DEPENDENCY INJECTION (HILT)
    // ================================
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)


    // ================================
    // NETWORK (RETROFIT & OKHTTP)
    // ================================
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.okhttp3)
    implementation(libs.squareup.okhttp3.logging.interceptor)

    // ================================
    // DATABASE (ROOM)
    // ================================
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ================================
    // GOOGLE SERVICES
    // ================================
    implementation(libs.play.services.auth)

    // ================================
    // IMAGE LOADING
    // ================================
    implementation(libs.coil.compose)

    // ================================
    // TESTING
    // ================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.squareup.javapoet)
}