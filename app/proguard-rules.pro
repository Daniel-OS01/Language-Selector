# ProGuard rules for Language-Selector application

# ============================================================================
# === GENERAL SETTINGS ===
# ============================================================================

# Disable obfuscation for easier debugging and reflection compatibility
-dontobfuscate

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Keep source file names in exceptions
-renamesourcefileattribute SourceFile

# ============================================================================
# === HILT DEPENDENCY INJECTION ===
# ============================================================================

# Keep Hilt generated classes
-keep class **_HiltComponents$* { *; }
-keep class **_Factory { *; }
-keep class **_Provide* { *; }
-keep class **_Impl { *; }

# Keep Hilt Module classes and methods
-keep @com.google.dagger.hilt.android.AndroidEntryPoint class *
-keep @com.google.dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.** class * { *; }
-keep @dagger.Module class * { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }

# ============================================================================
# === KOTLIN SYMBOL PROCESSING (KSP) ===
# ============================================================================

# Keep KSP generated classes
-keep class **.generated.** { *; }

# Keep metadata for reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# ============================================================================
# === ROOM DATABASE ===
# ============================================================================

# Keep Room database classes
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Room generated classes
-keep class **_Impl { *; }
-keep class ** extends androidx.room.RoomDatabase { *; }

# Preserve field and method names for Room reflection
-keepclasseswithmembernames class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# ============================================================================
# === JETPACK COMPOSE ===
# ============================================================================

# Keep Compose compiler metadata
-keepattributes RuntimeVisibleParameterAnnotations
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keep @androidx.compose.runtime.Composable fun * { *; }

# ============================================================================
# === ANDROID LIFECYCLE & JETPACK ===
# ============================================================================

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { 
    <init>(...); 
}

# Keep SavedStateHandle
-keep class androidx.lifecycle.SavedStateHandle { *; }

# Keep Navigation classes
-keep class androidx.navigation.** { *; }

# Keep lifecycle aware classes
-keepclasseswithmembernames class * {
    @androidx.lifecycle.* <methods>;
}

# ============================================================================
# === SHIZUKU API ===
# ============================================================================

# Keep Shizuku classes for system integration
-keep class dev.rikka.shizuku.** { *; }
-keep interface dev.rikka.shizuku.** { *; }

# Keep AIDL generated classes
-keep class **.*.aidl.** { *; }

# ============================================================================
# === LIBSU (ROOT ACCESS LIBRARY) ===
# ============================================================================

# Keep LibSu classes
-keep class com.topjohnwu.** { *; }
-keep class com.topjohnwu.superuser.** { *; }

# ============================================================================
# === HIDDEN API BYPASS ===
# ============================================================================

# Keep HiddenApiBypass for reflection
-keep class org.lsposed.hiddenapibypass.HiddenApiBypass { *; }

# ============================================================================
# === MATERIAL DESIGN & UI ===
# ============================================================================

# Keep Material Design components
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }

# Keep Material 3 classes
-keep class androidx.compose.material3.** { *; }

# ============================================================================
# === APPLICATION CLASSES ===
# ============================================================================

# Keep all application classes
-keep class vegabobo.languageselector.** { *; }

# Keep all Activities
-keep class * extends android.app.Activity { *; }
-keep class * extends android.app.Service { *; }
-keep class * extends android.content.BroadcastReceiver { *; }

# ============================================================================
# === NATIVE METHODS ===
# ============================================================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============================================================================
# === OPTIMIZATION ===
# ============================================================================

# Aggressive optimization
-optimizationpasses 5

# Inline aggressive
-allowaccessmodification

# Keep main entry points
-keep public class * {
    public static void main(java.lang.String[]);
}

# ============================================================================
# === MISCELLANEOUS ===
# ============================================================================

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep serializable classes
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep custom annotations
-keepattributes *Annotation*

# ============================================================================
# === WARNINGS ===
# ============================================================================

# Ignore warnings from missing classes
-dontwarn com.google.android.material.**
-dontwarn androidx.compose.**
-dontwarn dev.rikka.shizuku.**
-dontwarn com.topjohnwu.**
-dontwarn org.lsposed.**
