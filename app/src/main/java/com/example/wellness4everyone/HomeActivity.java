package com.example.wellness4everyone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);


    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(HomeActivity.this,NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(HomeActivity.this, Statspage.class);
                break;
            case "Activitiespage":
                intent = new Intent(HomeActivity.this, Activitieslist.class);
                break;
            case "Homepage":
                intent = new Intent(HomeActivity.this, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);
    }
}
