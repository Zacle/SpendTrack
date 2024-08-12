plugins {
    alias(libs.plugins.spendtrack.android.feature)
    alias(libs.plugins.spendtrack.android.library.compose)
}

android {
    namespace = "com.zacle.spendtrack.feature.register"
}

dependencies {

    implementation(projects.core.domain)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}