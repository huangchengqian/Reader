# Add project specific ProGuard rules here.

# ============================================================================
# Epublib + kxml2 XmlPullParser duplicate-class issue with Android platform
# ============================================================================
# The transitive dependency net.sf.kxml:kxml2:2.3.0 ships
#   - org/xmlpull/v1/XmlPullParser.class
#   - org/xmlpull/v1/XmlPullParserException.class
#   - org/xmlpull/v1/XmlPullParserFactory.class
# However Android's android.content.res.XmlResourceParser implements the same
# org.xmlpull.v1.XmlPullParser interface. R8 refuses to minify because a
# library class (Android framework) implements a program class (kxml2).
#
# Resolution: rename the kxml2 org.xmlpull.v1.* classes so they no longer clash
# with the Android platform implementation. Epublib looks up the parser via
# META-INF/services/org.xmlpull.v1.XmlPullParserFactory which is also renamed,
# so the rewrite is self-consistent.
-keep,allowobfuscation,allowshrinking class org.xmlpull.v1.** { *; }
-keep,allowobfuscation,allowshrinking class org.kxml2.** { *; }

# ============================================================================
# Project rules
# ============================================================================
-keep class nl.siegmann.epublib.** { *; }
-keep class com.localreader.data.database.** { *; }
-keep class com.localreader.data.model.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}