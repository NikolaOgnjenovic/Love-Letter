package com.mrmi.loveletter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private EditText userMoodInput, userNeedsInput, partnerTokenInput;
    private ImageButton sendToPartner;
    private TextView partnerMoodView, partnerNeedsView;
    private String userToken = "";
    private SharedPreferences sharedPreferences;
    private ImageButton helpButton;

    private ImageButton shareUserToken, clearPartnerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        initialiseListeners();
        initialiseObjects();

        getUserToken(true);

        initialiseAds();
    }


    private void initialiseAds() {
        MobileAds.initialize(this, initializationStatus -> {});

        AdView bannerAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
    }

    //Quit the app when the user presses the back button
    @Override
    public void onBackPressed() {
        Intent quitIntent = new Intent(Intent.ACTION_MAIN);
        quitIntent.addCategory(Intent.CATEGORY_HOME);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(quitIntent);
    }

    private void initialiseViews() {
        userNeedsInput = findViewById(R.id.userNeedsInput);
        partnerNeedsView = findViewById(R.id.partnerNeedsView);
        partnerTokenInput = findViewById(R.id.partnerTokenInput);
        sendToPartner = findViewById(R.id.sendToPartner);
        userMoodInput = findViewById(R.id.userMoodInput);
        partnerMoodView = findViewById(R.id.partnerMoodView);
        helpButton = findViewById(R.id.helpButton);
        shareUserToken = findViewById(R.id.shareUserToken);
        clearPartnerToken = findViewById(R.id.clearPartnerToken);
    }

    private void initialiseListeners() {
        //Send a notification to the user with the partnerTokenInput token
        sendToPartner.setOnClickListener(v -> sendNotificationToUserWithToken(partnerTokenInput.getText().toString()));

        //Copy the user's token to the clipboard
        shareUserToken.setOnClickListener(v -> {
            userToken = getUserToken(false);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your token with your partner!");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, userToken);
            startActivity(Intent.createChooser(sharingIntent,"Share with"));
        });

        //Save partner's token when the user changes it
        partnerTokenInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                savePartnerToken();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //Open help activity on help button click
        helpButton.setOnClickListener(v -> startActivity(new Intent(this, Help.class)));

        clearPartnerToken.setOnClickListener(v-> partnerTokenInput.setText(""));
    }

    private void initialiseObjects() {
        //Subscribe to all messaging topics
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        updatePartnerViews();
        updateUserViews();

        //Display the partner's token
        partnerTokenInput.setText(getPartnerToken());

        userMoodInput.clearFocus();
        userNeedsInput.clearFocus();
        partnerTokenInput.clearFocus();
        //hideSoftKeyboard(MainActivity.this);
    }

    @Override
    public void onResume() {
        updatePartnerViews();
        super.onResume();
    }

    //Get the user's token
    private String getUserToken(boolean calledFromOnCreate) {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> userToken = task.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("[MRMI]: User's token: " + userToken);

        if(!calledFromOnCreate && userToken.equals("")) {
            Toast toast = Toast.makeText(this, getString(R.string.copy_again_toast), Toast.LENGTH_SHORT);
            TextView textView = toast.getView().findViewById(android.R.id.message);
            textView.setTextColor(getResources().getColor(R.color.theme_primary));
            textView.setBackgroundColor(getResources().getColor(R.color.white));
            textView.setTextSize(20);
            toast.getView().setBackgroundColor(getResources().getColor(R.color.white));
            toast.show();
        }
        return userToken;
    }

    private void sendNotificationToUserWithToken(String token) {
        if(token.equals("")) {
            displayNullPartnerTokenDialog();
            return;
        }

        FcmNotificationSender notificationSender = new FcmNotificationSender(token, userMoodInput.getText().toString(), userNeedsInput.getText().toString(), this);
        notificationSender.sendNotification();
        saveUserMood();
        saveUserNeeds();
        updateUserViews();
        displayLetterSentDialog();
    }

    //Get partner's mood text from Shared preferences
    private String getPartnerMood() {
        return sharedPreferences.getString("partnerMood", "");
    }

    //Get partner's needs text from Shared preferences
    private String getPartnerNeeds() {
        return sharedPreferences.getString("partnerNeeds", "");
    }

    //Save the user's mood using Shared preferences
    private void saveUserMood() {
        sharedPreferences.edit().putString("userMood", userMoodInput.getText().toString()).apply();
    }

    //Save the user's needs using Shared preferences
    private void saveUserNeeds() {
        sharedPreferences.edit().putString("userNeeds", userNeedsInput.getText().toString()).apply();
    }

    //Get user's mood text from Shared preferences
    private String getUserMood() {
        return sharedPreferences.getString("userMood", "");
    }

    //Get user's needs text from Shared preferences
    private String getUserNeeds() {
        return sharedPreferences.getString("userNeeds", "");
    }

    //Get the user's mood and needs from Shared preferences and display them
    private void updateUserViews() {
        String userMoodText = getUserMood(), userNeedsText = getUserNeeds();
        userMoodInput.setText(userMoodText);
        userNeedsInput.setText(userNeedsText);
    }

    //Get the partner's mood and needs from Shared preferences and display them
    private void updatePartnerViews() {
        String partnerMoodText = getPartnerMood(), partnerNeedsText = getPartnerNeeds();
        partnerMoodView.setText(partnerMoodText);
        partnerNeedsView.setText(partnerNeedsText);
    }

    //Save the partner's token using Shared preferences
    private void savePartnerToken() {
        sharedPreferences.edit().putString("partnerToken", partnerTokenInput.getText().toString()).apply();
    }

    //Get partner's token from Shared preferences
    private String getPartnerToken() {
        return sharedPreferences.getString("partnerToken", "");
    }

    //Alert the user that their letter has been sent
    private void displayLetterSentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.letter_sent, null);
        alert.setView(dialogView);
        AlertDialog alertDialog = alert.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.okayButton).setOnClickListener(v-> alertDialog.dismiss());
    }

    //Displays a dialog which alerts the user that they need to input their partner's token in order to send a letter
    private void displayNullPartnerTokenDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.null_partner_token, null);
        alert.setView(dialogView);
        AlertDialog alertDialog = alert.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.okayButton).setOnClickListener(v-> alertDialog.dismiss());
    }

    //Removes focus from an edit text when the user taps outside of it
    private void removeEditTextFocus(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        removeEditTextFocus(ev);
        return super.dispatchTouchEvent(ev);
    }
}