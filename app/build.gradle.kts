import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.chheese.app.HeadphoneToolbox"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode(27)
        versionName = "4.2.1_Cobalt"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions.useIR = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.lifecycle:lifecycle-service:2.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.gyf.immersionbar:immersionbar:3.0.0")
    implementation("com.gyf.immersionbar:immersionbar-ktx:3.0.0")
    // 用于抓取酷安的网页，分析有没有更新
    implementation("org.jsoup:jsoup:1.13.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.0.0-beta02")
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta02")
    implementation("androidx.compose.foundation:foundation:1.0.0-beta02")
    implementation("androidx.compose.material:material:1.0.0-beta02")
    implementation("androidx.compose.material:material-icons-core:1.0.0-beta02")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta01")
    implementation("androidx.activity:activity-compose:1.3.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta01")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-beta01")
}