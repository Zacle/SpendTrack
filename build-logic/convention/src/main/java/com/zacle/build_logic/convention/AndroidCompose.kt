package com.zacle.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import com.zacle.build_logic.convention.ext.androidTestImplementation
import com.zacle.build_logic.convention.ext.debugImplementation
import com.zacle.build_logic.convention.ext.implementation
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            implementation(platform(bom))
            androidTestImplementation(platform(bom))
            implementation(libs.findLibrary("androidx-activity-compose").get())
            implementation(libs.findLibrary("androidx-compose-foundation").get())
            implementation(libs.findLibrary("androidx-compose-foundation-layout").get())
            implementation(libs.findLibrary("androidx-compose-material3").get())
            implementation(libs.findLibrary("androidx-compose-runtime").get())
            debugImplementation(libs.findLibrary("androidx-compose-ui-tooling").get())
            implementation(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            implementation(libs.findLibrary("androidx-compose-ui-util").get())
            androidTestImplementation(libs.findLibrary("androidx-compose-ui-test").get())
            androidTestImplementation(libs.findLibrary("androidx-compose-ui-test-junit4").get())
            debugImplementation(libs.findLibrary("androidx-compose-ui-testManifest").get())
            implementation(libs.findLibrary("androidx-navigation-compose").get())
            implementation(libs.findLibrary("timber").get())
        }

        testOptions {
            unitTests {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = flatMap {
            rootProject.layout.buildDirectory.dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")

        enableStrongSkippingMode = true
    }
}