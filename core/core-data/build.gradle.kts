plugins {
//    alias(libs.plugins.android.library)
    id("com.borisphen.memoryshot.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.borisphen.core.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    implementation(project(":util"))
    implementation(project(":core:core-domain"))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.retrofit)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    implementation(libs.dagger)
    ksp(libs.moshi.kotlin.codegen)
//    ksp(libs.dagger.compiler)
    ksp(libs.dagger.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}