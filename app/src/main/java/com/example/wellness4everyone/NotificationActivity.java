package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

public class NotificationActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);


    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(NotificationActivity.this,NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(NotificationActivity.this, Activitieslist.class);
                break;
            case "Activitiespage":
                intent = new Intent(NotificationActivity.this, Statspage.class);
                break;
            case "Homepage":
                intent = new Intent(NotificationActivity.this, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);
    }
}

