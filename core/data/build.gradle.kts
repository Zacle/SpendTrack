plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.zacle.spendtrack.core.data"
}

dependencies {
    api(projects.core.datastore)
    api(projects.core.domain)
    api(projects.core.model)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.testing)
    testImplementation(libs.mockito.kotlin)
}