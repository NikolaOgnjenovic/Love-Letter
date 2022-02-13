package com.example.hagz;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private NotificationManager notificationManager;
    private final String channelId = "HagzNotifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Save partner's needs (the content of the received notification) in shared preferences
        getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit().putString("partnerNeeds", Objects.requireNonNull(remoteMessage.getNotification()).getBody()).apply();

        getNotificationManager();
        createNotificationChannel();
        notifyUser(remoteMessage);
        updateWidget(Objects.requireNonNull(remoteMessage.getNotification()).getBody());

    }

    private void notifyUser(RemoteMessage remoteMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        builder.setSmallIcon(android.R.drawable.btn_star);

        Intent resultIntent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //builder.setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        //builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentTitle("Hagz");
        builder.setContentText("Your partner wants you to know that their mood has changed!");
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Objects.requireNonNull(remoteMessage.getNotification()).getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setChannelId(channelId);
        notificationManager.notify(100, builder.build());
    }

    //Create notification channel, used in Android versions above 8
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Hagz"; //Channel name
            String description = "Hagz notification channel"; //Channel description
            int importance = NotificationManager.IMPORTANCE_HIGH; //Channel importance
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            //Register the channel
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void getNotificationManager() {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void updateWidget(String partnerNeedsText) {
        Intent intent = new Intent(this, AppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), AppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        //intent.putExtra("PartnerNeedsText", partnerNeedsText);
        sendBroadcast(intent);
    }
}