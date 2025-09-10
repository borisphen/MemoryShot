plugins {
    alias(libs.plugins.memoryshot.android.library)
    alias(libs.plugins.memoryshot.compose)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.borisphen.memoryshot.voice.presentation"
}

dependencies {

    implementation(project(":feature:feature-voice:domain"))
    implementation(project(":utils:util"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-domain"))

    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}