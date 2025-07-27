plugins {
    id("com.borisphen.memoryshot.android.library")
    id("com.borisphen.memoryshot.compose")
//    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.borisphen.memoryshot.voice.presentation"
}

dependencies {

    implementation(project(":feature:feature-voice:domain"))
    implementation(project(":util"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.foundation.layout.android)

    implementation(libs.dagger)
//    kapt(libs.dagger.compiler)
    ksp(libs.dagger.compiler)

    implementation(libs.androidx.activity.compose)
    implementation(libs.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}