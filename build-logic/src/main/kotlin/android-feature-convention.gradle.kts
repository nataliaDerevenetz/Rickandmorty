import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.findLibrary("coil-core").get())
    implementation(libs.findLibrary("coil-network-okhttp").get())

    implementation(libs.findLibrary("hilt-android").get())
    "ksp"(libs.findLibrary("hilt-android-compiler").get())

    implementation(libs.findLibrary("androidx-navigation-fragment-ktx").get())
    implementation(libs.findLibrary("androidx-navigation-ui-ktx").get())

    implementation(libs.findLibrary("androidx-paging-runtime").get())
    implementation(libs.findLibrary("androidx-swiperefreshlayout").get())

    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("androidx-appcompat").get())
    implementation(libs.findLibrary("material").get())

    testImplementation(libs.findLibrary("junit").get())
    androidTestImplementation(libs.findLibrary("androidx-junit").get())
    androidTestImplementation(libs.findLibrary("androidx-espresso-core").get())

    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))
    implementation(project(":core:models"))
}

fun DependencyHandler.implementation(dependency: Any) = add("implementation", dependency)
fun DependencyHandler.testImplementation(dependency: Any) = add("testImplementation", dependency)
fun DependencyHandler.androidTestImplementation(dependency: Any) = add("androidTestImplementation", dependency)


