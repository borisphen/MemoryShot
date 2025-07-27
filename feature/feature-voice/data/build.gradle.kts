plugins {
//    alias(libs.plugins.android.library)
    id("com.borisphen.memoryshot.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.borisphen.memoryshot.voice.data"
}

dependencies {
    implementation(project(":feature:feature-voice:domain"))
    implementation(project(":util"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.retrofit)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

//    ksp(libs.moshi.kotlin.codegen)
//    ksp(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}