# 快速发送 Agora Chat 消息

本文详细介绍如何建立一个简单的项目并使用 Agora Chat SDK 实现消息的发送，加入群组及搭建三方推送。

## 消息发送与接收流程
// todo 需要增加一张流程图

登录 Agora Chat 系统包括以下流程：
1. 客户端向业务服务器请求 Token。
2. 业务服务器返回 Token。
3. 客户端使用获得的 Token 登录 Chat 服务器。

// todo 需要增加一张流程图

发送和接收点对点消息包括以下流程：
1. 客户端 A 发送点对点消息到 Chat 服务器。
2. Chat 服务器将消息发送到客户端 B。客户端 B 收到点对点消息。

## 前提条件
 - 安装 Android 4.4+ 及以上版本操作系统的 Android 模拟器或真实设备。
 - Android Studio 3.2或更高版本。
 - Android SDK (版本取决于目标平台)。
 - Java Development Kit (JDK)，版本选择参考 Android 官方文档。
 - 有效的 Agora 开发者账号。

 ## 操作步骤

 ### 1.创建 Agora 项目并获取AppKey

// todo 图片需要替换或者更换到本地
 1. 在我的应用中，点击【创建IM应用】按钮:

 ![](https://docs-im.easemob.com/_media/im/quickstart/guide/3_.png)

 2. 填写创建应用的名称（内容只限于数字、大小写字母）:

 ![](https://docs-im.easemob.com/_media/im/quickstart/guide/%E5%88%9B%E5%BB%BA%E5%BC%B9%E7%AA%97.png)

 应用名称会存在于你生成的 AppKey 中，如：AppKey 为easemob-demo#chatdemo，则 chatdemo 为填写的应用名称。注册授权根据需要自行选择，AppKey的长度限制为1k字节以内。

 3. 填写好应用名称后，点确定。创建成功，系统会为你生成 AppKey 以及相关配置信息:

 ![](https://docs-im.easemob.com/_media/im/quickstart/guide/%E5%BA%94%E7%94%A8%E8%AF%A6%E6%83%85.png)

 ### 2.创建 Android 项目

 使用 Android Studio 创建一个 [Android 项目](https://developer.android.google.cn/studio/projects/create-project)。

- Project Template 选择 Empty Activity。
- Name 设为 AgoraChatQuickstart。
- Package name 设为 io.agora.agorachatquickstart。
- Language 选择 Java。

### 3.集成 Agora Chat SDK
选择以下任意一种方式将 Agora chat SDK 集成到你的项目中。本文使用方法 1 进行集成。

**方法 1：使用 MavenCentral 自动集成**

在项目根目录的 build.gradle 文件中添加 MavenCentral 远程仓库。

```java
buildscript {
    repositories {
        ...
        mavenCentral()
    }
}
allprojects {
        repositories {
            ...
            mavenCentral()
        }
    }
```
在项目的 /app/build.gradle 文件添加 io.hyphenate:chat-sdk 依赖项（X.Y.Z 为当前版本号）。你可以在 [Sonatype](https://search.maven.org/) 官网查询最新版本号。

```java
android {
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    ...
    implementation 'io.hyphenate:chat-sdk:X.Y.Z'
}
```

**方法 2：手动下载 SDK 包**

// todo 需要添加下载链接
1. 下载最新版的 [Agora Chat SDK for Android]() 并解压。
2. 将 SDK 包内 libs 路径下的以下文件，拷贝到你的项目路径下：

| 文件                                           | 对应项目文件                                     |
|:-----------------------------------------------|:----------------------------------------------|
| agorachat_3.8.5.jar                            | ~/app/libs/                                   |
| /arm64-v8a/libagora-chat-sdk.so及libsqlite.so  | ~/app/src/main/jniLibs/arm64-v8a/             |
| /armeabi-v7a/libagora-chat-sdk.so及libsqlite.so | ~/app/src/main/jniLibs/armeabi-v7a/          |
| /x86/libagora-chat-sdk.so及libsqlite.so        | ~/app/src/main/jniLibs/x86/                   |
| /x86_64/libagora-chat-sdk.so及libsqlite.so     | ~/app/src/main/jniLibs/x86_64/                 |

### 4.防止代码混淆

在 app/proguard-rules.pro 文件中添加如下行，防止代码混淆：
```java
-keep class io.agora.** {*;}
-dontwarn  io.agora.**
```

### 5.添加权限

在 AndroidManifest.xml 中添加以下权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

### 6.实现用户界面和资源文件

为了帮助你快速实现并理解相关功能，本文通过最简方式，在一个 Activity 里实现以下操作：

- 登录和退出
- 发送一条文本消息
- 加入群组和从群组退出
- 加入聊天室和从聊天室退出
- 发送一条图片消息
- 配置 FCM 推送

1. 打开 app/res/layout/activity_main.xml 并将文件内容替换为以下 XML 代码：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <EditText
                android:id="@+id/et_username"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_username"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="20dp"/>

            <EditText
                android:id="@+id/et_pwd"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_password"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/et_username"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="10dp"/>

            <Button
                android:id="@+id/btn_sign_in"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/sign_in"
                android:onClick="signInWithToken"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintRight_toLeftOf="@id/btn_sign_out"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="10dp"
                android:layout_marginTop="10dp"/>

            <Button
                android:id="@+id/btn_sign_out"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/sign_out"
                android:onClick="signOut"
                app:layout_constraintLeft_toRightOf="@id/btn_sign_in"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintRight_toLeftOf="@id/btn_sign_up"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="10dp"
                android:layout_marginTop="10dp"/>

            <Button
                android:id="@+id/btn_sign_up"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/sign_up"
                android:onClick="signUp"
                app:layout_constraintLeft_toRightOf="@id/btn_sign_out"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintTop_toTopOf="@id/btn_sign_in"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="20dp"/>

            <EditText
                android:id="@+id/et_to_chat_name"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_to_send_name"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/btn_sign_in"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="20dp"/>

            <EditText
                android:id="@+id/et_msg_content"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_content"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toLeftOf="@id/btn_send_message"
                app:layout_constraintTop_toBottomOf="@id/et_to_chat_name"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="20dp"/>

            <Button
                android:id="@+id/btn_send_message"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/send_message"
                android:onClick="sendFirstMessage"
                app:layout_constraintLeft_toRightOf="@id/et_msg_content"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintBottom_toBottomOf="@id/et_msg_content"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="20dp"/>

            <Button
                android:id="@+id/btn_send_image_message"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/send_image_message"
                android:onClick="sendImageMessage"
                app:layout_constraintLeft_toLeftOf="@id/btn_send_message"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/btn_send_message"
                android:layout_marginTop="10dp"
                android:layout_marginEnd="20dp"/>

            <EditText
                android:id="@+id/et_group_id"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_group_id"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toLeftOf="@id/btn_join_group"
                app:layout_constraintTop_toBottomOf="@id/btn_send_image_message"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="30dp"/>

            <Button
                android:id="@+id/btn_join_group"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/join_group"
                android:onClick="joinChatGroup"
                app:layout_constraintLeft_toRightOf="@id/et_group_id"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintBottom_toBottomOf="@id/et_group_id"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="20dp"/>

            <Button
                android:id="@+id/btn_leave_group"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/quit_group"
                android:onClick="leaveChatGroup"
                app:layout_constraintLeft_toLeftOf="@id/btn_join_group"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/btn_join_group"
                android:layout_marginTop="10dp"
                android:layout_marginEnd="20dp"/>

            <EditText
                android:id="@+id/et_chat_room_id"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_chat_room_id"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toLeftOf="@id/btn_join_chat_room"
                app:layout_constraintTop_toBottomOf="@id/btn_leave_group"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="30dp"/>

            <Button
                android:id="@+id/btn_join_chat_room"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/join_chat_room"
                android:onClick="joinChatRoom"
                app:layout_constraintLeft_toRightOf="@id/et_chat_room_id"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintBottom_toBottomOf="@id/et_chat_room_id"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="20dp"/>

            <Button
                android:id="@+id/btn_leave_chat_room"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/quit_chat_room"
                android:onClick="leaveChatRoom"
                app:layout_constraintLeft_toLeftOf="@id/btn_join_chat_room"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/btn_join_chat_room"
                android:layout_marginTop="10dp"
                android:layout_marginEnd="20dp"/>

        </androidx.constraintlayout.widget.ConstraintLayout>

    </ScrollView>

    <TextView
        android:id="@+id/tv_log"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:hint="@string/log_hint"
        android:scrollbars="vertical"
        android:padding="10dp"/>

</LinearLayout>
```
2. 打开 app/res/values/strings.xml 并将内容替换为以下 XML 代码：

```java
<resources>
    <string name="app_name">Chat-Android</string>

    <string name="username_or_pwd_miss">Username or password is empty</string>
    <string name="sign_up_success">Sign up success!</string>
    <string name="sign_in_success">Sign in success!</string>
    <string name="sign_out_success">Sign out success!</string>
    <string name="send_message_success">Send message success!</string>
    <string name="enter_username">Enter username</string>
    <string name="enter_password">Enter password</string>
    <string name="sign_in">Sign in</string>
    <string name="sign_out">Sign out</string>
    <string name="sign_up">Sign up</string>
    <string name="enter_content">Enter content</string>
    <string name="enter_to_send_name">Enter the username you want to send</string>
    <string name="send_message">Send text</string>
    <string name="send_image_message">Send image</string>
    <string name="log_hint">Show log area...</string>
    <string name="has_login_before">An account has been signed in, please sign out first and then sign in</string>
    <string name="sign_in_first">Please sign in first</string>
    <string name="not_find_send_name">Please enter the username who you want to send first!</string>
    <string name="message_is_null">Message is null!</string>
    <string name="enter_group_id">Enter group id</string>
    <string name="join_group">Join group</string>
    <string name="quit_group">Quit group</string>
    <string name="join_group_success">Join group success!</string>
    <string name="leave_group_success">Leave group success!</string>
    <string name="enter_chat_room_id">Enter chat room id</string>
    <string name="join_chat_room">Join chatroom</string>
    <string name="quit_chat_room">Quit chatroom</string>
    <string name="join_chat_room_success">Join chat room success!</string>
    <string name="leave_chat_room_success">Leave chat room success!</string>

    <string name="app_key">Your App Key</string>
</resources>
```

你需要编辑以下字段：

- 将 Your App Key 替换为你的 App Key。

### 7.配置发送图片的设置
发送图片需要打开相册，Android 7.0以后要求需要在AndroidManifest.xml中增加以下配置：
```java
<!-- After android 7.0, you should add -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:grantUriPermissions="true"
    android:exported="false">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```
将上面的代码放到标签 application 内，与标签activity平级的地方。

同时需要在 app/res 文件夹中增加文件夹xml及file_paths.xml文件，代码如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path path="Android/data/io/agora/agorachatquickstart/" name="files_root" />
    <external-path path="." name="external_storage_root" />
</paths>
```

### 8.配置 FCM 推送
// todo 增加配置FCM推送的链接
详细配置参见：配置 FCM 推送

在本项目中需要增加如下配置：
1. 将 Firebase Android 配置文件（google-services.json）添加到 app 文件夹下。
2. 在项目根目录的 build.gradle 文件中添加 Google 服务插件

```java
buildscript {

  repositories {
    // Check that you have the following line (if not, add it):
    google()  // Google's Maven repository
  }

  dependencies {
    // ...

    // Add the following line:
    classpath 'com.google.gms:google-services:4.3.10'  // Google Services plugin
  }
}

allprojects {
  // ...

  repositories {
    // Check that you have the following line (if not, add it):
    google()  // Google's Maven repository
    // ...
  }
}
```
3. 在项目的 /app/build.gradle 文件中应用 Google 服务 Gradle 插件
```java
apply plugin: 'com.android.application'
// Add the following line:
apply plugin: 'com.google.gms.google-services'  // Google Services plugin

android {
  // ...
}
```
4. 在项目的 /app/build.gradle 文件配置Firebase SDK
```java
dependencies {
    // ...

    // FCM: Import the Firebase BoM
    implementation platform('com.google.firebase:firebase-bom:28.4.1')
    // FCM: Declare the dependencies for the Firebase Cloud Messaging
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation 'com.google.firebase:firebase-messaging'

}
```
5.同步应用后，继承 FirebaseMessagingService 的服务，并将其在AndroidManifest.xml中注册
```xml
<service
    android:name=".java.MyFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```
MyFirebaseMessagingService的实现逻辑如下：

```java
package io.agora.agorachatquickstart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Log.i("MessagingService", "onNewToken: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        if(ChatClient.getInstance().isSdkInited()) {
            ChatClient.getInstance().sendFCMTokenToServer(token);
        }
    }
}

```
6. 初始化并注册 FCM 到 Chat SDK
此部分代码均在下一步替换 MainActivity.java 中。这里简要介绍一下思路。

（1）在 Chat SDK 初始化时注册 FCM
```java
private void initSDK() {
    ChatOptions options = new ChatOpti
    ...
    initFCM(options);
    // To initialize Agora Chat SDK
    ChatClient.getInstance().init(this, options);
    ...
}

private void initFCM(ChatOptions options) {
    PushConfig.Builder builder = new PushConfig.Builder(this);
    // Replace to Your FCM sender id
    builder.enableFCM("Your FCM sender id");
    options.setPushConfig(builder.build());
    PushHelper.getInstance().setPushListener(new PushListener() {
        @Override
        public void onError(PushType pushType, long errorCode) {
            EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
        }
        @Override
        public boolean isSupportPush(PushType pushType, PushConfig pushConfig) {
            // Set whether FCM is supported
            if(pushType == PushType.FCM) {
                return GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this)
                        == ConnectionResult.SUCCESS;
            }
            return super.isSupportPush(pushType, pushConfig);
        }
    });
}
``` 

(2) 登录成功后，上传 FCM Token
```java
private void uploadFCMToken() {
    // If not login before, should not upload
    if(!ChatClient.getInstance().isLoggedInBefore()) {
        return;
    }
    // Check whether FCM is supported
    if(GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this) == ConnectionResult.SUCCESS) {
        return;
    }
    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
            if (!task.isSuccessful()) {
                EMLog.d("PushClient", "Fetching FCM registration token failed:"+task.getException());
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();
            EMLog.d("FCM", token);
            ChatClient.getInstance().sendFCMTokenToServer(token);
        }
    });
}
```

### 9.实现消息发送与加入群组逻辑

1. 打开 app/java/io.agora.agorachatquickstart/MainActivity.java 并将内容替换为以下 Java 代码：

```java
package io.agora.agorachatquickstart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import io.agora.CallBack;
import io.agora.ValueCallBack;
import io.agora.agorachatquickstart.utils.ImageUtils;
import io.agora.agorachatquickstart.utils.LogUtils;
import io.agora.agorachatquickstart.utils.PermissionsManager;
import io.agora.agorachatquickstart.utils.ThreadManager;
import io.agora.exceptions.ChatException;
import io.agora.push.PushConfig;
import io.agora.push.PushHelper;
import io.agora.push.PushListener;
import io.agora.push.PushType;
import io.agora.util.EMLog;
import io.agora.util.UriUtils;


public class MainActivity extends AppCompatActivity {
    private EditText et_username;
    private TextView tv_log;
    private EditText et_to_chat_name;
    private EditText et_group_id;
    private EditText et_chat_room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSDK();
    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        et_to_chat_name = findViewById(R.id.et_to_chat_name);
        et_group_id = findViewById(R.id.et_group_id);
        et_chat_room_id = findViewById(R.id.et_chat_room_id);
    }

//=================== init SDK start ========================
    private void initSDK() {
        ChatOptions options = new ChatOptions();
        // Set your appkey applied from Agora Console
        String sdkAppkey = getString(R.string.app_key);
        if(TextUtils.isEmpty(sdkAppkey)) {
            Toast.makeText(MainActivity.this, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set your appkey to options
        options.setAppKey(sdkAppkey);
        // Set you to use HTTPS only
        options.setUsingHttpsOnly(true);
        initFCM(options);
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
        // Upload FCM token
        uploadFCMToken();
    }

    private void initFCM(ChatOptions options) {
        PushConfig.Builder builder = new PushConfig.Builder(this);
        // Replace to Your FCM sender id
        builder.enableFCM("Your FCM sender id");
        options.setPushConfig(builder.build());

        PushHelper.getInstance().setPushListener(new PushListener() {
            @Override
            public void onError(PushType pushType, long errorCode) {
                EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
            }

            @Override
            public boolean isSupportPush(PushType pushType, PushConfig pushConfig) {
                // Set whether FCM is supported
                if(pushType == PushType.FCM) {
                    return GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this)
                            == ConnectionResult.SUCCESS;
                }
                return super.isSupportPush(pushType, pushConfig);
            }
        });
    }

    private void uploadFCMToken() {
        // If not login before, should not upload
        if(!ChatClient.getInstance().isLoggedInBefore()) {
            return;
        }
        // Check whether FCM is supported
        if(GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this) == ConnectionResult.SUCCESS) {
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    EMLog.d("PushClient", "Fetching FCM registration token failed:"+task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                EMLog.d("FCM", token);
                ChatClient.getInstance().sendFCMTokenToServer(token);
            }
        });
    }
//=================== init SDK end ========================

//=================== click event start ========================

    /**
     * Sign up with username and password
     */
    public void signUp(View view) {
        String username = et_username.getText().toString().trim();
        String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.username_or_pwd_miss));
            return;
        }
        ThreadManager.getInstance().execute(()-> {
            try {
                ChatClient.getInstance().createAccount(username, pwd);
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_up_success));
            } catch (ChatException e) {
                e.printStackTrace();
                LogUtils.showErrorLog(tv_log, e.getDescription());
            }
        });
    }

    /**
     * Login with token
     */
    public void signInWithToken(View view) {
        if(ChatClient.getInstance().isLoggedInBefore()) {
            LogUtils.showErrorLog(tv_log, getString(R.string.has_login_before));
            return;
        }
        String username = et_username.getText().toString().trim();
        String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            LogUtils.showErrorToast(MainActivity.this, tv_log, getString(R.string.username_or_pwd_miss));
            return;
        }
        ChatClient.getInstance().login(username, pwd, new CallBack() {
            @Override
            public void onSuccess() {
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_in_success));
                // After login successful, you can upload FCM token
                uploadFCMToken();
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "Login failed! code: "+code + " error: "+error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * Sign out
     */
    public void signOut(View view) {
        if(ChatClient.getInstance().isLoggedInBefore()) {
            ChatClient.getInstance().logout(true, new CallBack() {
                @Override
                public void onSuccess() {
                    LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_out_success));
                }

                @Override
                public void onError(int code, String error) {
                    LogUtils.showErrorToast(MainActivity.this, tv_log, "Sign out failed! code: "+code + " error: "+error);
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }
    }

    /**
     * Send your first message
     */
    public void sendFirstMessage(View view) {
        if(!ChatClient.getInstance().isLoggedInBefore()) {
            LogUtils.showErrorLog(tv_log, getString(R.string.sign_in_first));
            return;
        }
        String toSendName = et_to_chat_name.getText().toString().trim();
        if(TextUtils.isEmpty(toSendName)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.not_find_send_name));
            return;
        }
        EditText et_msg_content = findViewById(R.id.et_msg_content);
        String content = et_msg_content.getText().toString().trim();

        // Create a text message
        ChatMessage message = ChatMessage.createTxtSendMessage(content, toSendName);
        sendMessage(message);
    }

    /**
     * Send your first image message
     */
    public void sendImageMessage(View view) {
        // Check if have the permission of READ_EXTERNAL_STORAGE
        if(!PermissionsManager.getInstance().hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionsManager.getInstance().requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return;
        }
        // Open the photo album
        ImageUtils.openPhotoAlbum(this, 200);
    }

    /**
     * Join your first chat group
     */
    public void joinChatGroup(View view) {
        String groupId = et_group_id.getText().toString().trim();
        if(TextUtils.isEmpty(groupId)) {
            // If you not enter the id of the group you want to join, the default value will be used
            // todo add the public group id
            groupId = "";
        }
        ChatClient.getInstance().groupManager().asyncJoinGroup(groupId, new CallBack() {
            @Override
            public void onSuccess() {
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.join_group_success));
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "Join group failed! code: "+code + " error: "+error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * Exit your joined chat group
     */
    public void leaveChatGroup(View view) {
        String groupId = et_group_id.getText().toString().trim();
        if(TextUtils.isEmpty(groupId)) {
            // todo add the public group id
            groupId = "";
        }
        ChatClient.getInstance().groupManager().asyncLeaveGroup(groupId, new CallBack() {
            @Override
            public void onSuccess() {
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.leave_group_success));
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "Leave group failed! code: "+code + " error: "+error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * Join your first chat room
     */
    public void joinChatRoom(View view) {
        String roomId = et_chat_room_id.getText().toString().trim();
        if(TextUtils.isEmpty(roomId)) {
            // todo add the public group id
            roomId = "";
        }
        ChatClient.getInstance().chatroomManager().joinChatRoom(roomId, new ValueCallBack<ChatRoom>() {
            @Override
            public void onSuccess(ChatRoom value) {
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.join_chat_room_success));
            }

            @Override
            public void onError(int error, String errorMsg) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "Join chat room failed! code: "+error + " error: "+error);
            }
        });
    }

    /**
     * Leave the chat room you joined
     */
    public void leaveChatRoom(View view) {
        String roomId = et_chat_room_id.getText().toString().trim();
        if(TextUtils.isEmpty(roomId)) {
            // todo add the public group id
            roomId = "";
        }
        // If you fail to log out, the server will remove you from the chat room
        // after you have been offline for a certain amount of time.
        ChatClient.getInstance().chatroomManager().leaveChatRoom(roomId);
    }

//=================== click event end ========================

    private void sendImageMessage(String imageUrl) {
        String toSendName = et_to_chat_name.getText().toString().trim();
        if(TextUtils.isEmpty(toSendName)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.not_find_send_name));
            return;
        }
        // Create a image message with the absolute path
        ChatMessage message = ChatMessage.createImageSendMessage(imageUrl, false, toSendName);
        sendMessage(message);
    }

    private void sendImageMessage(Uri imageUri) {
        String toSendName = et_to_chat_name.getText().toString().trim();
        if(TextUtils.isEmpty(toSendName)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.not_find_send_name));
            return;
        }
        // Create a image message with the image uri
        ChatMessage message = ChatMessage.createImageSendMessage(imageUri, false, toSendName);
        sendMessage(message);
    }

    private void sendMessage(ChatMessage message) {
        // Check if the message is null
        if(message == null) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.message_is_null));
            return;
        }
        // Set the message callback before sending the message
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.send_message_success));
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "Send message failed! code: "+code + " error: " + error );
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        // Send the message
        ChatClient.getInstance().chatManager().sendMessage(message);
    }

    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = UriUtils.getFilePath(this, selectedImage);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    sendImageMessage(filePath);
                }else {
                    sendImageMessage(selectedImage);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if(resultCode == RESULT_OK) {
                if(requestCode == 200) {
                    onActivityResultForLocalPhotos(data);
                }
            }
        }
    }
}
```
2. 此外 MainActivity 中用到几个工具类，将这个几个工具类拷贝复制到项目中的utils文件夹中，如下：

- ImageUtils
```java
package io.agora.agorachatquickstart.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;


public class ImageUtils {

    /**
     * Open system Album
     * @param activity
     * @param requestCode
     */
    public static void openPhotoAlbum(Activity activity, int requestCode) {
        Intent intent = null;
        if(Build.VERSION.SDK_INT >= 29 && activity.getApplicationInfo().targetSdkVersion >= 29) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }
}

```

- LogUtils
```java
package io.agora.agorachatquickstart.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LogUtils {
    private static final String TAG = LogUtils.class.getSimpleName();

    public static void showErrorLog(TextView tvLog, String content) {
        showLog(tvLog, content);
    }

    public static void showNormalLog(TextView tvLog, String content) {
        showLog(tvLog, content);
    }

    public static void showLog(TextView tvLog, String content) {
        if(TextUtils.isEmpty(content) || tvLog == null) {
            return;
        }
        String preContent = tvLog.getText().toString().trim();
        content = content + "\n" + preContent;
        tvLog.setText(content);
    }

    public static void showErrorToast(Activity activity, TextView tvLog, String content) {
        if(activity == null) {
            Log.e(TAG, "Context is null...");
            return;
        }
        if(TextUtils.isEmpty(content)) {
            return;
        }
        ThreadManager.getInstance().executeUI(()-> {
            Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
            showErrorLog(tvLog,content);
        });
    }

    public static void showToast(Activity activity, TextView tvLog, String content) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        ThreadManager.getInstance().executeUI(()-> {
            Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
            showNormalLog(tvLog, content);
        });
    }
}

```
- ThreadManager
```java
package io.agora.agorachatquickstart.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Simple thread manager
 */
public class ThreadManager {
    private static ThreadManager mInstance;
    private ThreadManager(){}

    public synchronized static ThreadManager getInstance() {
        if(mInstance == null) {
            synchronized (ThreadManager.class) {
                if(mInstance == null) {
                    mInstance = new ThreadManager();
                }
            }
        }
        return mInstance;
    }

    public void executeUI(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}

```

- PermissionsManager
```java
package io.agora.agorachatquickstart.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class PermissionsManager {
    private static PermissionsManager mInstance = null;

    public static PermissionsManager getInstance() {
        if (mInstance == null) {
            mInstance = new PermissionsManager();
        }
        return mInstance;
    }

    private PermissionsManager() {}

    /**
     * Check if has permission
     * @param context
     * @param permission
     * @return
     */
    @SuppressWarnings("unused")
    public synchronized boolean hasPermission(@Nullable Context context, @NonNull String permission) {
        return context != null && ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request permissions
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public synchronized void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}

```

### 10.编译并运行项目

使用 Android Studio 在模拟器或真机上编译并运行项目。运行成功之后，你可以进行以下操作：

- 登录和退出
- 发送一条文本消息
- 加入群组和从群组退出
- 加入聊天室和从聊天室退出
- 发送一条图片消息
- 接收 FCM 离线推送

运行效果如下图所示：

// todo 需要增加一张运行图片