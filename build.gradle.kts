import java.lang.reflect.Modifier
import java.net.URLClassLoader
import java.util.jar.JarFile

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

tasks.register("generateBlockStatesReport") {
    group = "documentation"
    description = "Exports org.bukkit.block.BlockState implementations and their methods to a txt file."

    // Not compatible with the configuration cache because it uses reflection/classloading.
    notCompatibleWithConfigurationCache("This task loads JARs using reflection, which is not supported by the configuration cache.")

    // Can be overridden with -PblockStatesOutputDir=/your/directory
    val outputDir = file((findProperty("blockStatesOutputDir") as String?)
        ?: "${project.projectDir}/reports/blockstates")

    // Resolve eagerly during configuration and pass the resulting File Set to doLast.
    val classpathFiles = configurations.compileClasspath.get().files

    inputs.files(classpathFiles)
    outputs.dir(outputDir)

    doLast {
        println("Task JVM: ${System.getProperty("java.version")}")
        println("Task JVM home: ${System.getProperty("java.home")}")

        val paperJar = classpathFiles.firstOrNull { it.name.contains("paper-api") }
            ?: throw GradleException("paper-api JAR could not be found on the compileClasspath!")

        // Extract the version from the JAR file name (e.g. paper-api-1.20.1-R0.1-SNAPSHOT.jar)
        val paperVersion = Regex("""paper-api-(.+)\.jar""").find(paperJar.name)
            ?.groupValues?.get(1) ?: "unknown"

        val urls = classpathFiles.map { it.toURI().toURL() }.toTypedArray()
        URLClassLoader(urls, javaClass.classLoader).use { loader ->
            val blockStateClass = loader.loadClass("org.bukkit.block.BlockState")

            data class Entry(val name: String, val interfaces: List<String>, val methods: List<String>)
            val results = mutableListOf<Entry>()

            JarFile(paperJar).use { jar ->
                jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") && !it.name.startsWith("META-INF") }
                    .forEach { entry ->
                        val className = entry.name.removeSuffix(".class").replace('/', '.')
                        try {
                            val clazz = loader.loadClass(className)
                            if (clazz == blockStateClass) return@forEach
                            if (!blockStateClass.isAssignableFrom(clazz)) return@forEach

                            val interfaces = clazz.interfaces.map { it.simpleName }.sorted()

                            val methods = clazz.declaredMethods
                                .filter { Modifier.isPublic(it.modifiers) && !it.isSynthetic && !it.isBridge }
                                .sortedBy { it.name }
                                .map { m ->
                                    val params = m.parameterTypes.joinToString(", ") { p -> p.simpleName }
                                    val deprecated = if (m.isAnnotationPresent(Deprecated::class.java)) " [DEPRECATED]" else ""
                                    "${m.returnType.simpleName} ${m.name}($params)$deprecated"
                                }

                            results += Entry(clazz.simpleName, interfaces, methods)
                        } catch (_: Throwable) {
                            // Silently skip classes whose dependencies could not be loaded.
                        }
                    }
            }

            results.sortBy { it.name }

            outputDir.mkdirs()
            val outFile = File(outputDir, "block_states.txt")
            outFile.bufferedWriter().use { w ->
                w.write("# paper-api version: $paperVersion\n\n")
                for (e in results) {
                    w.write(e.name)
                    if (e.interfaces.isNotEmpty()) {
                        w.write(" implements ${e.interfaces.joinToString(", ")}")
                    }
                    w.write(":\n")
                    if (e.methods.isEmpty()) {
                        w.write("  (no declared methods)\n")
                    } else {
                        e.methods.forEach { w.write("  * $it\n") }
                    }
                    w.write("\n")
                }
            }
            logger.lifecycle("Block states report written to: ${outFile.absolutePath}")
        }
    }
}

tasks.build {
    dependsOn("generateBlockStatesReport")
}
