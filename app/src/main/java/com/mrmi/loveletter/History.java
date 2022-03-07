package com.mrmi.loveletter;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.Date;

public class History {
    private static String history;

    //Loads all received partner's moods & needs
    public static void loadHistory(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Shared preferences", Context.MODE_PRIVATE);
        history = sharedPreferences.getString("History", "");
    }

    //Saves all received partner's moods & needs
    public static void saveHistory(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("History", history);
        editor.apply();
    }

    //Adds the received partner mood & needs and the time at which they were received to the status history and saves it
    public static void addToHistory(String partnerMood, String partnerNeeds, Context context) {
        Date currentDateTime = new Date();
        loadHistory(context);
        String newStatus = "[" + DateFormat.getDateTimeInstance().format(currentDateTime) + "]" + "\n" + "Your partner was feeling: " + partnerMood + "\n" + "Your partner needed: " + partnerNeeds + "\n" + "\n";
        history += newStatus;
        saveHistory(context);
    }

    public static String getHistory(Context context) {
        loadHistory(context);
        return history;
    }

    public static void deleteHistory(Context context) {
        loadHistory(context);
        history = "";
        saveHistory(context);
    }
}
