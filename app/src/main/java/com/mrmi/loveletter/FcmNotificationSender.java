package com.mrmi.loveletter;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {

    private final String userFcmToken;
    private final Activity mActivity;

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA4oeYbBg:APA91bF1WFXLV7NsQO8HB2IYpRk37DJ6SzzhNVoi0oMhj4pqSAGTQI65L44UL66uLjiFweW1Nf2PfJ4FHGP_vlkyueguaKaO611ob3x73aPR_0Pzt8AuyZj0h9BDh1-fn7Z19-9F55ik";

    private final String userMood, userNeeds;

    public FcmNotificationSender(String userFcmToken, String userMood, String userNeeds, Activity activity) {
        this.userFcmToken = userFcmToken;
        this.userMood = userMood;
        this.userNeeds = userNeeds;
        this.mActivity = activity;
    }

    public void sendNotification() {
        /*Create a JSON file with the following structure:
            {
                "to": userFcmToken;
                "data"
                {
                    "userMood", userMood
                    "userNeeds", userNeeds
                }
            }
        */
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);

            JSONObject userData = new JSONObject();
            userData.put("userMood", userMood);
            userData.put("userNeeds", userNeeds);

            mainObj.put("data", userData);

            //Send a post request to the firebase server
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
            }, error -> {
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}