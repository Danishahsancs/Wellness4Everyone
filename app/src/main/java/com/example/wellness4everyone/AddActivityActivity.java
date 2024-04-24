package com.example.wellness4everyone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddActivityActivity extends Activity {

    ImageButton backButton;
    Button dateButton;
    Spinner spinnerHours, spinnerMinutes, spinnerSeconds;
    int year, month, day;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addactivity_screen);


        Intent intent = getIntent();
        String activityTag = intent.getStringExtra("ACTIVITY_TAG");

        // sets up back button
        backButton = (ImageButton) findViewById(R.id.button_back);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateButton = (Button) findViewById(R.id.button_selectdate);

    }

    public void goback(View view){
        Intent intent = new Intent(AddActivityActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    public void setdate(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivityActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

}
