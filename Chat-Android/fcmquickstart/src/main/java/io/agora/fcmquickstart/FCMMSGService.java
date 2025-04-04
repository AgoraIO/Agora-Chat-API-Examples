package io.agora.fcmquickstart;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.agora.chat.ChatClient;
import io.agora.util.EMLog;

public class FCMMSGService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "fcmquickstart_notification";
    private static final long[] VIBRATION_PATTERN = new long[]{0, 180, 80, 120};
    private static int NOTIFY_ID = 1000; // start notification id
    private static final String TAG = "EMFCMMSGService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("alert");
            EMLog.i(TAG, "onMessageReceived: " + message);
            notify(message);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        EMLog.i(TAG, "onNewToken: " + token);
        ChatClient.getInstance().sendFCMTokenToServer(token);
    }

    private void notify(String message) {
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            // Create the notification channel for Android 8.0
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "fcm quick start message default channel.", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setVibrationPattern(VIBRATION_PATTERN);
            notificationManager.createNotificationChannel(channel);
        }
        try {
            NotificationCompat.Builder builder = generateBaseBuilder(message);
            Notification notification = builder.build();
            notificationManager.notify(NOTIFY_ID, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a base Notification#Builder, contains:
     * 1.Use the app icon as default icon
     * 2.Use the app name as default title
     * 3.This notification would be sent immediately
     * 4.Can be cancelled by user
     * 5.Would launch the default activity when be clicked
     *
     * @return
     */
    private NotificationCompat.Builder generateBaseBuilder(String content) {
        PackageManager pm = getApplication().getPackageManager();
        String title = pm.getApplicationLabel(getApplication().getApplicationInfo()).toString();
        Intent i = getApplication().getPackageManager().getLaunchIntentForPackage(getApplication().getApplicationInfo().packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplication(), CHANNEL_ID)
                .setSmallIcon(getApplication().getApplicationInfo().icon)
                .setContentTitle(title)
                .setTicker(content)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }
}