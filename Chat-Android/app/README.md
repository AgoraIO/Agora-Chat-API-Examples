Instant messaging connects people wherever they are and allows them to communicate with others in real time. The Agora Chat SDK enables you to embed real-time messaging in any app, on any device, anywhere.

This page shows a sample code to add peer-to-peer messaging into your app by using the Agora Chat SDK for Android.

## Understand the tech

The following figure shows the workflow of how clients send and receive peer-to-peer messages.

![](https://web-cdn.agora.io/docs-files/1636443945728)

As shown in the figure, the workflow of peer-to-peer messaging is as follows:

1. The clients retrieve a token from your app server.
2. Client A and Client B sign in to Agora Chat.
3. Client A sends a message to Client B.
4. The message is sent to the Agora Chat server and the server delivers the message to Client B.
5. When Client B receives the message, the SDK triggers an event. Client B listens for the event and gets the message.

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

    <div class="alert note">The way to add the Maven Central dependency can be different if you set  <a href="https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration">dependencyResolutionManagement</a> in your Android project.</div>

   b. In `/Gradle Scripts/build.gradle(Module: <projectname>.app)`, add the following lines to integrate the Agora Chat SDK into your Android project:

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
       implementation 'io.agora.rtc:chat-sdk:X.Y.Z'
   }
   ```

   <div class="alert note"><ul><li><code>minSdkVersion</code> must be 21 or higher for the build process to succeed.</li><li>For the latest SDK version, go to <a href="https://search.maven.org/search?q=a:chat-sdk">Sonatype</a></li></ul>.</div>

3. Add permissions for network and device access.

   In `/app/Manifests/AndroidManifest.xml`, add the following permissions after `</application>`:

   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   <uses-permission android:name="android.permission.WAKE_LOCK"/>
   ```

   These are the minimum permissions you need to add to start Agora Chat. You can also add other permissions according to your use case.

4. Prevent code obfuscation.

   In `/Gradle Scripts/proguard-rules.pro`, add the following line:

   ```java
   -keep class io.agora.** {*;}
   -dontwarn  io.agora.**
   ```


## Implement peer-to-peer messaging

This section shows how to use the Agora Chat SDK to implement peer-to-peer messaging in your app step by step.

### Create the UI

1. To add the text strings used by the UI, open `app/res/values/strings.xml ` and  replace the content with the following codes:

   ```xml
   <resources>
       <string name="app_name">AgoraChatQuickstart</string>
   
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
   
       <string name="app_key">41117440#383391</string>
   </resources>
   ``` 
    <div class="alert note">The App Key provided here is for testing purposes only. In a production environment, you need to use the App Key for your Agora project.</div>

2. To add the UI framework, open  `app/res/layout/activity_main.xml` and replace the content with the following codes:

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
                   android:inputType="textPassword"
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
                   android:layout_marginStart="10dp"
                   android:layout_marginEnd="5dp"
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
                   android:layout_marginStart="5dp"
                   android:layout_marginEnd="5dp"
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
                   android:layout_marginStart="5dp"
                   android:layout_marginEnd="10dp"/>
   
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
                   app:layout_constraintRight_toRightOf="parent"
                   app:layout_constraintTop_toBottomOf="@id/et_to_chat_name"
                   android:layout_marginStart="20dp"
                   android:layout_marginEnd="20dp"
                   android:layout_marginTop="10dp"/>
   
               <Button
                   android:id="@+id/btn_send_message"
                   android:layout_width="0dp"
                   android:layout_height="wrap_content"
                   android:text="@string/send_message"
                   android:onClick="sendFirstMessage"
                   app:layout_constraintLeft_toLeftOf="parent"
                   app:layout_constraintRight_toRightOf="parent"
                   app:layout_constraintTop_toBottomOf="@id/et_msg_content"
                   android:layout_marginStart="10dp"
                   android:layout_marginEnd="10dp"
                   android:layout_marginTop="20dp"/>
   
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

### Implement the sending and receiving of messages

To enable your app to send and receive messages between individual users, do the following:

