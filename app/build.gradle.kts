plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "ddwu.com.mobile.petmap"
    compileSdk = 36

    defaultConfig {
        applicationId = "ddwu.com.mobile.petmap"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    viewBinding.enable=true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:${room_version}")
// Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${room_version}")
    ksp("androidx.room:room-compiler:${room_version}")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Google play service 위치 관련 정보 추가
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // GoogleMap 관련 정보 추가
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // 1. Google Places SDK (필수: 장소 검색 및 상세정보 추출용)
    implementation("com.google.android.libraries.places:places:4.1.0")

    // 2. Glide (권장: 병원 사진을 이미지뷰에 로드하기 위함)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 3. RecyclerView (이미 주석 처리되어 있는데, 리스트를 보이려면 주석 해제 필요)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // implementation(libs.androidx.recyclerview)

}