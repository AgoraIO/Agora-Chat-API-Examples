package io.agora.agorachatquickstart;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import io.agora.CallBack;
import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.TextMessageBody;


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
        initView();
        initSDK();
        initListener();
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_log)).setMovementMethod(new ScrollingMovementMethod());
    }

    private void initSDK() {
        ChatOptions options = new ChatOptions();
        // Get your appkey applied from Agora Console
        if(TextUtils.isEmpty(APP_KEY)) {
            Toast.makeText(MainActivity.this, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set your appkey to options
        options.setAppKey(APP_KEY);
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
        // Show current user
        ((TextView)findViewById(R.id.tv_username)).setText("Current user: "+USERNAME);
    }

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
            }

            @Override
            public void onTokenWillExpire() {
                showLog("ConnectionListener onTokenWillExpire", true);
            }
        });
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
     * Send your first message
     */
    public void sendFirstMessage(View view) {
        String toSendName = ((TextView)findViewById(R.id.et_to_chat_name)).getText().toString().trim();
        String content = ((TextView)findViewById(R.id.et_msg_content)).getText().toString().trim();
        // Create a text message
        ChatMessage message = ChatMessage.createTextSendMessage(content, toSendName);
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