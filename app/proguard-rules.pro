# Add project specific ProGuard rules here.
-keep class nl.siegmann.epublib.** { *; }
-keep class com.localreader.data.database.** { *; }
-keep class com.localreader.data.model.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
