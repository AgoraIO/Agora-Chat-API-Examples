package io.agora.agorachatquickstart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.agora.CallBack;
import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.agorachatquickstart.record.EaseChatRowVoicePlayer;
import io.agora.agorachatquickstart.record.EaseVoiceRecorder;
import io.agora.agorachatquickstart.utils.AccountHelper;
import io.agora.agorachatquickstart.utils.PermissionsManager;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.agorachatquickstart.utils.LogUtils;

public class SendAudioMessageActivity extends AppCompatActivity implements ConnectionListener {
    private Activity mContext;
    private Button btnStartRecording;
    private TextView tv_log;
    private EditText etToChatName;
    private EaseVoiceRecorder voiceRecorder;
    private PowerManager.WakeLock wakeLock;
    private String toChatUsername;
    private Button btnSignIn;
    private EditText etUsername;
    private EditText etPwd;
    private Button btnSignUp;

    private boolean canRecord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_audio_message);
        mContext = this;
        AccountHelper.initSDK(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        btnStartRecording = findViewById(R.id.btn_start_recording);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setMovementMethod(new ScrollingMovementMethod());
        etToChatName = findViewById(R.id.et_to_chat_name);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
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

        btnStartRecording.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        btnStartRecording.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(!ChatClient.getInstance().isLoggedIn()) {
                        LogUtils.showErrorToast(mContext, tv_log, getString(R.string.sign_in_first));
                        return false;
                    }
                    if (!checkRecordPermission()) {
                        LogUtils.showErrorLog(tv_log, getString(R.string.recording_without_permission));
                        return false;
                    }
                    toChatUsername = etToChatName.getText().toString().trim();
                    if(TextUtils.isEmpty(toChatUsername)) {
                        LogUtils.showErrorLog(tv_log, getString(R.string.not_find_send_audio_name));
                        return false;
                    }
                    canRecord = true;
                    startRecord();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if(!canRecord) {
                        return false;
                    }
                    moveAction(event);
                    return true;
                case MotionEvent.ACTION_UP:
                    if(!canRecord) {
                        return false;
                    }
                    stopRecord(event);
                    canRecord = false;
                    return true;
                default:
                    return false;
            }
        });
        // Register Agora Chat connect listener
        ChatClient.getInstance().addConnectionListener(this);
    }

    /**
     * Check record audio permission
     * @return
     */
    private boolean checkRecordPermission() {
        // Check if have the permission of RECORD_AUDIO
        if (!PermissionsManager.getInstance().hasPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PermissionsManager.getInstance().requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return false;
        }
        return true;
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initData() {
        voiceRecorder = new EaseVoiceRecorder(null);
        wakeLock = ((PowerManager) this.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "sendAudio");

    }

    /**
     * Send audio message
     * @param length
     */
    private void sendAudioMessage(int length) {
        LogUtils.showLog(tv_log, getString(R.string.start_to_send_audio));
        String voiceFilePath = voiceRecorder.getVoiceFilePath();
        ChatMessage message = ChatMessage.createVoiceSendMessage(Uri.parse(voiceFilePath), length, toChatUsername);
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                LogUtils.showLog(tv_log, getString(R.string.send_audio_message_success));
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.showErrorToast(mContext, tv_log,
                        getString(R.string.message_sent_failed, code, error));
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        // send message
        ChatClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * Start to record
     */
    private void startRecord() {
        try {
            EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(this);
            if (voicePlayer.isPlaying())
                voicePlayer.stop();
            startRecording();
        } catch (Exception e) {

        }
    }

    /**
     * Move action, do nothing
     * @param event
     */
    private void moveAction(MotionEvent event) {
        if (event.getY() < dip2px(this, 10)) {
            //showReleaseToCancelHint();
        } else {
            //showMoveUpToCancelHint();
        }
    }

    /**
     * Stop recording
     * @param event
     */
    private void stopRecord(MotionEvent event) {
        if (event.getY() < 0) {
            // discard the recorded audio.
            discardRecording();
        } else {
            // stop recording and send voice file
            try {
                int length = stopRecoding();
                if (length > 0) {
                    sendAudioMessage(length);
                } else if (length == Error.FILE_INVALID) {
                    LogUtils.showErrorToast(this, tv_log, getString(R.string.recording_without_permission));
                } else {
                    LogUtils.showErrorToast(this, tv_log, getString(R.string.recording_time_is_too_short));
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.showErrorToast(this, tv_log, getString(R.string.send_failure_please));
            }
        }
    }

    /**
     * Start to record
     */
    private void startRecording() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            LogUtils.showErrorToast(this, tv_log, getString(R.string.send_voice_need_sdcard_support));
            return;
        }
        LogUtils.showLog(tv_log, getString(R.string.start_record));
        try {
            wakeLock.acquire();
            voiceRecorder.startRecording(this);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            LogUtils.showErrorToast(this, tv_log, getString(R.string.recoding_fail));
            return;
        }
    }

    /**
     * Stop recording
     * @return
     */
    private int stopRecoding() {
        if (wakeLock.isHeld())
            wakeLock.release();
        LogUtils.showLog(tv_log, getString(R.string.stop_record));
        return voiceRecorder.stopRecoding();
    }

    /**
     * Discard recording
     */
    private void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
            }
        } catch (Exception e) {
        }
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

    /**
     * dip to px
     *
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
