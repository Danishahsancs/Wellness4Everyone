package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activitieslist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activitieslist);
    }

    // handles changing screens
    public void changescreen(View view) {
        ScreenNavigator.navigate(this, view);
    }

    // handles choosing an activity
    public void chooseactivity(View view){
        Button btn = (Button)  view;
        String tag = btn.getTag().toString();

        // creates intent to start AddActivityActivity
        Intent intent = new Intent(Activitieslist.this, AddActivityActivity.class);
        // passes activity tag as an extra to differentiate which activity screen to display
        intent.putExtra("ACTIVITY_TAG", tag);
        startActivity(intent);
    }
}
