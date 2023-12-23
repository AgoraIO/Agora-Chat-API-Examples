package io.agora.fcmquickstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.agora.CallBack;
import io.agora.ChatLogListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatOptions;
import io.agora.push.PushConfig;
import io.agora.push.PushHelper;
import io.agora.push.PushListener;
import io.agora.push.PushType;

public class MainActivity extends AppCompatActivity {
    // Create a user from Agora Console or by your app server
    private static final String USERNAME = "";
    // Gets token from Agora Console or generates by your app server
    private static final String TOKEN = "";
    // Gets AppKey from Agora Console
    private static final String APP_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv_log)).setMovementMethod(new ScrollingMovementMethod());
        initSDK();
        initListener();
    }

    private void initSDK() {
        ChatOptions options = new ChatOptions();
        // Get your appkey applied from Agora Console
        if(TextUtils.isEmpty(APP_KEY)) {
            showLog("You should set your AppKey first!", true);
            return;
        }
        // Set your appkey to options
        options.setAppKey(APP_KEY);
        /**
         * NOTE:You need to set up your own account to use the three-way push function, see the integration documentation
         */
        PushConfig.Builder builder = new PushConfig.Builder(this);

        // The FCM sender id should equals with the project_number in google-services.json
        builder.enableFCM("142290967082");
        options.setPushConfig(builder.build());
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
        // Show current user
        ((TextView)findViewById(R.id.tv_username)).setText("Current user: "+USERNAME);

        // Set push listener
        PushHelper.getInstance().setPushListener(new PushListener() {
            @Override
            public void onError(PushType pushType, long errorCode) {
                showLog("Push client occur a error: " + pushType + " - " + errorCode, true);
            }

            @Override
            public boolean isSupportPush(PushType pushType, PushConfig pushConfig) {
                if (pushType == PushType.FCM) {
                    showLog("GooglePlayServiceCode:" + GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this), true);
                    return GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(MainActivity.this) == ConnectionResult.SUCCESS;
                }
                return super.isSupportPush(pushType, pushConfig);
            }
        });
    }

    private void initListener() {
        if(ChatClient.getInstance().isSdkInited()) {
            ChatClient.getInstance().addLogListener(new ChatLogListener() {
                @Override
                public void onLog(String log) {
                    if(log.contains(PushHelper.class.getSimpleName()) || log.contains("ns : CHAT")) {
                        showLog(log, false);
                    }
                }
            });
        }
    }

    /**
     * Login with token
     */
    public void signInWithToken(View view) {
        loginToAgora();
    }

    private void loginToAgora() {
        if(TextUtils.isEmpty(USERNAME) || TextUtils.isEmpty(TOKEN)) {
            showLog("Username or token is empty!", true);
            return;
        }
        showLog("Signing in...", false);
        ChatClient.getInstance().loginWithAgoraToken(USERNAME, TOKEN, new CallBack() {
            @Override
            public void onSuccess() {
                showLog("Sign in success!", true);
            }

            @Override
            public void onError(int code, String error) {
                showLog(error, true);
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
                    showLog("Sign out success!", true);
                }

                @Override
                public void onError(int code, String error) {
                    showLog(error, true);
                }
            });
        }else {
            showLog("You were not logged in", false);
        }
    }

    /**
     * Register FCM token
     */
    public void registerFCM(View view) {
        if(!ChatClient.getInstance().isLoggedIn()) {
            showLog("Please login first!", true);
            return;
        }
        if(GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            // set enable FCM automatic initialization
            if(!FirebaseMessaging.getInstance().isAutoInitEnabled()){
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
            }
            // get FCM token upload
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        showLog("Fetching FCM registration token failed: "+task.getException(), true);
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    ChatClient.getInstance().sendFCMTokenToServer(token);
                }
            });
        }
    }

    private void showLog(String content, boolean showToast) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        runOnUiThread(()-> {
            if(showToast) {
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
            }
            TextView tv_log = findViewById(R.id.tv_log);
            String preContent = tv_log.getText().toString().trim();
            StringBuilder builder = new StringBuilder();
            builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()))
                    .append(" ").append(content).append("\n").append(preContent);
            tv_log.setText(builder);
        });
    }
}