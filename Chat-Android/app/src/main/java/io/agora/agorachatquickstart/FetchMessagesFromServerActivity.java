package io.agora.agorachatquickstart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.ValueCallBack;
import io.agora.agorachatquickstart.utils.AccountHelper;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.CursorResult;
import io.agora.agorachatquickstart.utils.LogUtils;

public class FetchMessagesFromServerActivity extends AppCompatActivity implements ConnectionListener {
    private Activity mContext;
    private TextView tv_log;
    private Button btnSignIn;
    private Button btnSignUp;
    private EditText etUsername;
    private EditText etPwd;
    private Button btnFetchConversations;
    private Button btnFetchMessages;
    private Map<String, Conversation> mConversationMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_messages_from_server);
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
        btnFetchConversations = findViewById(R.id.btn_fetch_conversations);
        btnFetchMessages = findViewById(R.id.btn_fetch_messages);
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
        btnFetchConversations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ChatClient.getInstance().isLoggedIn()) {
                    LogUtils.showErrorToast(mContext, tv_log, getString(R.string.sign_in_first));
                    return;
                }
                fetchConversations();
            }
        });
        btnFetchMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMessagesFromConversation();
            }
        });
    }

    /**
     * Fetch conversation list from Agora Chat Server
     */
    private void fetchConversations() {
        ChatClient.getInstance().chatManager().asyncFetchConversationsFromServer(new ValueCallBack<Map<String, Conversation>>() {
            @Override
            public void onSuccess(Map<String, Conversation> conversationMap) {
                LogUtils.showLog(tv_log, getString(R.string.fetch_conversations_success)+" size: "+conversationMap.size());
                if(conversationMap.size() > 0) {
                    runOnUiThread(()->btnFetchMessages.setVisibility(View.VISIBLE));
                    mConversationMap = conversationMap;
                    Iterator<Map.Entry<String, Conversation>> iterator = conversationMap.entrySet().iterator();
                    if(iterator.hasNext()) {
                        Map.Entry<String, Conversation> conversationEntry = iterator.next();
                        Conversation conv = conversationEntry.getValue();
                        LogUtils.showLog(tv_log, getString(R.string.show_conversation, conv.conversationId(), conv.getType().name()));
                    }
                }
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(mContext, tv_log, getString(R.string.fetch_conversation_failed, code, error));
            }
        });
    }

    /**
     * Fetch messages from Agora Chat Server by conversation id
     */
    private void fetchMessagesFromConversation() {
        int size = mConversationMap.size();
        int target = (int)(Math.random()*size);
        int index = 0;
        Conversation targetConversation = null;
        for (Conversation conversation : mConversationMap.values()) {
            if(target == index) {
                targetConversation = conversation;
                break;
            }
            index++;
        }
        if(targetConversation == null) {
            return;
        }
        ChatClient.getInstance().chatManager().asyncFetchHistoryMessage(targetConversation.conversationId(),
                targetConversation.getType(), 20, null, new ValueCallBack<CursorResult<ChatMessage>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatMessage> result) {
                        LogUtils.showLog(tv_log, getString(R.string.fetch_messages_success));
                        LogUtils.showLog(tv_log, getString(R.string.print_only_first_10_data));
                        List<ChatMessage> messages = result.getData();
                        for(int i = 0; i < messages.size(); i++) {
                            if(i>=10) {
                                break;
                            }
                            ChatMessage message = messages.get(i);
                            LogUtils.showLog(tv_log, getString(R.string.show_message, i, message.conversationId(),
                                    message.getMsgId(), message.getType().name(), message.getFrom(), message.getTo()));
                        }
                    }

                    @Override
                    public void onError(int code, String error) {
                        LogUtils.showErrorToast(mContext, tv_log, getString(R.string.fetch_message_failed, code, error));
                    }
                });
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
        AccountHelper.signIn(this, tv_log,
                etUsername.getText().toString().trim(), etPwd.getText().toString().trim());
    }

    @Override
    public void onTokenWillExpire() {
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
