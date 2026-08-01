# Add project-specific ProGuard rules here as R8 strips things it shouldn't.
# Firestore model classes need a no-args constructor + non-obfuscated fields
# for reflection-based deserialization:
-keep class com.aiforall.app.domain.model.** { *; }
