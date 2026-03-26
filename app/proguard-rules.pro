# Apache POI rules
-dontwarn org.apache.poi.**
-dontwarn org.apache.commons.collections4.**
-dontwarn javax.xml.stream.**
-dontwarn com.zaxxer.sparsebits.**
-dontwarn org.checkerframework.**
-dontwarn com.github.luben.zstd.**
-dontwarn org.tukaani.xz.**
-dontwarn org.bouncycastle.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn org.etsi.uri.x01903.v13.**
-dontwarn org.w3.x2000.x09.xmldsig.**
-dontwarn com.microsoft.schemas.**
-dontwarn org.apache.logging.log4j.**
-dontwarn aQute.bnd.annotation.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn org.osgi.framework.**

-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.schemas.** { *; }
-keep class com.microsoft.schemas.** { *; }

# iTextG rules
-dontwarn com.itextpdf.**
-keep class com.itextpdf.** { *; }

# General
-dontwarn java.awt.**
-dontwarn javax.xml.**
-dontwarn org.w3c.dom.**
-dontwarn org.xml.sax.**
