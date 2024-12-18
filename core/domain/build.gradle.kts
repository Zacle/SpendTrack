plugins {
    alias(libs.plugins.spendtrack.android.library)
}

android {
    namespace = "com.zacle.spendtrack.core.domain"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.testing)
    testImplementation(libs.mockito.kotlin)
}