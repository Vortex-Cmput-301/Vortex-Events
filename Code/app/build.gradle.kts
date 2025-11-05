plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin

    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.vortex_events"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vortex_events"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.monitor)
    implementation(libs.core)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // For mocking the firebase in testing
    testImplementation(libs.mockito.core)


    // Import the Firebase BoM

    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))


    // TODO: Add the dependencies for Firebase products you want to use

    // When using the BoM, don't specify versions in Firebase dependencies

    implementation("com.google.firebase:firebase-analytics")


    // Add the dependencies for any other desired Firebase products
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-firestore")
    // https://firebase.google.com/docs/android/setup#available-libraries

}