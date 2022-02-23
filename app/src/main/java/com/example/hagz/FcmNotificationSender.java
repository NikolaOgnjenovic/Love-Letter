package com.example.hagz;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA4oeYbBg:APA91bF1WFXLV7NsQO8HB2IYpRk37DJ6SzzhNVoi0oMhj4pqSAGTQI65L44UL66uLjiFweW1Nf2PfJ4FHGP_vlkyueguaKaO611ob3x73aPR_0Pzt8AuyZj0h9BDh1-fn7Z19-9F55ik";

    /*public FcmNotificationSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;

        cvjU7Jl_QE6bG_h8bYRiy1:APA91bGyW14XyZY5pads-sC_LSE685stL019KhLnsF2T_QkQ6dzDgvxDd1MS8m1T1m5by30meMsYCfcLWLwj27VGug_kZCcdpXTIEO-4R0SX6R9nVuyGU3r02mk6YoJ0HpaTPsTnHyT6
     */

    private String userMood, userNeeds;

    public FcmNotificationSender(String userFcmToken, String userMood, String userNeeds, Context context, Activity activity) {
        this.userFcmToken = userFcmToken;
        this.userMood = userMood;
        this.userNeeds = userNeeds;
        this.mContext = context;
        this.mActivity = activity;
    }
    public void SendNotifications() {
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            /*JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            notificationObject.put("icon", "icon");
            mainObj.put("notification", notificationObject);
            */
            JSONObject userData = new JSONObject();
            userData.put("userMood", userMood);
            userData.put("userNeeds", userNeeds);
            mainObj.put("data", userData);

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