package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Statspage extends AppCompatActivity {
    private BarChart chart;
    private FirebaseFirestore firestore;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
    private Switch switchWkMth, switchDurSteps;
    private LinearLayout toggleView;
    private Spinner spinnerActivity;
    TextView textAverages;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statspage);

        toggleView = findViewById(R.id.toggleView);
        chart = findViewById(R.id.barChart);
        switchDurSteps = findViewById(R.id.switch_durSteps);
        switchWkMth = findViewById(R.id.switch_wkMth);
        spinnerActivity = findViewById(R.id.spinner_viewstats);
        textAverages = findViewById(R.id.text_averages);

        // sets up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(adapter);

        // Firestore reference
        firestore = FirebaseFirestore.getInstance();

        // initialize visibility
        chart.setVisibility(View.INVISIBLE);
        switchDurSteps.setVisibility(View.INVISIBLE);
        switchWkMth.setVisibility(View.INVISIBLE);
        textAverages.setVisibility(View.INVISIBLE);

        // handles spinner selections
        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String activity = parent.getItemAtPosition(position).toString();
                if (activity.equals("Select activity")) {
                    chart.setVisibility(View.INVISIBLE);
                    switchDurSteps.setVisibility(View.INVISIBLE);
                    switchWkMth.setVisibility(View.INVISIBLE);
                    textAverages.setVisibility(View.INVISIBLE);
                } else {
                    toggleView.setVisibility(View.VISIBLE);
                    updateActivityData(activity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateActivityData(String activity) {
        // converts the activity name to lowercase to match the Firestone collection names
        String finalActivity = activity.toLowerCase();

        // Firestore query
        boolean involvesSteps = finalActivity.equals("running") || finalActivity.equals("walking");

        // sets visibility based on activity type
        chart.setVisibility(View.VISIBLE);
        switchWkMth.setVisibility(View.VISIBLE);
        switchDurSteps.setVisibility(involvesSteps ? View.VISIBLE : View.INVISIBLE);

        // removes existing listeners to prevent stacking listeners
        switchDurSteps.setOnCheckedChangeListener(null);
        switchWkMth.setOnCheckedChangeListener(null);

        // updates chart data based on switches current state
        updateChartData(switchDurSteps.isChecked(), switchWkMth.isChecked(), finalActivity, involvesSteps);

        // reattaches listeners
        switchDurSteps.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateChartData(isChecked, switchWkMth.isChecked(), finalActivity, involvesSteps));

        switchWkMth.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateChartData(switchDurSteps.isChecked(), isChecked, finalActivity, involvesSteps));
    }

    private void updateChartData(boolean showDuration, boolean showThirtyDays, String activity, boolean involvesSteps) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();
        String dateThreshold = getPastDate(showThirtyDays ? 30 : 7);

        firestore.collection("users").document(email).collection(activity)
                .whereGreaterThanOrEqualTo("Date", dateThreshold)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<BarEntry> entries = new ArrayList<>();
                        Map<String, Float> dataMap = new HashMap<>();
                        float total = 0f;
                        int count = 0;

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String date = document.getString("Date");
                            try {
                                Date parsedDate = dateFormat.parse(date); // Parse the date from Firestore using "M-d-yyyy"
                                String formattedDate = outputDateFormat.format(parsedDate); // Reformat to "MM-dd-yyyy"

                                String dataKey = involvesSteps ? (showDuration ? "Minutes" : "Steps") : "Minutes";
                                String stringValue = document.getString(dataKey);
                                Float value = Float.parseFloat(stringValue);
                                dataMap.put(formattedDate, value);

                                if (formattedDate.compareTo(dateThreshold) >= 0) {
                                    total += value;
                                    count++;
                                }

                            } catch (ParseException e) {
                                Log.e("DateConversionError", "Error parsing date: " + date, e);
                            }
                        }

                        if (count > 0) {
                            float average = total / count;
                            textAverages.setVisibility(View.VISIBLE);
                            displayAverage(average, showDuration, showThirtyDays, involvesSteps);
                        } else {
                            textAverages.setVisibility(View.INVISIBLE);
                        }

                        entries.addAll(createEntriesForDays(dataMap, showThirtyDays ? 30 : 7));
                        updateChart(entries, showDuration && involvesSteps ? "Minutes" : "Steps");
                    } else {
                        // Handle failures
                        Log.e("FirestoreError", "Error fetching data: ", task.getException()); // temporal
                    }
                });
    }

    private String getPastDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        return outputDateFormat.format(calendar.getTime());
    }

    private List<BarEntry> createEntriesForDays(Map<String, Float> dataMap, int days) {
        List<BarEntry> entries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < days; i++) {
            String dateKey = outputDateFormat.format(calendar.getTime());
            float value = dataMap.getOrDefault(dateKey, 0f);
            entries.add(new BarEntry(i, value));
            calendar.add(Calendar.DATE, -1); // resets calendar
        }

        return entries;
    }

    private void updateChart(List<BarEntry> entries, String label) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColors(ContextCompat.getColor(this, R.color.Secondarycolor));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));

        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        chart.animateY(1000);

        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh

        chartAppearance();
    }

    private void chartAppearance() {
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        chart.getAxisLeft().setAxisLineColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        chart.getAxisLeft().setTextSize(20f);
    }

    private void displayAverage(float average, boolean showDuration, boolean showThirtyDays, boolean involvesSteps) {
        String unit = involvesSteps ? (showDuration ? "min" : "steps") : "min";
        String timeFrame = showThirtyDays ? "month" : "week";

        if (involvesSteps && !showDuration) {
            average = Math.round(average);
            textAverages.setText(String.format(Locale.getDefault(), "Average %s for past %s:\n%.0f %s", unit, timeFrame, average, unit));
        } else {
            average = Math.round(average * 100) / 100f;
            textAverages.setText(String.format(Locale.getDefault(), "Average %s for past %s:\n%.2f %s", unit, timeFrame, average, unit));
        }
    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(Statspage.this, NotificationActivity.class);
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