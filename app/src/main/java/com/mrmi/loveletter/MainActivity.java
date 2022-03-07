package com.mrmi.loveletter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private EditText userMoodInput, userNeedsInput, partnerToken;
    private ImageButton sendToPartner;
    private Button getUserToken;
    private TextView partnerMoodView, partnerNeedsView, userTokenView;
    private String userToken;
    private SharedPreferences sharedPreferences;
    private ImageButton helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        initialiseListeners();
        initialiseObjects();
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
        partnerToken = findViewById(R.id.partnerToken);
        sendToPartner = findViewById(R.id.sendToPartner);
        userTokenView = findViewById(R.id.userToken);
        getUserToken = findViewById(R.id.getUserToken);
        userMoodInput = findViewById(R.id.userMoodInput);
        partnerMoodView = findViewById(R.id.partnerMoodView);
        helpButton = findViewById(R.id.helpButton);
    }

    private void initialiseListeners() {
        //Send a notification to the user with the partnerToken token
        sendToPartner.setOnClickListener(v -> sendNotificationToUserWithToken(partnerToken.getText().toString()));

        getUserToken.setOnClickListener(v -> userTokenView.setText(getUserToken()));

        userTokenView.setOnClickListener(v -> copyText(userTokenView.getText().toString()));

        partnerToken.addTextChangedListener(new TextWatcher() {
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
    }

    private void initialiseObjects() {
        //Subscribe to all messaging topics
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        updatePartnerViews();
        updateUserViews();

        //Display the user's token
        userTokenView.setText(getUserToken());

        //Display the partner's token
        partnerToken.setText(getPartnerToken());
    }

    //Get the user's token
    private String getUserToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> userToken = task.getResult());
        System.out.println("[MRMI]: User's token: " + userToken);
        return userToken;
    }

    private void sendNotificationToUserWithToken(String token) {
        FcmNotificationSender notificationSender = new FcmNotificationSender(token, userMoodInput.getText().toString(), userNeedsInput.getText().toString(), this);
        notificationSender.sendNotification();
        saveUserMood();
        saveUserNeeds();
        updateUserViews();
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

    //Copies the given String into the device's clipboard
    private void copyText(String text) {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); //Get the clipboard service
        ClipData myClip = ClipData.newPlainText("text", text); //Put the given text into the clip
        myClipboard.setPrimaryClip(myClip); //Put the clip into the clipboard
        Toast.makeText(this, getString(R.string.copied_user_token), Toast.LENGTH_SHORT).show(); //Notify the user that the text was copied to the clipboard
    }

    //Get the user's mood and needs from Shared preferences and display them
    private void updateUserViews() {
        String userMoodText = getUserMood(), userNeedsText = getUserNeeds();
        userMoodInput.setText(userMoodText);
        userNeedsInput.setText(userNeedsText);
    }

    //Get the partner's mood and needs from Shared preferences and display them
    private void updatePartnerViews() {
        String partnerMoodText = getString(R.string.partner_mood) + " " + getPartnerMood(), partnerNeedsText = getString(R.string.partner_needs) + " " + getPartnerNeeds();
        partnerMoodView.setText(partnerMoodText);
        partnerNeedsView.setText(partnerNeedsText);
    }

    //Save the partner's token using Shared preferences
    private void savePartnerToken() {
        sharedPreferences.edit().putString("partnerToken", partnerToken.getText().toString()).apply();
    }

    //Get partner's token from Shared preferences
    private String getPartnerToken() {
        return sharedPreferences.getString("partnerToken", "");
    }
}