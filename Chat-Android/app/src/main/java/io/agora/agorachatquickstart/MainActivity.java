package io.agora.agorachatquickstart;

import static io.agora.cloud.HttpClientManager.Method_POST;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.agora.CallBack;
import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.TextMessageBody;
import io.agora.cloud.HttpClientManager;
import io.agora.cloud.HttpResponse;


public class MainActivity extends AppCompatActivity {
    private EditText et_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSDK();
        initListener();
    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        ((TextView)findViewById(R.id.tv_log)).setMovementMethod(new ScrollingMovementMethod());
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
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
    }
//=================== init SDK end ========================
//================= SDK listener start ====================
    private void initListener() {
        ChatClient.getInstance().chatManager().addMessageListener(messages -> {
            for(ChatMessage message : messages) {
                StringBuilder builder = new StringBuilder();
                builder.append("Receive a ").append(message.getType().name())
                        .append(" message from: ").append(message.getFrom());
                if(message.getType() == ChatMessage.Type.TXT) {
                    builder.append(" content:")
                            .append(((TextMessageBody)message.getBody()).getMessage());
                }
                showLog(builder.toString(), false);
            }
        });
        ChatClient.getInstance().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected() {
                showLog("onConnected",false);
            }

            @Override
            public void onDisconnected(int error) {
                showLog("onDisconnected: "+error,false);
            }

            @Override
            public void onLogout(int errorCode) {
                showLog("User needs to log out: "+errorCode, false);
                ChatClient.getInstance().logout(false, null);
            }

            @Override
            public void onTokenExpired() {
                showLog("ConnectionListener onTokenExpired", true);
                signInWithToken(null);
            }

            @Override
            public void onTokenWillExpire() {
                showLog("ConnectionListener onTokenWillExpire", true);
                getTokenFromAppServer(true);
            }
        });
    }
//================= SDK listener end ====================
//=================== click event start ========================
    /**
     * Sign up with username and password
     */
    public void signUp(View view) {
        String username = et_username.getText().toString().trim();
        String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
        register(username, pwd, new CallBack() {
            @Override
            public void onSuccess() {
                showLog("Sign up success!", true);
            }

            @Override
            public void onError(int code, String error) {
                showLog(error, true);
            }
        });
    }

    /**
     * Login with token
     */
    public void signInWithToken(View view) {
        getTokenFromAppServer(false);
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
        }
    }

    /**
     * Send your first message
     */
    public void sendFirstMessage(View view) {
        String toSendName = ((TextView)findViewById(R.id.et_to_chat_name)).getText().toString().trim();
        String content = ((TextView)findViewById(R.id.et_msg_content)).getText().toString().trim();
        // Create a text message
        ChatMessage message = ChatMessage.createTextSendMessage(content, toSendName);
        // Check if the message is null
        if(message == null) {
            showLog("Message is null!", true);
            return;
        }
        // Set the message callback before sending the message
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                showLog("Send message success!", true);
            }

            @Override
            public void onError(int code, String error) {
                showLog(error, true);
            }
        });
        // Send the message
        ChatClient.getInstance().chatManager().sendMessage(message);
    }

//=================== click event end ========================
//=================== get token from server start ========================

    private void getTokenFromAppServer(boolean isRenewToken) {
        if(ChatClient.getInstance().isLoggedInBefore()) {
            showLog("An account has been signed in, please sign out first and then sign in", false);
            return;
        }
        String username = et_username.getText().toString().trim();
        String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
        getAgoraTokenFromAppServer(username, pwd, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String token) {
                if(isRenewToken) {
                    ChatClient.getInstance().renewToken(token);
                }else {
                    login(username,token);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                showLog(errorMsg, true);
            }
        });
    }

    private void getAgoraTokenFromAppServer(String username, String pwd, @NonNull ValueCallBack<String> callBack) {
        showLog("begin to getTokenFromAppServer ...", false);
        executeRequest(getString(R.string.login_url), username, pwd, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String token = object.getString("accessToken");
                    callBack.onSuccess(token);
                } catch (JSONException e) {
                    callBack.onError(Error.GENERAL_ERROR, e.getMessage());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                callBack.onError(error, errorMsg);
            }
        });
    }

    private void executeRequest(String url, String username, String password, @NonNull ValueCallBack<String> callBack) {
        if(TextUtils.isEmpty(url)) {
            callBack.onError(Error.INVALID_PARAM, "Request url should not be null");
            return;
        }
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            callBack.onError(Error.INVALID_PARAM, "Username or password is empty");
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        try {
            request.putOpt("userAccount", username);
            request.putOpt("userPassword", password);
        } catch (JSONException e) {
            callBack.onError(Error.GENERAL_ERROR, e.getMessage());
            return;
        }
        execute(()-> {
            try {
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        callBack.onSuccess(responseInfo);
                    } else {
                        callBack.onError(Error.SERVER_UNKNOWN_ERROR, responseInfo);
                    }
                } else {
                    callBack.onError(code, responseInfo);
                }
            } catch (Exception e) {
                callBack.onError(Error.GENERAL_ERROR, e.getMessage());
            }
        });
    }

//=================== get token from server end ========================
//=================== login and register start ========================

    private void login(String username, String token) {
        ChatClient.getInstance().loginWithAgoraToken(username, token, new CallBack() {
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

    private void register(String username, String pwd, @NonNull CallBack callBack) {
        showLog("begin to sign up...",false);
        executeRequest(getString(R.string.register_url), username, pwd, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String response) {
                String resultCode = null;
                try {
                    JSONObject object = new JSONObject(response);
                    resultCode = object.getString("code");
                    if(resultCode.equals("RES_OK")) {
                        callBack.onSuccess();
                    }else{
                        callBack.onError(Error.GENERAL_ERROR, "Sign up failed!");
                    }
                } catch (JSONException e) {
                    callBack.onError(Error.GENERAL_ERROR, e.getMessage());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                callBack.onError(error, errorMsg);
            }
        });
    }

//=================== login and register start ========================

    private void execute(Runnable runnable) {
        new Thread(runnable).start();
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