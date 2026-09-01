plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.6.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.2")
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

configurations.all {
    if (isCanBeResolved) {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release.set(16)
    }

    shadowJar {
        relocate("com.fasterxml.jackson", "com.desoi.structra.jackson")
        relocate("org.bstats", "com.desoi.structra.bstats")
        archiveClassifier.set("")
        isZip64 = true
    }

    processResources {
        val props = mapOf("version" to project.version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
