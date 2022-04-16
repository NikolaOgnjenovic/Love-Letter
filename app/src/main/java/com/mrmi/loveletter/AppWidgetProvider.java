package com.mrmi.loveletter;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            System.out.println("[MRMI]: Updating widget");

            Intent intent = new Intent(context, MainActivity.class);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
            SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            remoteViews.setTextViewText(R.id.widgetPartnerMoodView, " " + sharedPreferences.getString("partnerMood", ""));
            remoteViews.setTextViewText(R.id.widgetPartnerNeedsView, " " + sharedPreferences.getString("partnerNeeds", ""));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
