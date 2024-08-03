plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
    alias(libs.plugins.spendtrack.android.firebase)
}

android {
    namespace = "com.zacle.spendtrack.core.firebase"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.testing)
    testImplementation(libs.mockito.kotlin)
}