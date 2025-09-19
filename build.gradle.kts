import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "org.pilov"
version = "0.1.1"

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() } // важно для артефактов IDE и плагинов
}

dependencies {
    intellijPlatform {
        // Задаём целевую платформу: IntelliJ IDEA Community 2024.1.7
        intellijIdeaCommunity("2025.2.1")

        // Примеры зависимостей на плагины:
        // bundledPlugin("com.intellij.java")
        // plugin("org.intellij.scala", "2024.1.4")
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

intellijPlatform {
    // since/until — теперь внутри pluginConfiguration
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("241")
            untilBuild.set("252.*")
        }
        // при необходимости: id/name/description и т.п.
    }

    // подписывание/публикация — тоже тут
    signing {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }
}
