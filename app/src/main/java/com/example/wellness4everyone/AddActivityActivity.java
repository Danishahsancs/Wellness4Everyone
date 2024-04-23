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

        // sets up back button
        backButton = (ImageButton) findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivityActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // sets up hr, min, & sec spinners for duration
        spinnerHours = (Spinner) findViewById(R.id.spinner_hours);
        spinnerMinutes = (Spinner) findViewById(R.id.spinner_minutes);
        spinnerSeconds = (Spinner) findViewById(R.id.spinner_seconds);
        setupSpinner(spinnerHours, 24);
        setupSpinner(spinnerMinutes, 60);
        setupSpinner(spinnerSeconds, 60);

        // initializes calendar for date picker
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        // sets date button click to show the DatePickerDialog
        dateButton = (Button) findViewById(R.id.button_selectdate);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivityActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        }
                    }, year, month, day);
                datePickerDialog.show();
            }
        });

    }

    private void setupSpinner(Spinner spinner, int range) {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            options.add(String.format(Locale.getDefault(), "%02d", i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
