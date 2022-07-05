package io.agora.agorachatquickstart.utils;

import static io.agora.cloud.HttpClientManager.Method_POST;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.agora.CallBack;
import io.agora.agorachatquickstart.R;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatOptions;
import io.agora.cloud.HttpClientManager;
import io.agora.cloud.HttpResponse;

public class AccountHelper {
    private static final String LOGIN_URL = "https://a41.easemob.com/app/chat/user/login";
    private static final String REGISTER_URL = "https://a41.easemob.com/app/chat/user/register";
    public static final String NEW_LOGIN = "NEW_LOGIN";
    public static final String RENEW_TOKEN = "RENEW_TOKEN";

    /**
     * Initialize Agora Chat SDK
     * @param context
     */
    public static void initSDK(Context context) {
        ChatOptions options = new ChatOptions();
        // Set your appkey applied from Agora Console
        String sdkAppkey = context.getString(R.string.app_key);
        if(TextUtils.isEmpty(sdkAppkey)) {
            Toast.makeText(context, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set your appkey to options
        options.setAppKey(sdkAppkey);
        // Set you to use HTTPS only
        options.setUsingHttpsOnly(true);
        options.setAutoLogin(false);
        // To initialize Agora Chat SDK
        ChatClient.getInstance().init(context, options);
        // Make Agora Chat SDK debuggable
        ChatClient.getInstance().setDebugMode(true);
    }

    /**
     * Sign in
     * @param context
     * @param tv_log
     * @param username
     * @param pwd
     */
    public static void signIn(Activity context, TextView tv_log, String username, String pwd) {
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            LogUtils.showErrorToast(context, tv_log, context.getString(R.string.username_or_pwd_miss));
            return;
        }
        signOut(context, tv_log, new CallBack() {
            @Override
            public void onSuccess() {
                getTokenFromServer(context, tv_log, username, pwd, NEW_LOGIN);
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * Get Agora Chat token, login in or renew token.
     * @param context
     * @param tv_log
     * @param username
     * @param pwd
     * @param requestType see {@link #RENEW_TOKEN} and {@link #NEW_LOGIN}
     */
    public static void getTokenFromServer(Activity context, TextView tv_log, String username, String pwd, String requestType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                            if(TextUtils.equals(requestType, NEW_LOGIN)) {
                                ChatClient.getInstance().loginWithAgoraToken(username, token, new CallBack() {
                                    @Override
                                    public void onSuccess() {
                                        LogUtils.showToast(context, tv_log, context.getString(R.string.sign_in_success));
                                    }

                                    @Override
                                    public void onError(int code, String error) {
                                        LogUtils.showErrorToast(context, tv_log, "Login failed! code: " + code + " error: " + error);
                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }
                                });
                            }else if(TextUtils.equals(requestType, RENEW_TOKEN)) {
                                ChatClient.getInstance().renewToken(token);
                            }

                        } else {
                            LogUtils.showErrorToast(context, tv_log, "getTokenFromAppServer failed! code: " + code + " error: " + responseInfo);
                        }
                    } else {
                        LogUtils.showErrorToast(context, tv_log, "getTokenFromAppServer failed! code: " + code + " error: " + responseInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.showErrorToast(context, tv_log, "getTokenFromAppServer failed! code: " + 0 + " error: " + e.getMessage());

                }
            }
        }).start();
    }

    /**
     * Sign up
     * @param context
     * @param tv_log
     * @param username
     * @param pwd
     */
    public static void signUp(Activity context, TextView tv_log, String username, String pwd) {
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            LogUtils.showErrorToast(context, tv_log, context.getString(R.string.username_or_pwd_miss));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                                LogUtils.showToast(context, tv_log, context.getString(R.string.sign_up_success));
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
            }
        }).start();
    }

    /**
     * Sign out
     * @param context
     * @param tv_log
     * @param callBack
     */
    public static void signOut(Activity context, TextView tv_log, CallBack callBack) {
        if(ChatClient.getInstance().isLoggedInBefore()) {
            LogUtils.showLog(tv_log, "Login before, start to sign out...");
            ChatClient.getInstance().logout(true, new CallBack() {
                @Override
                public void onSuccess() {
                    LogUtils.showToast(context, tv_log, context.getString(R.string.sign_out_success));
                    if(callBack != null) {
                        callBack.onSuccess();
                    }
                }

                @Override
                public void onError(int code, String error) {
                    LogUtils.showErrorToast(context, tv_log, "Sign out failed! code: "+code + " error: "+error);
                    if(callBack != null) {
                        callBack.onError(code, error);
                    }
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }else {
            if(callBack != null) {
                callBack.onSuccess();
            }
        }
    }
}
