import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.BuildType

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

afterEvaluate {
    tasks {
        "assembleCompress" {
            dependsOn(clean)
        }
    }

    android.buildTypes.all {
        resValue(
            "string", "app_name", when (name) {
                "release" -> "EugeneTwink"
                else -> "EugeneTwink-$name"
            }
        )
    }
}

android {
    namespace = "ru.eugenehash.eugenetwink"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        resourceConfigurations.addAll(listOf("en", "ru"))
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".debug"
        }
        compress {
            isMinifyEnabled = true
            isShrinkResources = false
            applicationIdSuffix = ".compress"
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation("com.airbnb.android:lottie:6.1.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    }
}

fun compress(action: (ApplicationBuildType).() -> Unit): BuildType {
    return android.buildTypes.create("compress") {
        initWith(android.buildTypes.getByName("debug"))
        action(this)
    }
}