import com.zacle.build_logic.convention.ext.androidTestImplementation

plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
    alias(libs.plugins.spendtrack.android.room)
}

android {
    namespace = "com.zacle.spendtrack.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.zacle.spendtrack.core.testing.SpendTrackTestRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.truth)
}