package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.internal.GenericIdpActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupStats extends AppCompatActivity {
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    TextView walkinfo, runinfo, bikeinfo, swiminfo, liftinfo;
    Button Tbtn,Dbtn,Wbtn,Mbtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_stats);
        db = FirebaseFirestore.getInstance();

        walkinfo = findViewById(R.id.walkinginfo);
        runinfo = findViewById(R.id.runninginfo);
        bikeinfo = findViewById(R.id.bikinginfo);
        swiminfo = findViewById(R.id.swimminginfo);
        liftinfo = findViewById(R.id.liftinginfo);
        Tbtn = findViewById(R.id.totalbtn);
        Dbtn = findViewById(R.id.daybutton);
        Wbtn =findViewById(R.id.weekbtn);
        Mbtn = findViewById(R.id.monthbtn);

    }

    public void getStats(String activity, String term) {
        String startDate = getStartDateBasedOnTerm(term);
        int days = getDaysBasedOnTerm(term);

        if (startDate.isEmpty() || days == 0) {
            Log.e("DateError", "Invalid date term specified: " + term);
            return;
        }

        // Query the specific activity collection directly
        db.collection(activity)
                .whereGreaterThanOrEqualTo("Date", startDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalMins = 0;
                        float totalSteps = 0; // Track steps for walking and running

                        for (DocumentSnapshot document : task.getResult()) {
                            totalMins += parseFloat(document.getString("Minutes"));
                            totalSteps += parseFloat(document.getString("Steps"));
                        }

                        final float avgMins = totalMins / days;
                        final float avgSteps = totalSteps / days;

                        String text = totalMins + " Mins\n" + avgMins + " Mins avg";
                        if (activity.equals("walking") || activity.equals("running")) {
                            text += "\n" + totalSteps + " Total steps\n" + avgSteps + " avg steps per day";
                        }

                        updateUI(text, activity);
                    } else {
                        Toast.makeText(GroupStats.this, "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FirestoreError", "Error fetching data: ", task.getException());
                    }
                });
    }

    private void updateUI(String text, String activity) {
        switch (activity) {
            case "walking":
                walkinfo.setText(text);
                break;
            case "running":
                runinfo.setText(text);
                break;
            case "weightlifting":
                liftinfo.setText(text);
                break;
            case "swimming":
                swiminfo.setText(text);
                break;
            case "biking":
                bikeinfo.setText(text);
                break;
        }
    }

    private float parseFloat(String s) {
        try {
            return Float.parseFloat(s != null ? s : "0");
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getStartDateBasedOnTerm(String term) {
        switch (term) {
            case "Day":
                return getTodayDate();
            case "Month":
                return getStartDate(30);
            case "Week":
                return getPastWeekDate();
            default:
                return "";
        }
    }

    private int getDaysBasedOnTerm(String term) {
        switch (term) {
            case "Day":
                return 1;
            case "Month":
                return 30;
            case "Week":
                return 7;
            default:
                return 0;
        }
    }

    private String getStartDate(int daysBack) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -daysBack);
        return outputDateFormat.format(calendar.getTime());
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return outputDateFormat.format(calendar.getTime());
    }

    private String getPastWeekDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);  // Subtract one week from the current date
        return outputDateFormat.format(calendar.getTime());
    }


    public void goback(View view) {
        Intent intent = new Intent(GroupStats.this, Manger_Menu.class);
        startActivity(intent);
    }

    public void chooseterm(View view){
        Button btn = (Button)  view;
        String tag = btn.getText().toString();

        Intent intent;
        switch (tag) {
            case "Month":
                getStats("walking", "Month");
                getStats("running", "Month");
                getStats("biking", "Month");
                getStats("weightlifting", "Month");
                getStats("swimming", "Month");
                Mbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Mbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Dbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Dbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Wbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Wbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Tbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Tbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                break;
            case "Day":
                getStats("walking", "Day");
                getStats("running", "Day");
                getStats("biking", "Day");
                getStats("weightlifting", "Day");
                getStats("swimming", "Day");
                Mbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Mbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Dbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Dbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Wbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Wbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Tbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Tbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                break;
            case "Week":
                getStats("walking", "Week");
                getStats("running", "Week");
                getStats("biking", "Week");
                getStats("weightlifting", "Week");
                getStats("swimming", "Week");
                Mbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Mbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Dbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Dbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Wbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Wbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Tbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Tbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                break;
            case "Total":
                getTotalStats("walking");
                getTotalStats("running");
                getTotalStats("biking");
                getTotalStats("swimming");
                getTotalStats("weightlifting");
                Mbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Mbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Dbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Dbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Wbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                Wbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Tbtn.setBackgroundTintList(ContextCompat.getColorStateList(GroupStats.this,R.color.Secondarycolor));
                Tbtn.setTextColor(ContextCompat.getColorStateList(GroupStats.this,R.color.primarycolor));
                break;

        }

    }

    public void getTotalStats(String activity) {

        db.collection(activity)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalMins = 0;
                        float totalSteps = 0; // Track steps for walking and running
                        float days =0;
                        for (DocumentSnapshot document : task.getResult()) {
                            totalMins += parseFloat(document.getString("Minutes"));
                            totalSteps += parseFloat(document.getString("Steps"));
                            days++;
                        }

                        final float avgMins = totalMins / days;
                        final float avgSteps = totalSteps / days;

                        String text = totalMins + " Mins\n" + avgMins + " Mins avg";
                        if (activity.equals("walking") || activity.equals("running")) {
                            text += "\n" + totalSteps + " Total steps\n" + avgSteps + " avg steps per day";
                        }

                        updateUI(text, activity);
                    } else {
                        Toast.makeText(GroupStats.this, "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FirestoreError", "Error fetching data: ", task.getException());
                    }
                });
    }
}
