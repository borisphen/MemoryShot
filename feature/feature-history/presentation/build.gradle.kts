plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.memoryshot.android.library)
    alias(libs.plugins.memoryshot.compose)
}

android {
    namespace = "com.borisphen.memoryshot.history.presentation"
}

dependencies {
    implementation(project(":utils:util"))
    implementation(project(":utils:util-ui"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.dagger)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    ksp(libs.dagger.compiler)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}