plugins {
    alias(libs.plugins.spendtrack.jvm.library)
    alias(libs.plugins.spendtrack.android.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}