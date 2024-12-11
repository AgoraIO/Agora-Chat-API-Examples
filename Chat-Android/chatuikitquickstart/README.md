Instant messaging connects people wherever they are and allows them to communicate with others in real time. The Agora Chat UIKit enables you to embed real-time messaging in any app, on any device, anywhere.

This page shows a sample code to add peer-to-peer messaging into your app by using the Agora Chat UIKit for Android.

## Understand the tech

The following figure shows the workflow of how clients send and receive peer-to-peer messages.

![](https://web-cdn.agora.io/docs-files/1643335864426)

As shown in the figure, the workflow of peer-to-peer messaging is as follows:

1. Clients retrieve a token from your app server.
2. Client A and Client B log in to Agora Chat.
3. Client A sends a message to Client B. The message is sent to the Agora Chat server and the server delivers the message to Client B. When Client B receives the message, the SDK triggers an event. Client B listens for the event and gets the message.

## Prerequisites

In order to follow the procedure in this page, you must have:

- An Android simulator or a physical Android device.
- Android Studio 3.2 or higher.
- Java Development Kit (JDK). You can refer to the [User Guide of Android](https://developer.android.com/studio/write/java8-support) for applicable versions.

## Project setup

Follow the steps to create the environment necessary to add video call into your app.

1. For new projects, in **Android Studio**, create a **Phone and Tablet** [Android project](https://developer.android.com/studio/projects/create-project) with an **Empty Activity**.
   <div class="alert note">After creating the project, <b>Android Studio</b> automatically starts gradle sync. Ensure that the sync succeeds before you continue.</div>

2. Integrate the Agora Chat SDK into your project with Maven Central. 

    a. In `/Gradle Scripts/build.gradle(Project: <projectname>)`, add the following lines to add the Maven Central dependency:

   ```java
   buildscript {
       repositories {
           mavenCentral()
       }
   }
   ```
	 
	 <div class="alert note">The way to add the Maven Central dependency can be different if you set  <a href="https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration">dependencyResolutionManagement</a> in your Android project.</div>
		
	b. In `/Gradle Scripts/build.gradle(Module: <projectname>.app)`, add the following lines to integrate the Agora Chat UIKit into your Android project:

   ```java
   android {
       defaultConfig {
               // The Android OS version should be 21 or higher.
               minSdkVersion 21
       }
       compileOptions {
           sourceCompatibility JavaVersion.VERSION_1_8
           targetCompatibility JavaVersion.VERSION_1_8
       }
   }
   dependencies {
       ...
       // Replace X.Y.Z with the latest version of the Chat UIKit. 
       // For the latest version, go to https://search.maven.org/.
       implementation 'io.agora.rtc:chat-uikit:X.Y.Z'
   }
   ```

3. Add permissions for network and device access.

   In `/app/Manifests/AndroidManifest.xml`, add the following permissions after `</application>`:
	 
	```xml
	<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
	```
	
	These are the minimum permissions you need to add to start Agora Chat. You can also add other permissions according to your use case.

4. Prevent code obfuscation.

   In `/Gradle Scripts/proguard-rules.pro`, add the following line:

	```java
	-keep class io.agora.** {*;}
	-dontwarn  io.agora.**
	```


## Implement peer-to-peer messaging

This section shows how to use the Agora Chat UIKit to implement peer-to-peer messaging in your app step by step.

### Create the UI

1. To add the text strings used by the UI, open `app/res/values/strings.xml ` and  replace the content with the following:

```xml
<resources>
    <string name="app_name">AgoraChatUIKitQuickstart</string>

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
    <string name="enter_to_username">Enter to username</string>
    <string name="start_chat">Start chat</string>
    <string name="enter_content">Enter content</string>
    <string name="log_hint">Show log area...</string>
    <string name="has_login_before">An account has been signed in, please sign out first and then sign in</string>
    <string name="sign_in_first">Please sign in first</string>
    <string name="not_find_send_name">Please enter the username who you want to send first!</string>

    <string name="app_key">41117440#383391</string>
</resources>
```

2. To add the UI framework, open `app/res/layout/activity_main.xml` and replace the content with the following:

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        tools:context=".MainActivity">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <EditText
                android:id="@+id/et_username"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_username"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toLeftOf="@id/et_pwd"
                app:layout_constraintTop_toTopOf="parent"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="20dp"/>

            <EditText
                android:id="@+id/et_pwd"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_password"
                android:inputType="textPassword"
                app:layout_constraintLeft_toRightOf="@id/et_username"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toTopOf="@id/et_username"
                android:layout_marginEnd="20dp"/>

            <Button
                android:id="@+id/btn_sign_in"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:text="@string/sign_in"
                android:onClick="signInWithToken"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintRight_toLeftOf="@id/btn_sign_out"
                android:layout_marginStart="10dp"
                android:layout_marginEnd="5dp"
                android:layout_marginTop="10dp"/>

            <Button
                android:id="@+id/btn_sign_out"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:text="@string/sign_out"
                android:onClick="signOut"
                app:layout_constraintLeft_toRightOf="@id/btn_sign_in"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintRight_toLeftOf="@id/btn_sign_up"
                android:layout_marginStart="5dp"
                android:layout_marginEnd="5dp"
                android:layout_marginTop="10dp"/>

            <Button
                android:id="@+id/btn_sign_up"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/sign_up"
                android:textSize="12sp"
                android:onClick="signUp"
                app:layout_constraintLeft_toRightOf="@id/btn_sign_out"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/et_pwd"
                app:layout_constraintTop_toTopOf="@id/btn_sign_in"
                android:layout_marginStart="5dp"
                android:layout_marginEnd="10dp"/>

            <EditText
                android:id="@+id/et_to_username"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:hint="@string/enter_to_username"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toLeftOf="@id/btn_start_chat"
                app:layout_constraintTop_toBottomOf="@id/btn_sign_up"
                app:layout_constraintHorizontal_weight="2"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:layout_marginTop="10dp"
                android:layout_marginBottom="10dp"/>

            <Button
                android:id="@+id/btn_start_chat"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="@string/start_chat"
                android:onClick="startChat"
                android:textSize="12sp"
                app:layout_constraintLeft_toRightOf="@id/et_to_username"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintBottom_toBottomOf="@id/et_to_username"
                app:layout_constraintHorizontal_weight="1"
                android:layout_marginEnd="10dp"/>

            <FrameLayout
                android:id="@+id/fl_fragment"
                android:layout_width="match_parent"
                android:layout_height="0dp"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="@id/btn_start_chat"
                app:layout_constraintBottom_toBottomOf="parent"/>

        </androidx.constraintlayout.widget.ConstraintLayout>

        <TextView
            android:id="@+id/tv_log"
            android:layout_width="match_parent"
            android:layout_height="100dp"
            android:hint="@string/log_hint"
            android:scrollbars="vertical"
            android:padding="10dp"/>

    </LinearLayout>
    ```

### Implementation

To enable your app to send and receive messages between individual users, do the following:

1. Implement sending and receiving messages. 

   In  `app/java/io.agora.agorachatquickstart/MainActivity`, replace the code with the following:

    ```java
    package io.agora.chatuikitquickstart;

    import android.Manifest;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.text.method.ScrollingMovementMethod;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

    import io.agora.CallBack;
    import io.agora.ConnectionListener;
    import io.agora.Error;
    import io.agora.chat.ChatClient;
    import io.agora.chat.ChatMessage;
    import io.agora.chat.ChatOptions;
    import io.agora.chat.uikit.EaseUIKit;
    import io.agora.chat.uikit.chat.EaseChatFragment;
    import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
    import io.agora.chat.uikit.chat.interfaces.OnChatInputChangeListener;
    import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
    import io.agora.chat.uikit.chat.interfaces.OnMessageSendCallBack;
    import io.agora.chatuikitquickstart.utils.LogUtils;
    import io.agora.chatuikitquickstart.utils.PermissionsManager;
    import io.agora.cloud.HttpClientManager;
    import io.agora.cloud.HttpResponse;
    import io.agora.util.EMLog;

    import static io.agora.cloud.HttpClientManager.Method_POST;


    public class MainActivity extends AppCompatActivity {
        private static final String NEW_LOGIN = "NEW_LOGIN";
        private static final String RENEW_TOKEN = "RENEW_TOKEN";
        private static final String LOGIN_URL = "https://a41.chat.agora.io/app/chat/user/login";
        private static final String REGISTER_URL = "https://a41.chat.agora.io/app/chat/user/register";
        private EditText et_username;
        private TextView tv_log;
        private ConnectionListener connectionListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initView();
            requestPermissions();
            initSDK();
            addConnectionListener();
        }

        private void initView() {
            et_username = findViewById(R.id.et_username);
            tv_log = findViewById(R.id.tv_log);
            tv_log.setMovementMethod(new ScrollingMovementMethod());
        }

        private void requestPermissions() {
            checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, 110);
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
            // Set whether confirmation of delivery is required by the recipient. Default: false
            options.setRequireDeliveryAck(true);
            // Set not to log in automatically
            options.setAutoLogin(false);
            // Use uikit to initialize Agora Chat SDK
            EaseUIKit.getInstance().init(this, options);
            // Make Agora Chat SDK debuggable
            ChatClient.getInstance().setDebugMode(true);
        }
    //=================== init SDK end ========================
    //================= SDK listener start ====================
        private void addConnectionListener() {
            connectionListener = new ConnectionListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onDisconnected(int error) {
                    switch (error) {
                        case Error.USER_REMOVED:
                            onUserException("account_removed");
                            break;
                        case Error.USER_LOGIN_ANOTHER_DEVICE:
                            onUserException("account_conflict");
                            break;
                        case Error.SERVER_SERVICE_RESTRICTED:
                            onUserException("account_forbidden");
                            break;
                        case Error.USER_KICKED_BY_CHANGE_PASSWORD:
                            onUserException("account_kicked_by_change_password");
                            break;
                        case Error.USER_KICKED_BY_OTHER_DEVICE:
                            onUserException("account_kicked_by_other_device");
                            break;
                        case Error.USER_BIND_ANOTHER_DEVICE:
                            onUserException("user_bind_another_device");
                            break;
                        case Error.USER_DEVICE_CHANGED:
                            onUserException("user_device_changed");
                            break;
                        case Error.USER_LOGIN_TOO_MANY_DEVICES:
                            onUserException("user_login_too_many_devices");
                            break;
                    }
                }

                @Override
                public void onTokenExpired() {
                    //login again
                    signInWithToken(null);
                    LogUtils.showLog(tv_log,"ConnectionListener onTokenExpired");
                }

                @Override
                public void onTokenWillExpire() {
                    getTokenFromAppServer(RENEW_TOKEN);
                    LogUtils.showLog(tv_log, "ConnectionListener onTokenWillExpire");
                }
            };
            // Call removeConnectionListener(connectionListener) when the activity is destroyed
            ChatClient.getInstance().addConnectionListener(connectionListener);
        }

    //================= SDK listener end ====================
    //=================== click event start ========================

        /**
        * Sign up with username and password.
        */
        public void signUp(View view) {
            String username = et_username.getText().toString().trim();
            String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
                LogUtils.showErrorToast(this, tv_log, getString(R.string.username_or_pwd_miss));
                return;
            }
            register(REGISTER_URL, username, pwd, new CallBack() {
                @Override
                public void onSuccess() {
                    LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_up_success));
                }

                @Override
                public void onError(int code, String error) {
                    LogUtils.showErrorLog(tv_log, error);
                }
            });
        }

        /**
        * Log in with token.
        */
        public void signInWithToken(View view) {
            getTokenFromAppServer(NEW_LOGIN);
        }

        /**
        * Sign out.
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

        public void startChat(View view) {
            EditText et_to_username = findViewById(R.id.et_to_username);
            String toChatUsername = et_to_username.getText().toString().trim();
            // check username
            if(TextUtils.isEmpty(toChatUsername)) {
                LogUtils.showErrorToast(this, tv_log, getString(R.string.not_find_send_name));
                return;
            }
            
            EaseChatFragment fragment = new EaseChatFragment.Builder(toChatUsername, EaseChatType.SINGLE_CHAT)
                    .useHeader(false)
                    .setOnChatExtendMenuItemClickListener(new OnChatExtendMenuItemClickListener() {
                        @Override
                        public boolean onChatExtendMenuItemClick(View view, int itemId) {
                            if(itemId == io.agora.chat.uikit.R.id.extend_item_take_picture) {
                                return !checkPermissions(Manifest.permission.CAMERA, 111);
                            }else if(itemId == io.agora.chat.uikit.R.id.extend_item_picture || itemId == io.agora.chat.uikit.R.id.extend_item_file || itemId == io.agora.chat.uikit.R.id.extend_item_video) {
                                return !checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, 112);
                            }
                            return false;
                        }
                    })
                    .setOnChatRecordTouchListener(new OnChatRecordTouchListener() {
                        @Override
                        public boolean onRecordTouch(View v, MotionEvent event) {
                            return !checkPermissions(Manifest.permission.RECORD_AUDIO, 113);
                        }
                    })
                    .setOnMessageSendCallBack(new OnMessageSendCallBack() {
                        @Override
                        public void onSuccess(ChatMessage message) {
                            LogUtils.showLog(tv_log, "Send success: message type: " + message.getType().name());
                        }

                        @Override
                        public void onError(int code, String errorMsg) {
                            LogUtils.showErrorLog(tv_log, "Send failed: error code: "+code + " errorMsg: "+errorMsg);
                        }
                    })
                    .build();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
        }
    //=================== click event end ========================
    //=================== get token from server start ========================

        private void getTokenFromAppServer(String requestType) {
            if(ChatClient.getInstance().getOptions().getAutoLogin() && ChatClient.getInstance().isLoggedInBefore()) {
                LogUtils.showErrorLog(tv_log, getString(R.string.has_login_before));
                return;
            }
            String username = et_username.getText().toString().trim();
            String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
                LogUtils.showErrorToast(MainActivity.this, tv_log, getString(R.string.username_or_pwd_miss));
                return;
            }
            getAgoraTokenFromAppServer(username, pwd, new ValueCallBack<String>() {
                @Override
                public void onSuccess(String token) {
                    if(TextUtils.equals(requestType, NEW_LOGIN)) {
                        login(username,token);
                    }else if(TextUtils.equals(requestType, RENEW_TOKEN)) {
                        ChatClient.getInstance().renewToken(token);
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                    LogUtils.showErrorToast(MainActivity.this, tv_log, "getTokenFromAppServer failed! code: " + error + " error: " + errorMsg);
                }
            });
        }
        private void getAgoraTokenFromAppServer(String username, String pwd, ValueCallBack<String> callBack) {
            execute(()-> {
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");

                    JSONObject request = new JSONObject();
                    request.putOpt("userAccount", username);
                    request.putOpt("userPassword", pwd);

                    LogUtils.showErrorLog(tv_log,"begin to getTokenFromAppServer ...");

                    HttpResponse response = HttpClientManager.httpExecute(LOGIN_URL, headers, request.toString(), Method_POST);
                    int code = response.code;
                    String responseInfo = response.content;
                    if (code == 200) {
                        if (responseInfo != null && responseInfo.length() > 0) {
                            JSONObject object = new JSONObject(responseInfo);
                            String token = object.getString("accessToken");
                            if(callBack != null) {
                                callBack.onSuccess(token);
                            }
                        } else {
                            if(callBack != null) {
                                callBack.onError(Error.SERVER_UNKNOWN_ERROR, responseInfo);
                            }
                        }
                    } else {
                        if(callBack != null) {
                            callBack.onError(code, responseInfo);
                        }
                    }
                } catch (Exception e) {
                    if(callBack != null) {
                        callBack.onError(Error.GENERAL_ERROR, e.getMessage());
                    }
                }
            });
        }
    //=================== get token from server end ========================
    //=================== login and register start ========================
        private void login(String username, String token) {
            ChatClient.getInstance().loginWithToken(username, token, new CallBack() {
                @Override
                public void onSuccess() {
                    LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_in_success));
                }

                @Override
                public void onError(int code, String error) {
                    LogUtils.showErrorToast(MainActivity.this, tv_log, "Login failed! code: " + code + " error: " + error);
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }

        private void register(String url, String username, String pwd, CallBack callBack) {
            execute(()-> {
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    JSONObject request = new JSONObject();
                    request.putOpt("userAccount", username);
                    request.putOpt("userPassword", pwd);

                    LogUtils.showErrorLog(tv_log,"begin to signUp...");

                    HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                    int code=  response.code;
                    String responseInfo = response.content;
                    if (code == 200) {
                        if (responseInfo != null && responseInfo.length() > 0) {
                            JSONObject object = new JSONObject(responseInfo);
                            String resultCode = object.getString("code");
                            if(resultCode.equals("RES_OK")) {
                                if(callBack != null) {
                                    callBack.onSuccess();
                                }
                            }else{
                                if(callBack != null) {
                                    callBack.onError(Error.GENERAL_ERROR, object.getString("errorInfo"));
                                }
                            }
                        } else {
                            if(callBack != null) {
                                callBack.onError(code, responseInfo);
                            }
                        }
                    } else {
                        if(callBack != null) {
                            callBack.onError(code, responseInfo);
                        }
                    }
                } catch (Exception e) {
                    if(callBack != null) {
                        callBack.onError(Error.GENERAL_ERROR, e.getMessage());
                    }
                }
            });
        }
    //=================== login and register end ========================
        /**
        * Check and request permission
        * @param permission
        * @param requestCode
        * @return
        */
        private boolean checkPermissions(String permission, int requestCode) {
            if(!PermissionsManager.getInstance().hasPermission(this, permission)) {
                PermissionsManager.getInstance().requestPermissions(this, new String[]{permission}, requestCode);
                return false;
            }
            return true;
        }
        /**
        * user met some exception: conflict, removed or forbidden， goto login activity
        */
        protected void onUserException(String exception) {
            LogUtils.showLog(tv_log, "onUserException: " + exception);
            ChatClient.getInstance().logout(false, null);
        }

        public void execute(Runnable runnable) {
            new Thread(runnable).start();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if(connectionListener != null) {
                ChatClient.getInstance().removeConnectionListener(connectionListener);
            }
        }
    }
    ```

2. Add LogUtils and PermissionsManager.

   To make troubleshooting less time-consuming, this quickstart also uses `LogUtils` class for logs. Navigate to `app/java/io.agora.agorachatquickstart/`, create a folder named `utils`. In this new folder, create a `.java` file, name it `LogUtils`, and copy the following codes into the file.

    ```java
    package io.agora.chatuikitquickstart.utils;

    import android.app.Activity;
    import android.text.TextUtils;
    import android.util.Log;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.Locale;

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
            StringBuilder builder = new StringBuilder();
            builder.append(formatCurrentTime())
                    .append(" ")
                    .append(content)
                    .append("\n")
                    .append(preContent);
            tvLog.post(()-> {
                tvLog.setText(builder);
            });
        }

        public static void showErrorToast(Activity activity, TextView tvLog, String content) {
            if(activity == null || activity.isFinishing()) {
                Log.e(TAG, "Context is null...");
                return;
            }
            if(TextUtils.isEmpty(content)) {
                return;
            }
            activity.runOnUiThread(()-> {
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
                showErrorLog(tvLog,content);
            });
        }

        public static void showToast(Activity activity, TextView tvLog, String content) {
            if(TextUtils.isEmpty(content) || activity == null || activity.isFinishing()) {
                return;
            }
            activity.runOnUiThread(()-> {
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
                showNormalLog(tvLog, content);
            });
        }

        private static String formatCurrentTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date());
        }

    }
    ```

    When your app launches, check if the permissions necessary to insert real-time chat into the app are granted. In the `utils` file, create a `.java` file, name it `PermissionsManager`, and copy the following codes into the file.

    ```java
    package io.agora.chatuikitquickstart.utils;

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

