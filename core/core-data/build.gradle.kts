plugins {
    alias(libs.plugins.memoryshot.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.borisphen.core.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    implementation(project(":utils:util"))
    implementation(project(":utils:util-platform"))
    implementation(project(":core:core-domain"))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)

    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.retrofit)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.text.recognition)
    implementation(libs.tess.text.recognition)
    implementation(libs.language.id)
    // Обязательно добавьте Firebase BoM
    implementation(platform(libs.firebase.bom))


    implementation(libs.language.id.common)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
