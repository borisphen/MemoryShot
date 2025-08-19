plugins {
    id("com.borisphen.memoryshot.android.library")
}

android {
    namespace = "com.borisphen.memoryshot.util.platform"
}

dependencies {

    implementation(project(":core:core-domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}