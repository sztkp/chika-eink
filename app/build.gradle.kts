plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Keep distributed notices in sync with the repository's authoritative texts.
val legalAssets = layout.buildDirectory.dir("generated/legalAssets")
val syncLegalAssets by tasks.registering(Sync::class) {
    from(rootProject.projectDir) {
        include("LICENSE", "THIRD_PARTY_NOTICES.md", "THIRD_PARTY_LICENSES/**")
        include("docs/LICENSING.md", "docs/ARTWORK.md")
        into("legal")
    }
    into(legalAssets)
}

android {
    namespace = "com.chakra.comicreader"
    compileSdk = 36
    buildToolsVersion = "36.1.0"

    defaultConfig {
        applicationId = "com.chakra.comicreader"
        minSdk = 26
        targetSdk = 36
        // versionName comes from the git tag in CI (VERSION_NAME), else this default.
        // versionCode is derived deterministically from MAJOR*10000 + MINOR*100 + PATCH so it is
        // reproducible and monotonically increasing across all channels (Play, F-Droid, sideload)
        // — F-Droid builds from source and requires a stable, increasing versionCode. A pre-release
        // suffix is ignored per component (e.g. "0.2.1-beta" → 0.2.1 → 20100... → 201).
        val appVersionName = System.getenv("VERSION_NAME") ?: "0.2.1"
        versionName = appVersionName
        versionCode = appVersionName.split('.').map { part ->
            part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
        }.let { p ->
            (p.getOrElse(0) { 0 } * 10000) + (p.getOrElse(1) { 0 } * 100) + p.getOrElse(2) { 0 }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    // Release signing is configured only when a keystore is supplied via environment variables
    // (the CI release workflow decodes it from repo secrets). Local builds without these vars are
    // unaffected — debug builds use the default debug key, and `assembleRelease` stays unsigned.
    val releaseKeystore = System.getenv("KEYSTORE_FILE")?.let { file(it) }
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // R8 strips the large amount of dead code in the dex (notably the unused
            // material-icons-extended set); resource shrinking drops unreferenced resources.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    sourceSets.getByName("main").assets.srcDir(legalAssets.get().asFile)

    // Keep the TFLite model uncompressed so it can be memory-mapped by the interpreter.
    androidResources {
        noCompress += "tflite"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        // Compress the native libraries inside the APK (they ship uncompressed by default). This
        // keeps every ABI but shrinks the download — the libs are extracted at install time.
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

tasks.named("preBuild") {
    dependsOn(syncLegalAssets)
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.documentfile)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.commons.compress)
    implementation(libs.sevenzip)

    implementation(libs.tensorflow.lite)
}
