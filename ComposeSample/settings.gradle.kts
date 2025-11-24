pluginManagement {
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

        // Adwhale Mediation
        maven(url = "https://dev-adwhale.github.io/adwhale-sdk-android-maven/maven-repo") {
        }
        // Cauly SDK Repository
        maven(url = "https://cauly.github.io/admize-sdk-android-maven/maven-repo") {
        }
        // Admize SDK Repository
        maven(url = "https://cauly.github.io/cauly-sdk-android-maven/maven-repo") {
        }
        // Kakao
        maven(url = "https://devrepo.kakao.com/nexus/content/groups/public/") {
        }
    }
}

rootProject.name = "ADWHALE-MEDIATION-ANDROID-SAMPLE"
include(":app")
 