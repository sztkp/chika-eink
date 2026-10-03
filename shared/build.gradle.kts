plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

// Platform-independent core: panel model, reading-order + merge/divide planning, camera math, and
// the archive abstraction. No Android (or any platform) APIs may be added to commonMain — platform
// integrations (TFLite/LiteRT, bitmap decoding, archive backends) live behind these interfaces in
// the Android app. The core builds for JVM only.
kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
