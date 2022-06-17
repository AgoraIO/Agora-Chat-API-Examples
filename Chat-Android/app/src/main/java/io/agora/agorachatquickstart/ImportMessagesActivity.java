package io.agora.agorachatquickstart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.MessageBody;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.agorachatquickstart.utils.AccountHelper;
import io.agora.agorachatquickstart.utils.LogUtils;

public class ImportMessagesActivity extends AppCompatActivity implements ConnectionListener {
    private Activity mContext;
    private TextView tv_log;
    private Button btnSignIn;
    private Button btnSignUp;
    private EditText etUsername;
    private EditText etPwd;
    private Button btnImportMessage;
    private Button btnShowMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_emssages);
        mContext = this;
        AccountHelper.initSDK(this);
        initView();
        initListener();
    }

    private void initView() {
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        btnImportMessage = findViewById(R.id.btn_import_message);
        btnShowMessage = findViewById(R.id.btn_show_message);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        // Register Agora Chat connect listener
        ChatClient.getInstance().addConnectionListener(this);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHelper.signIn(mContext, tv_log,
                        etUsername.getText().toString().trim(), etPwd.getText().toString().trim());
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHelper.signUp(mContext, tv_log,
                        etUsername.getText().toString().trim(), etPwd.getText().toString().trim());
            }
        });
        btnImportMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if logged in before
                if(!ChatClient.getInstance().isLoggedIn()) {
                    LogUtils.showErrorToast(mContext, tv_log, getString(R.string.sign_in_first));
                    return;
                }
                LogUtils.showLog(tv_log, getString(R.string.start_to_import));
                String json = loadStreamFromLocal();
                if(TextUtils.isEmpty(json)) {
                    LogUtils.showErrorToast(mContext, tv_log, getString(R.string.no_local_data));
                    return;
                }
                parseJsonToMessage(json);
            }
        });
        btnShowMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalMessages();
            }
        });
    }

    /**
     * Get messages from local db
     */
    private void getLocalMessages() {
        LogUtils.showLog(tv_log, getString(R.string.get_conversations_from_db));
        Map<String, Conversation> conversations = ChatClient.getInstance().chatManager().getAllConversations();
        if(conversations.size() <= 0) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.no_local_conversation));
            return;
        }
        Iterator<Map.Entry<String, Conversation>> iterator = conversations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Conversation> next = iterator.next();
            Conversation conversation = next.getValue();
            LogUtils.showLog(tv_log, getString(R.string.show_conversation, conversation.conversationId(), conversation.getType().name()));
            List<ChatMessage> allMessages = conversation.getAllMessages();
            for(int i = 0; i < allMessages.size(); i++) {
                ChatMessage message = allMessages.get(i);
                LogUtils.showLog(tv_log, getString(R.string.show_message, i, message.conversationId(),
                        message.getMsgId(), message.getType().name(), message.getFrom(), message.getTo()));
            }
        }
    }

    /**
     * Parse local data to Agora Chat messages
     * @param json
     */
    private void parseJsonToMessage(String json) {
        try {
            JSONArray array = new JSONArray(json);
            if(array.length() <= 0) {
                LogUtils.showErrorToast(mContext, tv_log, getString(R.string.local_data_is_substandard));
                return;
            }
            btnShowMessage.setVisibility(View.VISIBLE);
            for(int i = 0; i < array.length(); i++) {
                JSONObject channelObject = (JSONObject) array.get(i);
                JSONArray messages = channelObject.getJSONArray("messages");
                if(messages == null || messages.length() <= 0) {
                    continue;
                }
                String conversationId = channelObject.getString("channelUrl");
                for(int j = 0; j < messages.length(); j++) {
                    JSONObject object = messages.getJSONObject(j);
                    String type = object.getString("type");
                    if(TextUtils.isEmpty(type)) {
                        continue;
                    }
                    ChatMessage message = null;
                    String senderId = object.getString("sender");
                    long timestamp = object.getLong("createdAt");
                    String messageId = object.getString("messageId");
                    ChatMessage.Status status = translateStatus(object.getInt("sendingStatus"));
                    MessageBody body = null;
                    if(TextUtils.equals(type, "MessageTypeText")) {
                        String text = object.getString("message");
                        body = new TextMessageBody(text);
                    }else if(TextUtils.equals(type, "MessageTypeImage")) {
                        Uri imageUri = null;
                        ImageMessageBody imageMessageBody = new ImageMessageBody(imageUri);
                        imageMessageBody.setFileLength(object.getLong("size"));
                        if(object.has("localUrl")) {
                            imageMessageBody.setLocalUrl(object.getString("localUrl"));
                        }
                        imageMessageBody.setRemoteUrl(object.getString("remoteUrl"));
                        imageMessageBody.setFileName(object.getString("fileName"));
                        if(object.has("thumbnailLocalUrl")) {
                            imageMessageBody.setThumbnailLocalPath(object.getString("thumbnailLocalUrl"));
                        }
                        if(object.has("thumbnailRemoteUrl")) {
                            imageMessageBody.setThumbnailUrl(object.getString("thumbnailRemoteUrl"));
                        }
                        body = imageMessageBody;
                    }else if(TextUtils.equals(type, "MessageTypeFile")) {
                        Uri fileUri = Uri.parse("");
                        NormalFileMessageBody fileBody = new NormalFileMessageBody(fileUri);
                        fileBody.setFileLength(object.getLong("size"));
                        if(object.has("localUrl")) {
                            fileBody.setLocalUrl(object.getString("localUrl"));
                        }
                        fileBody.setRemoteUrl(object.getString("remoteUrl"));
                        fileBody.setFileName(object.getString("fileName"));
                        body = fileBody;
                    }
                    message = ChatMessage.createReceiveMessage(null);
                    message.setMsgTime(timestamp);
                    message.setMsgId(messageId);
                    message.setTo(conversationId);
                    message.setFrom(senderId);
                    message.setBody(body);
                    message.setChatType(ChatMessage.ChatType.GroupChat);
                    message.setStatus(status);
                    ChatClient.getInstance().chatManager().saveMessage(message);
                }
            }
            LogUtils.showLog(tv_log, getString(R.string.import_message_successful));
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.showErrorLog(tv_log, e.getMessage());
        }
    }

    /**
     * Convert status to {@link ChatMessage.Status}
     * @param status
     * @return
     */
    private ChatMessage.Status translateStatus(int status) {
        switch (status) {
            case 0 :
                return ChatMessage.Status.CREATE;
            case 1 :
                return ChatMessage.Status.INPROGRESS;
            case 3 :
                return ChatMessage.Status.SUCCESS;
            default:
                return ChatMessage.Status.FAIL;
        }
    }

    /**
     * Get data from local resource file
     * @return
     */
    private String loadStreamFromLocal() {
        InputStream inputStream = getResources().openRawResource(R.raw.dbmessage);
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        String jsonStr="",line="";
        try {
            while ((line=reader.readLine())!=null){
                jsonStr+=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

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
        // If token expired, login again
        AccountHelper.signIn(this, tv_log,
                etUsername.getText().toString().trim(), etPwd.getText().toString().trim());
    }

    @Override
    public void onTokenWillExpire() {
        // If token will expire, update it
        AccountHelper.getTokenFromServer(this, tv_log, etUsername.getText().toString().trim(),
                etPwd.getText().toString().trim(), AccountHelper.RENEW_TOKEN);
    }

    /**
     * user met some exception: conflict, removed or forbidden， goto login activity
     */
    protected void onUserException(String exception) {
        LogUtils.showErrorLog(tv_log, "onUserException: " + exception);
        AccountHelper.signOut(this, tv_log, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister connect listener when activity is finishing
        ChatClient.getInstance().removeConnectionListener(this);
    }
}
