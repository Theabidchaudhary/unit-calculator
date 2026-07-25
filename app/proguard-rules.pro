# Keep kotlinx.serialization metadata for models serialized in backup/export.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.orwyx.unitcalculator.** {
    *** Companion;
}
-keepclasseswithmembers class com.orwyx.unitcalculator.** {
    kotlinx.serialization.KSerializer serializer(...);
}
