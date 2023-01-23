import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("idea")
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "kr.abins.corpse"


repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        name = "sk89q-repo"
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "everything"
        url = uri("https://repo.citizensnpcs.co/")
    }
    maven("https://oss.sonatype.org/content/repositories/central")
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.19.3-R0.1-SNAPSHOT") {
        exclude("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    }
    compileOnly(group = "com.comphenix.protocol", name = "ProtocolLib", version = "5.0.0-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldguard", name = "worldguard-bukkit", version = "7.0.6-SNAPSHOT")
    compileOnly(group = "net.citizensnpcs", name = "citizens-main", version = "2.0.30-SNAPSHOT") {
        exclude("*", "*")
    }
}

fun TaskContainer.createJar(name: String, configuration: ShadowJar.() -> Unit) {
    create<ShadowJar>(name) {
        archiveBaseName.set(project.name)
        archiveVersion.set("") // For bukkit plugin update
        from(sourceSets["main"].output)
        configurations = listOf(project.configurations.shadow.get().apply { isCanBeResolved = true })
        configuration()
    }
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }


    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }
    createJar("outJar") {
        var dest = File("C:/Users/a0103/바탕 화면/모음지이이입/버킷 모음지이입/TheOutpost/plugins")
        val pluginName = archiveFileName.get()
        val pluginFile = File(dest, pluginName)
        if (pluginFile.exists()) dest = File(dest, "update")
        doLast {
            copy {
                from(archiveFile)
                into(dest)
            }
        }
    }


}