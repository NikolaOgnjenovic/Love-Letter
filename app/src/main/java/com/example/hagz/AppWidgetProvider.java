package com.example.hagz;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    @Override
    public void onUpdate(Context context , AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            //Get the widget view
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            //Go to main activity on click
            views.setOnClickPendingIntent(R.id.view, pendingIntent);
            //Display the partner's needs
            views.setTextViewText(R.id.widgetPartnerNeedsView, context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getString("partnerNeeds", ""));
            views.setTextViewText(R.id.widgetPartnerMoodView, context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getString("partnerMood", ""));
            //Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