1. Import classes. In  `app/java/io.agora.agorachatquickstart/MainActivity`, add the following lines after `import android.os.Bundle;` :

   ```java
   import android.text.TextUtils;
   import android.text.method.ScrollingMovementMethod;
   import android.view.View;
   import android.widget.EditText;
   import android.widget.TextView;
   import android.widget.Toast;
   
   import org.json.JSONObject;
   
   import java.util.HashMap;
   import java.util.List;
   import java.util.Map;
   
   import static io.agora.cloud.HttpClientManager.Method_POST;
   import io.agora.CallBack;
   import io.agora.ConnectionListener;
   import io.agora.Error;
   import io.agora.MessageListener;
   import io.agora.agorachatquickstart.utils.LogUtils;
   import io.agora.chat.ChatClient;
   import io.agora.chat.ChatMessage;
   import io.agora.chat.ChatOptions;
   import io.agora.chat.TextMessageBody;
   import io.agora.cloud.HttpClientManager;
   import io.agora.cloud.HttpResponse;
   import io.agora.util.EMLog;
   ```

2. Define global variables. In `app/java/io.agora.agorachatquickstart/MainActivity`,  before adding the following lines after `AppCompatActivity {`, ensure you delete the `onCreate` funtion created by default.

   ```java
   private final String TAG = getClass().getSimpleName();
   private static final String NEW_LOGIN = "NEW_LOGIN";
   private static final String RENEW_TOKEN = "RENEW_TOKEN";
   private static final String ACCOUNT_REMOVED = "account_removed";
   private static final String ACCOUNT_CONFLICT = "conflict";
   private static final String ACCOUNT_FORBIDDEN = "user_forbidden";
   private static final String ACCOUNT_KICKED_BY_CHANGE_PASSWORD = "kicked_by_change_password";
   private static final String ACCOUNT_KICKED_BY_OTHER_DEVICE = "kicked_by_another_device";
   private static final String LOGIN_URL = "https://a41.easemob.com/app/chat/user/login";
   private static final String REGISTER_URL = "https://a41.easemob.com/app/chat/user/register";
   private EditText et_username;
   private TextView tv_log;
   private EditText et_to_chat_name;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       // Add methods for initialization and listen for message and connection events.
       initView();
       initSDK();
       addMessageListener();
       addConnectionListener();
   }
   ```

3. Initialize the view and the app. In `app/java/io.agora.agorachatquickstart/MainActivity`, add the following lines after the `onCreate` function:

   ```java
   // Initialize the view.
   private void initView() {
        et_username = findViewById(R.id.et_username);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        et_to_chat_name = findViewById(R.id.et_to_chat_name);
    }
    // Initialize the SDK.
    private void initSDK() {
        ChatOptions options = new ChatOptions();
        // Set the appkey you obtained from Agora Console.
        String sdkAppkey = getString(R.string.app_key);
        if(TextUtils.isEmpty(sdkAppkey)) {
            Toast.makeText(MainActivity.this, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set your appkey to options.
        options.setAppKey(sdkAppkey);
        // Set you to use HTTPS only.
        options.setUsingHttpsOnly(true);
        // To initialize Agora Chat SDK.
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable.
        ChatClient.getInstance().setDebugMode(true);
    }
   ```

4. Retrieve a token. To get a token from the app server, add the following lines after the `initSDK` function:

   ```java
   
   private void getTokenFromAppServer(String requestType) {
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
    // Retrieve a token from the app server.
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
   ```

5. Add event callbacks. In `app/java/io.agora.agorachatquickstart/MainActivity`, add the following lines after the `getTokenFromAppServer` function:

   ```java
   // Add message events callbacks. 
   private void addMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(new MessageListener() {
            @Override
            public void onMessageReceived(List<ChatMessage> messages) {
                parseMessage(messages);
            }
        });
    }
   // Show message logs.
   private void parseMessage(List<ChatMessage> messages) {
           if(messages != null && !messages.isEmpty()) {
               for(ChatMessage message : messages) {
                   ChatMessage.Type type = message.getType();
                   String from = message.getFrom();
                   StringBuilder builder = new StringBuilder();
                   builder.append("Receive a ")
                           .append(type.name())
                           .append(" message from: ")
                           .append(from);
                   if(type == ChatMessage.Type.TXT) {
                       builder.append(" content:")
                               .append(((TextMessageBody)message.getBody()).getMessage());
                   }
                   LogUtils.showLog(tv_log, builder.toString());
               }
           }
       }
   // Add connection events callbacks.
   private void addConnectionListener() {
        ChatClient.getInstance().addConnectionListener(new ConnectionListener(){
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

            protected void onUserException(String exception) {
                EMLog.e(TAG, "onUserException: " + exception);
                ChatClient.getInstance().logout(false, null);
            }

            // This callback occurs when the token expires. When the callback is triggered, the app client must get a new token from the app server and logs in to the app again.
            @Override
            public void onTokenExpired() {
                //login again
                signInWithToken(null);
                LogUtils.showLog(tv_log,"ConnectionListener onTokenExpired");
            }
            // This callback occurs when the token is to expire. 
            @Override
            public void onTokenWillExpire() {
                getTokenFromAppServer(RENEW_TOKEN);
                LogUtils.showLog(tv_log, "ConnectionListener onTokenWillExpire");
            }
        });
    }
   
   ```

