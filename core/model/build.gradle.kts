plugins {
    alias(libs.plugins.spendtrack.android.library)
}

android {
    namespace = "com.zacle.spendtrack.core.model"
}

dependencies {
    api(projects.core.sharedResources)

    api(libs.kotlinx.datetime)
}