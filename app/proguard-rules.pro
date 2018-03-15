# PrettyTime
-keep class org.ocpsoft.prettytime.i18n.**

#Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature

# GMS
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

# Support appcompat v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Support cardview v7
-keep class android.support.v7.widget.RoundRectDrawable { *; }

# Support design v7
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Support Settings
-keep public class * extends android.support.v7.preference.Preference

# Eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Parcelables (recommended by doc)
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Butterknife
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# RxJava
-dontwarn rx.**