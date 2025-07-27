plugins {
//    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.kotlin.kapt)
    id("com.borisphen.memoryshot.android.library")
}

android {
    namespace = "com.borisphen.memoryshot.history.presentation"
}

dependencies {
    implementation(project(":util"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

//    ksp(libs.moshi.kotlin.codegen)
//    ksp(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}