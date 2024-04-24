package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Statspage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statspage);
    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(Statspage.this,NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(Statspage.this, Statspage.class);
                break;
            case "Activitiespage":
                intent = new Intent(Statspage.this, Activitieslist.class);
                break;
            case "Homepage":
                intent = new Intent(Statspage.this, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);
    }
}
