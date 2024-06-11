# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# OkHttp3 框架混淆规则
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# EasyHttp 框架混淆规则
-keep class com.hjq.http.** {*;}

# 必须要加上此规则，否则会导致泛型解析失败
-keep public class * implements com.hjq.http.listener.OnHttpListener {
    *;
}

# 必须要加上此规则，否则可能会导致 Bean 类的字段无法解析成后台返回的字段，xxx 请替换成对应包名
-keep class com.xxx.xxx.xxx.xxx.** {
    <fields>;
}