# ==========================================
# SRLAppExperiment ProGuard Rules
# ==========================================

# Keep line number information for crash reporting
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name in crash reports
-renamesourcefileattribute SourceFile

# ==========================================
# Firebase
# ==========================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**

# Firebase Crashlytics
-keepattributes *Annotation*
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firestore.** { *; }

# Firebase Performance
-keep class com.google.firebase.perf.** { *; }

# ==========================================
# Hilt / Dagger
# ==========================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <fields>;
}
-dontwarn dagger.hilt.**

# ==========================================
# Room Database
# ==========================================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.paging.**

# ==========================================
# Kotlin Coroutines
# ==========================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ==========================================
# Kotlin Serialization / Reflection
# ==========================================
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-dontwarn kotlin.reflect.jvm.internal.**

# ==========================================
# Gson
# ==========================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==========================================
# Domain Models (keep for serialization)
# ==========================================
-keep class com.example.srlappexperiment.domain.model.** { *; }
-keep class com.example.srlappexperiment.data.local.database.entities.** { *; }

# ==========================================
# Jetpack Compose
# ==========================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ==========================================
# DataStore
# ==========================================
-keep class androidx.datastore.** { *; }

# ==========================================
# WorkManager
# ==========================================
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ==========================================
# Google AI / Gemini SDK
# ==========================================
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**