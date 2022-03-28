package com.mrmi.loveletter;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Help extends AppCompatActivity {

    private ImageButton backButton;
    private LinearLayout[] helpGroups;
    private TextView[] helpDetails;
    private TextView historyView;
    private Button deleteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initialiseViews();
        initialiseListeners();
    }

    //Go to the main activity the back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMainActivity();
    }

    private void initialiseViews() {
        backButton = findViewById(R.id.backButton);
        helpGroups = new LinearLayout[]{findViewById(R.id.help1), findViewById(R.id.help2)};
        helpDetails = new TextView[]{findViewById(R.id.help1Details), findViewById(R.id.help2Details)};

        //Enable animations for all help groups
        for (LinearLayout helpGroup : helpGroups) {
            helpGroup.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        historyView = findViewById(R.id.history);
        deleteHistory = findViewById(R.id.delete_history);
    }

    private void initialiseListeners() {
        backButton.setOnClickListener(v -> goToMainActivity());

        //Loop through all help groups and set their listener - toggle the visibility of the specific group's details on click
        for (int i = 0; i < helpGroups.length; ++i) {
            int finalI = i;
            helpGroups[i].setOnClickListener(v -> toggleHelpDetails(helpGroups[finalI], helpDetails[finalI]));
        }

        String history = History.getHistory(this);
        historyView.setText(history);

        deleteHistory.setOnClickListener(v -> {
            History.deleteHistory(this);
            recreate();
        });
    }

    //Shows or hides the details of the given help group's details
    private void toggleHelpDetails(LinearLayout helpGroup, TextView helpDetail) {
        int visibility = (helpDetail.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(helpGroup, new AutoTransition());
        helpDetail.setVisibility(visibility);
    }

    //Go to the main activity
    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}