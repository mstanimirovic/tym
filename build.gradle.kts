import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
    id("com.gradleup.shadow") version "9.3.1"
}

application {
    mainClass.set("org.stanimirovic.tym.Main")
}

val lwjglVersion = "3.4.1"
val jomlVersion = "1.10.8"

val os = System.getProperty("os.name").lowercase()
val arch = System.getProperty("os.arch").lowercase()

val lwjglNatives = when {
    os.contains("mac") && (arch.contains("aarch64") || arch.contains("arm")) -> "natives-macos-arm64"
    os.contains("mac") -> "natives-macos"
    os.contains("win") -> "natives-windows"
    else -> "natives-linux"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

val appName = "TYM"
val appVersion = "1.0.0"

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    mergeServiceFiles()
}

tasks.named("build") {
    dependsOn("shadowJar")
}

tasks.named<JavaExec>("run") {
    if (OperatingSystem.current().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

fun jpackageTypeForCurrentOs(): String = when {
    OperatingSystem.current().isMacOsX -> "dmg"
    OperatingSystem.current().isWindows -> "msi"
    else -> error("Unsupported OS for installer packaging in this task")
}

tasks.register<Exec>("packagePortable") {
    dependsOn("shadowJar")

    val os = OperatingSystem.current()

    val shadow = tasks.named<ShadowJar>("shadowJar").get()
    val jarFile = shadow.archiveFile.get().asFile
    val outDir = layout.buildDirectory.dir("jpackage/portable").get().asFile

    doFirst { outDir.mkdirs() }

    val cmd = mutableListOf(
        "jpackage",
        "--type", "app-image",
        "--name", appName,
        "--app-version", appVersion,
        "--input", jarFile.parentFile.absolutePath,
        "--main-jar", jarFile.name,
        "--main-class", application.mainClass.get(),
        "--dest", outDir.absolutePath
    )

    if (os.isMacOsX) {
        cmd.addAll(listOf("--java-options", "-XstartOnFirstThread"))
        // cmd.addAll(listOf("--icon", "src/packaging/icon.icns"))
    }

    if (os.isWindows) {
        // cmd.addAll(listOf("--icon", "src/packaging/icon.ico"))
    }

    commandLine(cmd)
}

tasks.register<Exec>("packageInstaller") {
    dependsOn("shadowJar")

    val type = jpackageTypeForCurrentOs()
    val shadow = tasks.named<ShadowJar>("shadowJar").get()
    val jarFile = shadow.archiveFile.get().asFile
    val outDir = layout.buildDirectory.dir("jpackage/installer").get().asFile

    doFirst { outDir.mkdirs() }

    val cmd = mutableListOf(
        "jpackage",
        "--type", type,
        "--name", appName,
        "--app-version", appVersion,
        "--input", jarFile.parentFile.absolutePath,
        "--main-jar", jarFile.name,
        "--main-class", application.mainClass.get(),
        "--dest", outDir.absolutePath
    )

    // macOS: GLFW first-thread
    if (OperatingSystem.current().isMacOsX) {
        cmd.addAll(listOf("--java-options", "-XstartOnFirstThread"))
        // cmd.addAll(listOf("--icon", "src/main/java/resources/icon.icns"))
    }

    if (OperatingSystem.current().isWindows) {
        // cmd.addAll(listOf("--icon", "src/main/java/resources/icon.ico"))
    }

    commandLine(cmd)
}