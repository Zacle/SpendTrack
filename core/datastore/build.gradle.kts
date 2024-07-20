import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.spendtrack.android.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.zacle.spendtrack.core.datastore"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    androidComponents {
        onVariants(selector().all()) { variant ->
            afterEvaluate {
                project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                    val buildConfigTask = project.tasks.getByName("generate${variant.name.capitalized()}Proto")
                            as com.google.protobuf.gradle.GenerateProtoTask
                    dependsOn(buildConfigTask)
                    (this as AbstractKotlinCompileTool<*>).setSource(buildConfigTask.outputBaseDir)
                }
            }
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    api(libs.androidx.dataStore)
    api(libs.androidx.dataStore.core)
    api(libs.protobuf.kotlin.lite)

    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}