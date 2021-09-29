package io.agora.chat;

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

import io.agora.CallBack;
import io.agora.chat.utils.ImageUtils;
import io.agora.chat.utils.LogUtils;
import io.agora.chat.utils.PermissionsManager;
import io.agora.chat.utils.ThreadManager;
import io.agora.exceptions.ChatException;


public class MainActivity extends AppCompatActivity {
    private EditText et_username;
    private TextView tv_log;
    private EditText et_to_chat_name;
    private EditText et_group_id;

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
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(this, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
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

//=================== click event end ========================
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

    private void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                sendImageMessage(selectedImage);
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