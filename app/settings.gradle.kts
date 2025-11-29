pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "My Application"
include(":app")

includeBuild("../cactus-kotlin") {
    dependencySubstitution {
        // This tells Gradle: "When you see a request for the internet version..."
        substitute(module("com.github.cactus-compute:cactus-kotlin"))
            // "...use the local library module instead."
            .using(project(":library"))
    }
}
