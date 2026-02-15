/**
 * NOTE: This is entirely optional and basics can be done in `settings.gradle.kts`
 */

plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    // Allow resolution from local Maven as well as Maven Central
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Depend on the stripped server jar installed to mavenLocal (provided scope)
    compileOnly("com.hypixel.hytale:HytaleServer-stripped:1.0-SNAPSHOT")
    // Common runtime dependencies you may need
    implementation("com.google.code.gson:gson:2.10.1")
}