3. To send image and file messages, take the following configurations:
 
   Under `/app/src/main/res/`, create a folder, name it `xml`, and create an xml file named `file_paths.xml` under `xml`. Open `file_paths.xml`, replace the code with the following:

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths>
        <external-path path="Android/data/io/agora/chatuikitquickstart/" name="files_root" />
        <external-path path="." name="external_storage_root" />
    </paths>
    ```

    In `/app/Manifests/AndroidManifest.xml`, add the following lines before `</application>`:

    ```xml
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

4. Click `Sync Project with Gradle Files` to sync your project. Now you are ready to test your app.

## Test your app

To validate the peer-to-peer messaging you have just integrated into your app using Agora Chat:

1. In Android Studio, click `Run 'app'`.

   You see the following interface on your simulator or physical device:
   
	 <img src="https://web-cdn.agora.io/docs-files/1652232540196" style="zoom:50%;" />

2. Create a user account and click **SIGN UP**. Click **Sign in** and you will see a log that says Sign in success.

3. Run the app on another Android device or simulator and create another user account. Ensure that the usernames you created are unique.

4. On the first device or simulator, enter the username you just created and click **START CHAT**. You can now start chatting between the two clients.

 <img src="https://web-cdn.agora.io/docs-files/1652232530394" style="zoom:50%;" />
   
## Next Step

For demonstration purposes, Agora Chat provides an app server that enables you to quickly retrieve a token using the App Key given in this guide. In a production context, the best practice is for you to deploy your own token server, use your own [App Key](.enable_agora_chat?platform=Android#get-the-information-of-the-agora-chat-project) to generate a token, and retrieve the token on the client side to log in to Agora. To see how to implement a server that generates and serves tokens on request, see [Generate a User Token](./generate_user_tokens?platform=All%20Platforms).

## Reference

Agora provides the fully featured [AgoraChat-UIKit-Android](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android) demo app as an implementation reference.