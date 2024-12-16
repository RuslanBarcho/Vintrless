import ir.mahozad.manifest.ManifestMode
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)

    id("io.realm.kotlin") version "2.3.0"
    id("ir.mahozad.compose-exe-manifest") version "1.0.0"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ZXing
            implementation(libs.core)

            // Camera
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.lifecycle)

            // V2Ray
            implementation(files("libs/libv2ray.aar"))

            // Preferences
            implementation(libs.androidx.preference.ktx)
        }
        commonMain.dependencies {
            // Common
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // ViewModel
            implementation(libs.lifecycle.viewmodel.compose)

            // Navigation
            implementation(libs.navigation.compose)

            // Koin
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Key-value storage
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.datastore)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences.core)

            // Logging
            api(libs.logging)

            // Uri for multiplatform
            implementation(libs.uri.kmp)

            // QR for multiplatform
            implementation(libs.qr.kit)

            // Realm
            implementation(libs.library.base)

            // File picker
            implementation(libs.filekit.core)

            // Date time
            implementation(libs.kotlinx.datetime)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

android {
    namespace = "pw.vintr.vintrless"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "pw.vintr.vintrless"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        splits {
            abi {
                isEnable = true
                include(
                    "arm64-v8a",
                    "armeabi-v7a",
                    "x86_64",
                    "x86"
                )
                isUniversalApk = true
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
    applicationVariants.all {
        val variant = this
        val versionCodes = mapOf(
            "armeabi-v7a" to 1,
            "arm64-v8a" to 1,
            "x86" to 1,
            "x86_64" to 1,
            "universal" to 1
        )

        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null) {
                    output.getFilter("ABI")
                } else {
                    "universal"
                }

                output.outputFileName = "Vintrless_${variant.versionName}_${abi}.apk"
                if (versionCodes.containsKey(abi)) {
                    output.versionCodeOverride =
                        (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                } else {
                    return@forEach
                }
            }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

compose.desktop {
    composeExeManifest {
        enabled = true
        manifestMode = ManifestMode.EMBED
        manifestFile = file("app.manifest")
    }

    application {
        mainClass = "pw.vintr.vintrless.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Vintrless"
            packageVersion = "1.0.0"
            vendor = "Vintrapps"
            modules("jdk.unsupported")

            appResourcesRootDir = rootDir.resolve("desktopLibs")

            jvmArgs(
                "-Dapple.awt.application.appearance=system"
            )
        }

        buildTypes.release.proguard {
            version.set("7.4.0")
            configurationFiles.from("proguard.pro")
        }
    }
}
