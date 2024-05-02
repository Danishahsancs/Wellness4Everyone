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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.widget.ArrayAdapter;
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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
    private Switch switchWkMth, switchDurSteps;
    private LinearLayout toggleView;
    private Spinner spinnerActivity;
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

        // handles spinner selections
        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String activity = parent.getItemAtPosition(position).toString();
                if (activity.equals("Select activity")) {
                    chart.setVisibility(View.INVISIBLE);
                    switchDurSteps.setVisibility(View.INVISIBLE);
                    switchWkMth.setVisibility(View.INVISIBLE);
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

        firestore.collection("users").document(email).collection(activity)
                .whereGreaterThanOrEqualTo("Date", getPastDate(showThirtyDays ? 30 : 7))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<BarEntry> entries = new ArrayList<>();
                        Map<String, Float> dataMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String date = document.getString("Date");
                            String dataKey = involvesSteps ? (showDuration ? "Minutes" : "Steps") : "Minutes";
                            String stringValue = document.getString(dataKey);
                            Float value = Float.parseFloat(stringValue);
                            dataMap.put(date, value);
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
        return dateFormat.format(calendar.getTime());
    }

    private List<BarEntry> createEntriesForDays(Map<String, Float> dataMap, int days) {
        List<BarEntry> entries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < days; i++) {
            String dateKey = dateFormat.format(calendar.getTime());
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

        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        chart.getAxisLeft().setAxisLineColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        chart.getAxisLeft().setTextSize(20f);

        chart.invalidate(); // refresh
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