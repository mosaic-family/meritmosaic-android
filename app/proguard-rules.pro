# MeritMosaic ProGuard rules — minimal, mirror parkarmor pattern.
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses

# kotlinx.serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1>$Companion {
    public static ** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
