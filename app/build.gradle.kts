plugins {
    id("com.android.application")
}

android {
    namespace = "com.stingach.dm.savethecube"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.stingach.dm.savethecube"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.gridlayout:gridlayout:1.1.0")
    val room_version = "2.6.1"

    //noinspection GradleDependency
    implementation("androidx.room:room-runtime:$room_version")
    //noinspection GradleDependency
    annotationProcessor("androidx.room:room-compiler:$room_version")

    //noinspection GradleDependency
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    //noinspection GradleDependency
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("com.facebook.stetho:stetho:1.6.0")
    implementation ("com.facebook.stetho:stetho-okhttp3:1.6.0") // если используешь OkHttp

    testImplementation("junit:junit:4.13.2")
    //noinspection GradleDependency
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    //noinspection GradleDependency
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


