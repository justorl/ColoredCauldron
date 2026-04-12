plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "com.pulse"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.tcoded.com/releases")
    maven("https://maven.playpro.com")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    compileOnly(kotlin("stdlib"))

    // utils
    implementation("com.github.justorl:UtilsLib:2.3.8")
    compileOnly("com.tcoded:FoliaLib:0.5.1")
    compileOnly("com.jeff-media:MorePersistentDataTypes:2.4.0")
    compileOnly("com.jeff-media:custom-block-data:2.2.4")

    // command api
    compileOnly("dev.jorel:commandapi-paper-shade:11.1.0")
    compileOnly("dev.jorel:commandapi-kotlin-paper:11.1.0")

    // apis
    compileOnly("net.coreprotect:coreprotect:23.1")
}

tasks {

    runServer {
        minecraftVersion("1.21.11")
    }

    shadowJar {
        archiveClassifier.set("")

        relocate("com.pulse.utilslib", "com.pulse.coloredcauldron.shadow.utilslib")

        dependencies {
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}