package io.agora.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.agora.CallBack;
import io.agora.exceptions.ChatException;


public class MainActivity extends AppCompatActivity {
    private EditText et_username;
    private TextView tv_log;

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
    }

//=================== init SDK start ========================
    private void initSDK() {
        ChatOptions options = new ChatOptions();
        String sdkAppkey = BuildConfig.SDK_APPKEY;
        if(TextUtils.isEmpty(sdkAppkey)) {
            Toast.makeText(MainActivity.this, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        options.setAppKey(sdkAppkey);
        options.setUsingHttpsOnly(true);
        ChatClient.getInstance().init(this, options);
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
            showErrorToast(getString(R.string.username_or_pwd_miss));
            return;
        }
        execute(()-> {
            try {
                ChatClient.getInstance().createAccount(username, pwd);
                showToast(getString(R.string.sign_up_success));
            } catch (ChatException e) {
                e.printStackTrace();
                showErrorLog(e.getDescription());
            }
        });
    }

    /**
     * Login with token
     */
    public void signInWithToken(View view) {
        if(ChatClient.getInstance().isLoggedInBefore()) {
            showErrorLog(getString(R.string.has_login_before));
            return;
        }
        String username = et_username.getText().toString().trim();
        String pwd = ((EditText) findViewById(R.id.et_pwd)).getText().toString().trim();
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            showErrorToast(getString(R.string.username_or_pwd_miss));
            return;
        }
        ChatClient.getInstance().login(username, pwd, new CallBack() {
            @Override
            public void onSuccess() {
                showToast(getString(R.string.sign_in_success));
            }

            @Override
            public void onError(int code, String error) {
                showErrorToast("code: "+code + " error: "+error);
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
                    showToast(getString(R.string.sign_out_success));
                }

                @Override
                public void onError(int code, String error) {
                    showErrorToast("code: "+code + " error: "+error);
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
            showErrorLog(getString(R.string.sign_in_first));
            return;
        }
        EditText et_msg_content = findViewById(R.id.et_msg_content);
        String content = et_msg_content.getText().toString().trim();
        ChatMessage message = ChatMessage.createTxtSendMessage(content, "som");
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                showToast(getString(R.string.send_message_success));
            }

            @Override
            public void onError(int code, String error) {
                showErrorToast("code: "+code + " error: " + error );
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        ChatClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * Send your first image message
     */
    public void sendImageMessage(View view) {

    }

    /**
     * Join your first chat group
     */
    public void joinChatGroup(View view) {

    }

    /**
     * Exit your joined chat group
     */
    public void exitChatGroup(View view) {

    }

    /**
     * Join your first chat room
     */
    public void joinChatRoom(View view) {

    }
//=================== click event end ========================

//=================== utils start ========================

    private void executeUI(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    private void execute(Runnable runnable) {
        new Thread(runnable).start();
    }

    private void showErrorLog(String content) {
        showLog(content);
    }

    private void showNormalLog(String content) {
        showLog(content);
    }

    private void showLog(String content) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        String preContent = tv_log.getText().toString().trim();
        content = content + "\n" + preContent;
        tv_log.setText(content);
    }

    private void showErrorToast(String content) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        executeUI(()-> {
            Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            showErrorLog(content);
        });
    }

    private void showToast(String content) {
        if(TextUtils.isEmpty(content)) {
            return;
        }
        executeUI(()-> {
            Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            showNormalLog(content);
        });
    }
//=================== utils end ========================
}