6. Create a user account, log in to the app. To implement this logic, in `app/java/io.agora.agorachatquickstart/MainActivity`, add the following lines after the `addConnectionListener` function:

   ```java
   // Sign up with a username and password.
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
   
   // Log in with Token.
   public void signInWithToken(View view) {
       getTokenFromAppServer(NEW_LOGIN);
   }
   
   // Sign out.
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
   ```

7. Start a chat. To enable the function of sending messages, add the following lines after the `signOut` function:

   ```java
   // Send your first message.
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
   
       // Create a text message.
       ChatMessage message = ChatMessage.createTxtSendMessage(content, toSendName);
       sendMessage(message);
   
   }
   private void sendMessage(ChatMessage message) {
       // Check if the message is null.
       if(message == null) {
           LogUtils.showErrorToast(this, tv_log, getString(R.string.message_is_null));
           return;
       }
       // Set the message callback before sending the message.
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
       // Send the message.
       ChatClient.getInstance().chatManager().sendMessage(message);
   }
   
   public void execute(Runnable runnable) {
       new Thread(runnable).start();
   }
    
   ```

8. To make troubleshooting less time-consuming, this quickstart also uses `LogUtils` class for logs. Navigate to `app/java/io.agora.agorachatquickstart/`, create a folder named `utils`. In this new folder, create a `.java` file, name it `LogUtils`, and copy the following codes into the file.

   ```java
   package io.agora.agorachatquickstart.utils;
   
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

9. Click `Sync Project with Gradle Files` to sync your project. Now you are ready to test your app.

## Test your app

To validate the peer-to-peer messaging you have just integrated into your app using Agora Chat:

1. In Android Studio, click `Run 'app'`.

   You see the following interface on your simulator or physical device:
   <img src="https://web-cdn.agora.io/docs-files/1637661621212" style="zoom:50%;" />

2. Create a user account and click **SIGN UP**.

3. Sign in with the user account you just created and send a message.
   <img src="https://web-cdn.agora.io/docs-files/1637562675862" style="zoom:50%;" />

4. Run the app on another Android device or simulator and create another user account. Ensure that the usernames you created are unique.

5. Send messages between the users.
   <img src="https://web-cdn.agora.io/docs-files/1637562770527" style="zoom:50%;" />

## Next Step

In a production context, the best practice is for your app to retrieve the token used to log in to Agora. To see how to implement a server that generates and serves tokens on request, see [Token](./agora_chat_user_token).

## See also

In addition to integrating the Agora Chat SDK into your project with mavenCentral, you can also manually download the [Agora Chat SDK for Android](https://download.agora.io/sdk/release/Agora_Chat_SDK_for_Android_v1.0.0.zip).

1. Download the latest version of the Agora Chat SDK for Android, and extract the files from the downloaded SDK package.

2. Copy the following files or subfolders from the **libs** folder of the downloaded SDK to the corresponding directory of your project.

   | File or subfolder                                      | Path of your project                  |
      | ------------------------------------------------------ | ------------------------------------- |
   | `agorachat_X.Y.Z.jar`                                  | `~/app/libs/`                         |
   | `/arm64-v8a/libagora-chat-sdk.so` and `libsqlite.so`   | `~/app/src/main/jniLibs/arm64-v8a/`   |
   | `/armeabi-v7a/libagora-chat-sdk.so` and `libsqlite.so` | `~/app/src/main/jniLibs/armeabi-v7a/` |
   | `/x86/libagora-chat-sdk.so` and `libsqlite.so`         | `~/app/src/main/jniLibs/x86/`         |
   | `/x86_64/libagora-chat-sdk.so` and `libsqlite.so`      | `~/app/src/main/jniLibs/x86_64/`      |

   <div class="alert info"> X.Y.Z refers to the version number of the Agora Chat SDK you downloaded.</div>
