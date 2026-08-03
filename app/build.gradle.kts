plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.annapurna"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.annapurna"
        minSdk = 24
        targetSdk = 37
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
}

dependencies {

    // UI & Layout Libraries
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-livedata:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.11.0")

    // Room Database for local offline storage
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")

    // Media3 ExoPlayer for video streaming and local video playback
    implementation("androidx.media3:media3-exoplayer:1.10.1")
    implementation("androidx.media3:media3-ui:1.10.1")

    // Glide for thumbnails and images
    implementation("com.github.bumptech.glide:glide:5.0.9")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.9")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}