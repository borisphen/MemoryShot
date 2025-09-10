plugins {
    alias(libs.plugins.memoryshot.android.library)
    alias(libs.plugins.memoryshot.compose)
}

android {
    namespace = "com.borisphen.core.ui"
}

dependencies {

    implementation(project(":utils:util-platform"))
    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}