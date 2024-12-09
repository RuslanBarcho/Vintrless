-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }

-dontwarn okhttp3.internal.platform.**

# Serialization
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }

-keepclassmembers public class **$$serializer {
    private ** descriptor;
}

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Realm
-keep class io.realm.kotlin.types.RealmInstant$Companion
-keepclassmembers class io.realm.kotlin.types.RealmInstant {
    io.realm.kotlin.types.RealmInstant$Companion Companion;
}
-keep class org.mongodb.kbson.BsonObjectId$Companion
-keepclassmembers class org.mongodb.kbson.BsonObjectId {
    org.mongodb.kbson.BsonObjectId$Companion Companion;
}
-keep class io.realm.kotlin.dynamic.DynamicRealmObject$Companion, io.realm.kotlin.dynamic.DynamicMutableRealmObject$Companion
-keepclassmembers class io.realm.kotlin.dynamic.DynamicRealmObject, io.realm.kotlin.dynamic.DynamicMutableRealmObject {
    **$Companion Companion;
}
-keep,allowobfuscation class ** implements io.realm.kotlin.types.BaseRealmObject
-keep class ** implements io.realm.kotlin.internal.RealmObjectCompanion
-keepclassmembers class ** implements io.realm.kotlin.types.BaseRealmObject {
    **$Companion Companion;
}

## Preserve all native method names and the names of their classes.
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

## Preserve all classes that are looked up from native code
# Notification callback
-keep class io.realm.kotlin.internal.interop.NotificationCallback {
    *;
}
# Utils to convert core errors into Kotlin exceptions
-keep class io.realm.kotlin.internal.interop.CoreErrorConverter {
    *;
}
-keep class io.realm.kotlin.internal.interop.JVMScheduler {
    *;
}
# Interop, sync-specific classes
-keep class io.realm.kotlin.internal.interop.sync.NetworkTransport {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.Response {
    *;
}
-keep class io.realm.kotlin.internal.interop.LongPointerWrapper {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.AppError {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.CoreConnectionState {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.SyncError {
    *;
}
-keep class io.realm.kotlin.internal.interop.LogCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.SyncErrorCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.JVMSyncSessionTransferCompletionCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.ResponseCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.ResponseCallbackImpl {
    *;
}
-keep class io.realm.kotlin.internal.interop.AppCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.CompactOnLaunchCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.MigrationCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.DataInitializationCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.SubscriptionSetCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.SyncBeforeClientResetHandler {
    *;
}
-keep class io.realm.kotlin.internal.interop.SyncAfterClientResetHandler {
    *;
}
-keep class io.realm.kotlin.internal.interop.AsyncOpenCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.NativePointer {
    *;
}
-keep class io.realm.kotlin.internal.interop.ProgressCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.ApiKeyWrapper {
    *;
}
-keep class io.realm.kotlin.internal.interop.ConnectionStateChangeCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.SyncThreadObserver {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.CoreCompensatingWriteInfo {
    *;
}
# Preserve Function<X> methods as they back various functional interfaces called from JNI
-keep class kotlin.jvm.functions.Function* {
    *;
}
-keep class kotlin.Unit {
    *;
}

# Platform networking callback
-keep class io.realm.kotlin.internal.interop.sync.WebSocketTransport {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.CancellableTimer {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.WebSocketClient {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.WebSocketObserver {
    *;
}
-keep class io.realm.kotlin.Deleteable {
    *;
}
-keep class io.realm.kotlin.jvm.SoLoader {
    *;
}
-keep class pw.vintr.vintrless.data.routing.model.ExcludeRulesetCacheObject

# KileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }