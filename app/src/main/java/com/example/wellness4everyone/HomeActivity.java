package com.example.wellness4everyone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends Activity {
    ImageButton homeButton;
    ImageButton notifyButton;
    Button addActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        homeButton = (ImageButton) findViewById(R.id.homeButton);
        notifyButton = (ImageButton) findViewById(R.id.notifyButton);
        addActivityButton = (Button) findViewById(R.id.button_addactivity);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // does nothing for now, may add functionality to refresh page if need be
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddActivityActivity.class);
                startActivity(intent);
            }
        });
    }
}