plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "in.urjakendra.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "in.urjakendra.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "URJA_API_BASE_URL", "\"http://10.0.2.2:8000/\"")
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
        getByName("release") {
            val releaseApiBaseUrl = providers.gradleProperty("URJA_API_BASE_URL").orElse("").get()
            buildConfigField("String", "URJA_API_BASE_URL", "\"$releaseApiBaseUrl\"")
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.03.00"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}


// Refuse release builds until a real HTTPS backend URL is explicitly configured.
val verifyReleaseConfig by tasks.registering {
    doLast {
        val baseUrl = providers.gradleProperty("URJA_API_BASE_URL").orNull.orEmpty()
        require(baseUrl.startsWith("https://")) {
            "Release build blocked: pass -PURJA_API_BASE_URL=https://your-real-api-host/"
        }
        require(!baseUrl.contains("10.0.2.2") && !baseUrl.contains("localhost") && !baseUrl.contains("127.0.0.1")) {
            "Release build blocked: URJA_API_BASE_URL must not point to a local development host."
        }
        require(!baseUrl.contains("your-real-api-host") && baseUrl.endsWith("/")) {
            "Release build blocked: provide a real HTTPS base URL ending in '/'."
        }
    }
}

tasks.configureEach {
    if (name == "preReleaseBuild") {
        dependsOn(verifyReleaseConfig)
    }
}

