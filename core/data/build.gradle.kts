plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.zacle.spendtrack.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.datastore)
    api(projects.core.domain)
    api(projects.core.model)
    api(projects.core.sharedResources)

    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.ext.work)
    implementation(libs.timber)

    testImplementation(projects.core.testing)
    testImplementation(libs.mockito.kotlin)
}