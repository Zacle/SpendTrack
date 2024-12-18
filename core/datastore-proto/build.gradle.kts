plugins {
    alias(libs.plugins.spendtrack.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.zacle.spendtrack.core.datastore_proto"
}

dependencies {
    api(libs.protobuf.kotlin.lite)
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