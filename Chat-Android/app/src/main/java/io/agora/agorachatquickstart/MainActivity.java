package io.agora.agorachatquickstart;

import static io.agora.cloud.HttpClientManager.Method_POST;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.CallBack;
import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.MessageListener;
import io.agora.agorachatquickstart.utils.ImageUtils;
import io.agora.agorachatquickstart.utils.PermissionsManager;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.TextMessageBody;
import io.agora.agorachatquickstart.utils.LogUtils;
import io.agora.cloud.HttpClientManager;
import io.agora.cloud.HttpResponse;
import io.agora.util.EMLog;


public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static final String NEW_LOGIN = "NEW_LOGIN";
    private static final String RENEW_TOKEN = "RENEW_TOKEN";
    private static final String LOGIN_URL = "https://a41.easemob.com/app/chat/user/login";
    private static final String REGISTER_URL = "https://a41.easemob.com/app/chat/user/register";
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
        addMessageListener();
        addConnectionListener();
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
//================= SDK listener start ====================
    private void addMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(new MessageListener() {
            @Override
            public void onMessageReceived(List<ChatMessage> messages) {
                parseMessage(messages);
            }

            @Override
            public void onCmdMessageReceived(List<ChatMessage> messages) {
                LogUtils.showLog(tv_log, "onCmdMessageReceived");
            }

            @Override
            public void onMessageRead(List<ChatMessage> messages) {
                LogUtils.showLog(tv_log, "onMessageRead");
            }

            @Override
            public void onMessageDelivered(List<ChatMessage> messages) {
                LogUtils.showLog(tv_log, "onMessageDelivered");
            }

            @Override
            public void onMessageRecalled(List<ChatMessage> messages) {
                LogUtils.showLog(tv_log, "onMessageRecalled");
            }

            @Deprecated
            @Override
            public void onMessageChanged(ChatMessage message, Object change) {
                LogUtils.showLog(tv_log, "onMessageChanged");
            }
        });
    }

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
    private void addConnectionListener() {
        ChatClient.getInstance().addConnectionListener(new ConnectionListener(){
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected(int error) {
                if (error == Error.USER_REMOVED) {
                    onUserException("account_removed");
                } else if (error == Error.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException("account_conflict");
                } else if (error == Error.SERVER_SERVICE_RESTRICTED) {
                    onUserException("account_forbidden");
                } else if (error == Error.USER_KICKED_BY_CHANGE_PASSWORD) {
                    onUserException("account_kicked_by_change_password");
                } else if (error == Error.USER_KICKED_BY_OTHER_DEVICE) {
                    onUserException("account_kicked_by_other_device");
                } else if(error == Error.USER_BIND_ANOTHER_DEVICE) {
                    onUserException("user_bind_another_device");
                } else if(error == Error.USER_DEVICE_CHANGED) {
                    onUserException("user_device_changed");
                } else if(error == Error.USER_LOGIN_TOO_MANY_DEVICES) {
                    onUserException("user_login_too_many_devices");
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
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.username_or_pwd_miss));
            return;
        }
        execute(()-> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                JSONObject request = new JSONObject();
                request.putOpt("userAccount", username);
                request.putOpt("userPassword", pwd);

                LogUtils.showErrorLog(tv_log,"begin to signUp...");

                HttpResponse response = HttpClientManager.httpExecute(REGISTER_URL, headers, request.toString(), Method_POST);
                int code=  response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        String resultCode = object.getString("code");
                        if(resultCode.equals("RES_OK")) {
                            LogUtils.showToast(MainActivity.this, tv_log, getString(R.string.sign_up_success));
                        }else{
                            String errorInfo = object.getString("errorInfo");
                            LogUtils.showErrorLog(tv_log,errorInfo);
                        }
                    } else {
                        LogUtils.showErrorLog(tv_log,responseInfo);
                    }
                } else {
                    LogUtils.showErrorLog(tv_log,responseInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.showErrorLog(tv_log, e.getMessage());
            }
        });
    }

    /**
     * Login with token
     */
    public void signInWithToken(View view) {
        getTokenFromAppServer(NEW_LOGIN);
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
//=================== get token from server start ========================

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
        getAndParseToken(username,pwd,requestType);
    }

    private void getAndParseToken(String username,String pwd,String requestType){
        execute(()-> {
            try {
                HttpResponse response=getToken(username,pwd);
                parseResponse(response,username,requestType);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.showErrorToast(MainActivity.this, tv_log, "getTokenFromAppServer failed! code: " + 0 + " error: " + e.getMessage());
            }
        });
    }

    private HttpResponse getToken(String username,String pwd) throws Exception{
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        request.putOpt("userAccount", username);
        request.putOpt("userPassword", pwd);

        LogUtils.showErrorLog(tv_log,"begin to getTokenFromAppServer ...");

        HttpResponse response = HttpClientManager.httpExecute(LOGIN_URL, headers, request.toString(), Method_POST);

        return response;
    }

    private void parseResponse(HttpResponse response,String username ,String requestType) throws Exception{
        int code = response.code;
        String responseInfo = response.content;
        if (code == 200) {
            if (responseInfo != null && responseInfo.length() > 0) {
                JSONObject object = new JSONObject(responseInfo);
                String token = object.getString("accessToken");
                if(TextUtils.equals(requestType, NEW_LOGIN)) {
                    login(username,token);
                }else if(TextUtils.equals(requestType, RENEW_TOKEN)) {
                    ChatClient.getInstance().renewToken(token);
                }
            } else {
                LogUtils.showErrorToast(MainActivity.this, tv_log, "getTokenFromAppServer failed! code: " + code + " error: " + responseInfo);
            }
        } else {
            LogUtils.showErrorToast(MainActivity.this, tv_log, "getTokenFromAppServer failed! code: " + code + " error: " + responseInfo);
        }
    }

    private void login(String username, String token) {
        ChatClient.getInstance().loginWithAgoraToken(username, token, new CallBack() {
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
//=================== get token from server end ========================

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

    /**
     * user met some exception: conflict, removed or forbidden， goto login activity
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        ChatClient.getInstance().logout(false, null);
    }

    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}