import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    `kotlin-dsl`
}
group = "com.example.rickandmortyexplorer.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidRoom") {
            id = libs.plugins.explorer.android.room.get().pluginId
            implementationClass = "com.example.rickandmortyexplorer.AndroidRoomConventionPlugin"
        }
    }
}