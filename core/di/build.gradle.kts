plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
}

android {
    namespace = "com.zacle.spendtrack.core.di"
}

dependencies {
    api(projects.core.common)
    api(projects.core.domain)

    implementation(libs.kotlinx.coroutines.core)
}