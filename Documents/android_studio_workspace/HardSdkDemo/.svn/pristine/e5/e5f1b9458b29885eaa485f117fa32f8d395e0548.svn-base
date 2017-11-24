# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\soft\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:


-libraryjars libs/SMSSDK-2.0.2.aar


# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# 友盟混淆
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep public class **.R$*{
   public static final int *;
}

#EventBus 混淆

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}
-keepclassmembers class ** {
public void xxxxxx(**);
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

## ----------------------------------
##      sharesdk
## ----------------------------------
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class com.xxx.share.onekey.theme.classic.EditPage

    -keep class cn.sharesdk.**{*;}
    -keep class com.sina.**{*;}
    -keep class **.R$* {*;}
    -keep class **.R{*;}
    -keep class com.mob.**{*;}
    -dontwarn com.mob.**
    -dontwarn cn.sharesdk.**
    -dontwarn **.R$*
    #短信sdk
-keep class cn.smssdk.**{*;}


-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


-dontwarn oauth.**
-dontwarn com.android.auth.TwitterHandle.**

-keep class oauth.** { *; }
-keep class com.android.auth.TwitterHandle.** { *; }




# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.walnutin.hardsdkdemo.ProductNeed.entity.** { *; }

#与工程有关的
-keep class com.walnutin.hardsdkdemo.utils.** {*;}
-keep interface com.walnutin.hardsdkdemo.ProductNeed.Jinterface.** {*;}
-keep class com.walnutin.hardsdkdemo.ProductList.HardSdk{*;}
-keep class com.realsil.**{*;}
-keep class com.yc.**{*;}
-keep class com.htsmart.**{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.ycy.**{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.hch.HchSdk{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.hch.DataProcessing{*;}
#-keep class com.walnutin.hardsdkdemo.ProductList.fitcloud.FitCloudSdk{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.fitcloud.DbSyncRawDataEntity{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.fitcloud.FitCloudSdk{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.ad.**{*;}
#-keep class com.walnutin.hardsdkdemo.ProductList.ModelConfig{*;}
-keep class com.walnutin.hardsdkdemo.ProductList.ThirdBaseSdk{*;}
#-keep class com.walnutin.hardsdkdemo.ProductList.ProductFactory{
#        public static ** getInstance();
#        public ** creatSDKImplByUUID(**, **);
#}


-keep class com.walnutin.eventbus.** { *; }



#使用butterknife注入会导致ProGuard 认为变量未被使用，因此，需要在ProGuard 中加入如下字段
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.**{*;}
#==================protobuf======================-
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

#集成百度地图SDK的应用，在打包混淆的时候，需要注意与地图SDK相关的方法不可被混淆，否则会出现网络不可用等运行时异常。混淆方法如下：
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**
-dontwarn com.baidu.mapapi.**
-keep class com.baidu.mapapi.** {*; }
-keep class assets.** {*; }
-keep class vi.com.gdi.bgl.** {*; }

#下面是常见的proguard.cfg配置项
#指定代码的压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

##########JS接口类不混淆，否则执行不了
-dontwarn com.android.JsInterface.**
-keep class com.android.JsInterface.** {*; }

#极光推送和百度lbs android sdk一起使用proguard 混淆的问题#http的类被混淆后，导致apk定位失败，保持apache 的http类不被混淆就好了
-dontwarn org.apache.**
-keep class org.apache.**{ *; }



#忽略警告
-ignorewarning

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射-printmapping mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end#####

######混淆保护自己项目的部分代码以及引用的第三方jar包library########
#如果引用了v4或者v7包
-dontwarn android.support.**
####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

-keep public class * extends android.view.View {
  public <init>(android.content.Context);
  public <init>(android.content.Context, android.util.AttributeSet);
  public <init>(android.content.Context, android.util.AttributeSet, int);
  public void set*(...);
 }

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
  native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
  public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
      public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
      public static <fields>;
}

#避免混淆泛型 如果混淆报错建议关掉
#–keepattributes Signature

#如果用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错
#gson
#-libraryjars libs/gson-2.2.2.jar
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
#客户端代码中的JavaBean(实体类)的类名与其字段名称全部变成了a、b、c、d等等字符串，这与服务端返回的json字符串中的不一致，导致解析失败。所以，解决的办法是：在进行混淆编译进行打包apk的时候，过滤掉存放所有JavaBean（实体类)的包不进行混淆编译
-keep class com.android.model.** {*;}

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-dontwarn com.umeng.**

-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**


#邮件异常
 -keep class javax.mail.**{*;}
 -keep class com.sun.mail.**{*;}
-keep class javax.activation.**{*;}
-keep class org.apache.harmony.**{*;}
-keep class java.security.**{*;}

#AQuery
-keep class com.androidquery.**{*;}

#DFU
-keep class no.nordicsemi.android.dfu.** { *; }

-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#科大讯飞语音
#讯飞
-keep class com.iflytek.**{*;}

# veryfit 爱都
-keep class com.veryfit.multi.**{*;}


-dontwarn java.lang.invoke**
-dontwarn org.apache.lang.**
-dontwarn org.apache.commons.**
-dontwarn com.nhaarman.**
-dontwarn se.emilsjolander.**
-dontwarn com.aphidmobile.**
-dontwarn javax.naming.**

-ignorewarnings



#-keep class * {
#    public private *;
#}


-keepattributes Signature
