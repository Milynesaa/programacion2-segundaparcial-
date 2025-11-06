plugins {
    id("com.android.application")
    // NO incluir KSP en proyectos Java puros
}

android {
    namespace = "com.example.gestionclientes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gestionclientes"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val room_version = "2.6.1"
    val work_version = "2.9.0"
    val retrofit_version = "2.9.0"

    // AndroidX Core
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Room (Persistencia local - Requerimiento 3) - PARA JAVA
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version") // ← annotationProcessor para Java

    // WorkManager (Tareas programadas - Requerimiento 4)
    implementation("androidx.work:work-runtime:$work_version")

    // Retrofit (Peticiones HTTP - Todos los requerimientos)
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson (Serialización JSON)
    implementation("com.google.code.gson:gson:2.10.1")

    // Compresión ZIP (Requerimiento 2)
    implementation("net.lingala.zip4j:zip4j:2.11.5")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}