import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)

}

android {
    namespace = "com.zacle.spendtrack.core.google"
    compileSdk = 34

    defaultConfig {
        proguardFiles("proguard-rules.pro")

        val keystoreFile = project.rootProject.file("secrets.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        val webClientId = properties.getProperty("WEB_CLIENT_ID") ?: ""
        buildConfigField(
            type = "String",
            name = "WEB_CLIENT_ID",
            value = webClientId
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.identity.googleid)
}