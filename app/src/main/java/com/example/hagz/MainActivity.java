package com.example.hagz;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private EditText userNeedsInput, partnerToken;
    private Button sendAll, sendToPartner, getUserToken;
    private TextView partnerNeedsView, userTokenView;
    private String userToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        initialiseListeners();
        initialiseObjects();
    }

    private void initialiseViews() {
        userNeedsInput = findViewById(R.id.userNeedsInput);
        partnerNeedsView = findViewById(R.id.partnerNeedsView);
        partnerToken = findViewById(R.id.partnerToken);
        sendAll = findViewById(R.id.sendAll);
        sendToPartner = findViewById(R.id.sendToPartner);
        userTokenView = findViewById(R.id.userToken);
        getUserToken = findViewById(R.id.getUserToken);
    }

    private void initialiseListeners() {
        //Send a notification to all users (temp)
        sendAll.setOnClickListener(v -> {
            FcmNotificationSender notificationSender = new FcmNotificationSender("/topics/all", "Hagz notifier", userNeedsInput.getText().toString(), getApplicationContext(), MainActivity.this);
            notificationSender.SendNotifications();
        });

        //Send a notification to the user with the partnerToken token
        sendToPartner.setOnClickListener(v -> {
            FcmNotificationSender notificationSender = new FcmNotificationSender(partnerToken.getText().toString(), "Hagz notifier", userNeedsInput.getText().toString(), getApplicationContext(), MainActivity.this);
            notificationSender.SendNotifications();
        });

        getUserToken.setOnClickListener(v -> userTokenView.setText(getUserToken()));

        userTokenView.setOnClickListener(v -> copyText(userTokenView.getText().toString()));
    }

    private void initialiseObjects() {
        //Subscribe to all messaging topics
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        //Get partner's needs from Shared preferences and display them
        String partnerNeedsText = partnerNeedsView.getText().toString() + getPartnerNeeds();
        partnerNeedsView.setText(partnerNeedsText);

        //Display the user's token
        userTokenView.setText(getUserToken());
    }

    //Get the user's token
    private String getUserToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> userToken = task.getResult());
        System.out.println("[MRMI]: User's token: " + userToken);
        return userToken;
    }

    //Get partner's needs text from shared preferences
    private String getPartnerNeeds() {
        return sharedPreferences.getString("partnerNeeds", "");
    }

    //Copies the given String into the device's clipboard
    private void copyText(String text) {
        ClipboardManager myClipboard = (ClipboardManager)  getSystemService(CLIPBOARD_SERVICE); //Get the clipboard service
        ClipData myClip = ClipData.newPlainText("text", text); //Put the given text into the clip
        myClipboard.setPrimaryClip(myClip); //Put the clip into the clipboard
        Toast.makeText(this, getString(R.string.copied_user_token), Toast.LENGTH_SHORT).show(); //Notify the user that the text was copied to the clipboard
    }
}