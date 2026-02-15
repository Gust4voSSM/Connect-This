rootProject.name = "dev.hytalemodding"

// The recommended approach is to install a "stripped" server jar to your
// local Maven repository and depend on it (see patcher README and docs).
// Avoid including the decompiled server as a composite build because the
// decompiled code contains artifacts that won't compile.

plugins {
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.+"
}

// Would you like to do a split project?
// Create a folder named "common", then configure details with `common { }`

hytale {
    usePatchline("release")
    useVersion("latest")

    repositories {
            // Allow Gradle to resolve artifacts from the local Maven repo as well
            mavenLocal()
            // Any external repositories besides: MavenCentral, HytaleMaven, and CurseMaven
    }

        dependencies {
            // Depend on the stripped server artifact installed to mavenLocal
            implementation("com.hypixel.hytale:HytaleServer-stripped:1.0-SNAPSHOT")
        }

    manifest {
        Group = "Gust4vo"
        Name = "ConnectThisPlugin"
        Main = "gust4vo.connectedblocks.Main"
    }
}