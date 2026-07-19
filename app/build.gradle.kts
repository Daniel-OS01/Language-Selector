plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt)
    alias(libs.plugins.com.mikepenz.aboutlibraries)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.com.google.devtools.ksp)
}

val compileSdkParts = providers.gradleProperty("android.compileSdk").get().split('.', limit = 2)

fun resolveVersionCode(): Int {
    val raw = System.getenv("CI_VERSION_CODE") ?: return 5
    val parsed = raw.toIntOrNull()
        ?: error("CI_VERSION_CODE must be an integer from 1 to 2100000000")
    require(parsed in 1..2_100_000_000) {
        "CI_VERSION_CODE must be an integer from 1 to 2100000000"
    }
    return parsed
}

android {
    namespace = "vegabobo.languageselector"
    compileSdk {
        version = release(compileSdkParts[0].toInt()) {
            minorApiLevel = compileSdkParts[1].toInt()
        }
    }

    defaultConfig {
        applicationId = "vegabobo.languageselector"
        minSdk = 33
        targetSdk = 37
        versionCode = resolveVersionCode()
        versionName = "1.04"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val releaseKeystore = System.getenv("RELEASE_SIGNING_KEYSTORE")
        if (!releaseKeystore.isNullOrEmpty()) {
            create("ciRelease") {
                storeFile = file(releaseKeystore)
                storePassword = System.getenv("RELEASE_SIGNING_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfigs.findByName("ciRelease")?.let {
                signingConfig = it
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        buildConfig = true
        compose = true
        aidl = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    testImplementation(libs.junit)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.libsu.core)
    implementation(libs.libsu.service)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material3)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.aboutlibraries.core)

    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    implementation(libs.hiddenapibypass)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    compileOnly(project(":hidden_api"))
}
