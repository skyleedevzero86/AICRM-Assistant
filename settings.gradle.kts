pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven(url = uri("https://repo.spring.io/milestone"))
    }
}

rootProject.name = "aicrm-assistant"

include(
    "backend:app",
    "backend:core-api-service",
    "backend:chat-ai-service",
)
