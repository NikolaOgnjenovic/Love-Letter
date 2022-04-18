package com.mrmi.loveletter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private NotificationManager notificationManager;
    private final String channelId = "LoveLetterNotifications";
    private String partnerMood, partnerNeeds;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        partnerMood = Objects.requireNonNull(remoteMessage.getData()).get("userMood");
        partnerNeeds = Objects.requireNonNull(remoteMessage.getData()).get("userNeeds");

        savePartnerValues();
        getNotificationManager();
        createNotificationChannel();
        notifyUser();
        updateWidget();
        History.addToHistory(partnerMood, partnerNeeds, this);
    }

    //Save partner's needs and mood (the content of the received notification) in shared preferences
    private void savePartnerValues() {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("partnerMood", partnerMood).apply();
        sharedPreferencesEditor.putString("partnerNeeds", partnerNeeds).apply();
    }

    private void getNotificationManager() {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //Create notification channel, used in Android versions above 8
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "LoveLetter"; //Channel name
            String description = "LoveLetter notification channel"; //Channel description
            int importance = NotificationManager.IMPORTANCE_HIGH; //Channel importance
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            //Register the channel
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    //Set notification properties and notify the user
    private void notifyUser() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        builder.setSmallIcon(android.R.drawable.btn_star);

        Intent resultIntent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.notification_text));
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setChannelId(channelId);
        builder.setSmallIcon(R.drawable.small_notification_icon);
        notificationManager.notify(100, builder.build());
    }

    private void updateWidget() {
        System.out.println("[MRMI]: Calling update widget");
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), AppWidgetProvider.class));
        AppWidgetProvider appWidgetProvider = new AppWidgetProvider();
        appWidgetProvider.onUpdate(this, AppWidgetManager.getInstance(this),ids);
    }
}