plugins {
    alias(libs.plugins.memoryshot.android.library)
    alias(libs.plugins.memoryshot.compose)
}

android {
    namespace = "com.borisphen.memoryshot.util.ui"
}

dependencies {

    implementation(project(":core:core-domain"))

    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}