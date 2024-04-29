package com.example.wellness4everyone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends Activity {

    Button welcomeWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME_KEY");
        welcomeWidget = (Button) findViewById(R.id.text_welcome);
        String welcomeText = "<big><b>Welcome back, " + username + "!</b></big><br/><br/>" +
                "<small>Kick off your day by starting a new activity.</small>";
        welcomeWidget.setText(Html.fromHtml(welcomeText, Html.FROM_HTML_MODE_LEGACY));

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
