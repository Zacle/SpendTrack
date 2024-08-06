plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.spendtrack.android.application)
    alias(libs.plugins.spendtrack.android.application.compose)
    alias(libs.plugins.spendtrack.android.hilt)
    alias(libs.plugins.spendtrack.android.firebase)
}

android {
    namespace = "com.zacle.spendtrack"

    defaultConfig {
        applicationId = "com.zacle.spendtrack"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.zacle.spendtrack.core.testing.SpendTrackTestRunner"
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

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.di)
    implementation(projects.core.domain)
    implementation(projects.core.firebase)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.feature.onboarding)
    implementation(projects.feature.home)
    implementation(projects.feature.transaction)
    implementation(projects.feature.budget)
    implementation(projects.feature.profile)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.datetime)
    implementation(libs.timber)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.datastore)
}