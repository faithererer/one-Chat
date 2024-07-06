plugins {
    alias(libs.plugins.androidApplication)


}


android {
    namespace = "com.zjc.onechat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zjc.onechat"
        minSdk = 28
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



    packagingOptions {
        resources.excludes.add("META-INF/*")
    }


}


configurations.all{
    resolutionStrategy{
        force("com.google.android:flexbox:2.0.1")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.airbnb.android:lottie:6.4.1")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("jp.wasabeef:blurry:4.0.1")

    implementation("com.github.getActivity:EasyHttp:12.8")
    implementation("com.github.getActivity:Toaster:12.6")
    implementation("com.github.getActivity:GsonFactory:9.5")

    implementation("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    implementation("com.android.support:cardview-v7:28.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation("jp.wasabeef:glide-transformations:4.3.0")





    // room --start
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")



    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")

    // room --end

    // 聊天界面
    implementation("com.google.android:flexbox:2.0.1")
    implementation("com.github.stfalcon-studio:Chatkit:0.4.1")

    implementation("com.github.siyamed:android-shape-imageview:0.9.+@aar")
    // 阿里云
    implementation("com.aliyun.dpa:oss-android-sdk:+")

    implementation("io.github.scwang90:refresh-layout-kernel:2.1.0") // 核心必须依赖
    implementation("io.github.scwang90:refresh-header-classics:2.1.0") // 经典刷新头
    implementation("io.github.scwang90:refresh-header-radar:2.1.0") // 雷达刷新头
    implementation("io.github.scwang90:refresh-header-falsify:2.1.0") // 虚拟刷新头
    implementation("io.github.scwang90:refresh-header-material:2.1.0") // 谷歌刷新头
    implementation("io.github.scwang90:refresh-header-two-level:2.1.0") // 二级刷新头
    implementation("io.github.scwang90:refresh-footer-ball:2.1.0") // 球脉冲加载
    implementation("io.github.scwang90:refresh-footer-classics:2.1.0") // 经典加载
}
