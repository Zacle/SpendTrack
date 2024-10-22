plugins {
    alias(libs.plugins.spendtrack.android.feature)
    alias(libs.plugins.spendtrack.android.library.compose)
}

android {
    namespace = "com.zacle.spendtrack.feature.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.sharedResources)

    implementation(libs.coil.kt.compose)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}