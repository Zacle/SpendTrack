pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SpendTrack"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:model")
include(":core:datastore")
include(":core:common")
include(":core:domain")
include(":core:testing")
include(":core:data")
include(":core:di")
include(":core:designsystem")
include(":feature:onboarding")
include(":core:ui")
include(":feature:home")
include(":feature:transaction")
include(":feature:budget")
include(":feature:profile")
include(":core:firebase")
include(":core:database")
include(":core:google")
include(":feature:login")
include(":feature:register")
include(":feature:forgot-password")
include(":feature:verify-auth")
include(":feature:expense")
include(":feature:income")
include(":core:shared-resources")
include(":feature:report")
