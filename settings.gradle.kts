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
        maven {
            url = uri("https://repo.maven.apache.org/maven2")
        }
        maven("https://maven.aliyun.com/repository/jcenter")


    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = {"https://jitpack.io"})
        google()
        mavenCentral()
        maven {
            url = uri("https://repo.maven.apache.org/maven2")
        }
        maven("https://maven.aliyun.com/repository/jcenter")



    }
}



rootProject.name = "oneChat"
include(":app")


