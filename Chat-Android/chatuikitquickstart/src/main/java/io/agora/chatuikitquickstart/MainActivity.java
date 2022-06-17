package io.agora.chatuikitquickstart;

import static io.agora.cloud.HttpClientManager.Method_POST;

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
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSendCallBack;
import io.agora.chatuikitquickstart.utils.LogUtils;
import io.agora.chatuikitquickstart.utils.PermissionsManager;
import io.agora.cloud.HttpClientManager;
import io.agora.cloud.HttpResponse;


public class MainActivity extends AppCompatActivity {
    private static final String NEW_LOGIN = "NEW_LOGIN";
    private static final String RENEW_TOKEN = "RENEW_TOKEN";
    private static final String LOGIN_URL = "https://a41.easemob.com/app/chat/user/login";
    private static final String REGISTER_URL = "https://a41.easemob.com/app/chat/user/register";
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
        };
        // Call removeConnectionListener(connectionListener) when the activity is destroyed
        ChatClient.getInstance().addConnectionListener(connectionListener);
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

    public void startChat(View view) {
        EditText et_to_username = findViewById(R.id.et_to_username);
        String toChatUsername = et_to_username.getText().toString().trim();
        // check username
        if(TextUtils.isEmpty(toChatUsername)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.not_find_send_name));
            return;
        }
        // 1: single chat; 2: group chat; 3: chat room
        EaseChatFragment fragment = new EaseChatFragment.Builder(toChatUsername, 1)
                .useHeader(false)
                .setOnChatExtendMenuItemClickListener(new OnChatExtendMenuItemClickListener() {
                    @Override
                    public boolean onChatExtendMenuItemClick(View view, int itemId) {
                        if(itemId == R.id.extend_item_take_picture) {
                            return !checkPermissions(Manifest.permission.CAMERA, 111);
                        }else if(itemId == R.id.extend_item_picture || itemId == R.id.extend_item_file || itemId == R.id.extend_item_video) {
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