# Keep Room Database components and metadata
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class *

# Custom Data Models - keep entirely to support SQLite column mapping and Moshi serialization
-keep class com.example.data.model.** { *; }
-keep class com.example.data.local.** { *; }

# Retain generic type signatures and annotations for reflection/serialization systems
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Keep generated Room helper tables and code templates
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

