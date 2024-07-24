plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
}

android {
    namespace = "com.zacle.spendtrack.core.testing"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)
    api(libs.kotlinx.coroutines.test)

    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.datetime)
}