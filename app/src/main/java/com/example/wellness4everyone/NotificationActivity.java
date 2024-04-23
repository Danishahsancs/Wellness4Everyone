package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

public class NotificationActivity extends Activity {

    ImageButton homeButton;
    ImageButton notifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);

        homeButton = (ImageButton) findViewById(R.id.homeButton);
        notifyButton = (ImageButton) findViewById(R.id.notifyButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // does nothing for now, may add functionality to refresh page if need be
            }
        });
    }
}
