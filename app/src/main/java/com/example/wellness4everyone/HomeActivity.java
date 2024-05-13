package com.example.wellness4everyone;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class HomeActivity extends Activity {
    private FirebaseFirestore firestore;
    private TextView textSteps, progBarPercent, statusText, weeksLabel, textQuote, textQuoteAuthor, welcomeUser;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String email;
    CircularProgressIndicator progBar;
    SharedPreferences prefs;
    private final List<TimePeriod> timePeriods = Arrays.asList(
            new TimePeriod("03-17-2024", "03-31-2024", "WEEKS 1&2"),
            new TimePeriod("03-31-2024", "04-14-2024", "WEEKS 3&4"),
            new TimePeriod("04-14-2024", "04-28-2024", "WEEKS 5&6"),
            new TimePeriod("04-28-2024", "05-12-2024", "WEEKS 7&8"),
            new TimePeriod("05-12-2024", "05-26-2024", "WEEKS 9&10"),
            new TimePeriod("05-26-2024", "06-09-2024", "WEEKS 11&12"),
            new TimePeriod("06-09-2024", "06-23-2024", "WEEKS 13&14")
    );
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
    int totalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("FirebaseAuth", "No user logged in");
            return;
        }

        // initialize views
        firestore = FirebaseFirestore.getInstance();
        welcomeUser = findViewById(R.id.text_username);
        textSteps = findViewById(R.id.text_steps);
        progBarPercent = findViewById(R.id.text_progbar_percent);
        statusText = findViewById(R.id.text_m4wc2);
        weeksLabel = findViewById(R.id.text_m4wc_weeks);
        progBar = findViewById(R.id.progbar);
        progBar.setMin(0);
        progBar.setMax(100);
        textQuote = findViewById(R.id.text_quote);
        textQuoteAuthor = findViewById(R.id.text_quote_author);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateWeeksDisplay();
        updateQuote();

        String userEmail = currentUser.getEmail();
        firestore.collection("usersinfo").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("name");
                                if (username == null) username = userEmail;
                                welcomeUser.setText(username + "!");
                            } else {
                                Log.d("Document", "No such document");
                            }
                        } else {
                            Log.d("Database", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void updateWeeksDisplay() {
        Date currentDate = new Date();
        for (TimePeriod period : timePeriods) {
            if (period.isWithinRange(currentDate)) {
                weeksLabel.setText(period.getLabel());
                fetchWalkingSteps(period.getStartDate(), period.getEndDate());
                break;
            }
        }
    }

    private void fetchWalkingSteps(String startDateStr, String endDateStr) {
        email = currentUser.getEmail();

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

        firestore.collection("users").document(email).collection("walking")
                .whereGreaterThanOrEqualTo("Date", startDateStr)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String dateStr = document.getString("Date");
                            try {
                                Date date = inputDateFormat.parse(dateStr); // Parse the Firestore date
                                String formattedDate = outputDateFormat.format(date);
                                Date parsedDate = outputDateFormat.parse(formattedDate);
                                if (parsedDate != null && isWithinRange(parsedDate, outputDateFormat.parse(startDateStr), outputDateFormat.parse(endDateStr))) {
                                    String stepsStr = document.getString("Steps");
                                    totalSteps += Integer.parseInt(stepsStr != null ? stepsStr : "0");
                                }
                            } catch (ParseException e) {
                                Log.e("DateConversionError", "Error parsing date: " + dateStr, e);
                            }
                        }
                        textSteps.setText(String.valueOf(totalSteps)); // Set total steps
                        updateProgressDisplay();
                    } else {
                        Log.w("Firebase", "Error getting documents", task.getException());
                    }
                });
    }

    private boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    private void updateProgressDisplay() {
        int progressPercentage = (int) ((totalSteps / 100000.0) * 100);
        progressPercentage = Math.min(progressPercentage, 100);

        progBarPercent.setText(progressPercentage + "%");

        ObjectAnimator animation = ObjectAnimator.ofInt(progBar, "progress", 0, progressPercentage);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        if (totalSteps < 100000) {
            statusText.setText("of 100k steps.");
        } else {
            statusText.setText("challenge complete! Stay tuned for next challenge.");
        }
    }

    class TimePeriod {
        private String startDate;
        private String endDate;
        private String label;

        TimePeriod(String start, String end, String label) {
            this.startDate = start;
            this.endDate = end;
            this.label = label;
        }

        boolean isWithinRange(Date current) {
            try {
                Date start = dateFormatter.parse(this.startDate);
                Date end = dateFormatter.parse(this.endDate);
                return !current.before(start) && !current.after(end);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        String getStartDate() {
            return startDate;
        }

        String getEndDate() {
            return endDate;
        }

        String getLabel() {
            return label;
        }
    }

    private void updateQuote() {
        Calendar today = Calendar.getInstance();
        long lastUpdate = prefs.getLong("lastUpdate", 0);
        Calendar lastUpdateDate = Calendar.getInstance();
        lastUpdateDate.setTimeInMillis(lastUpdate);

        if (lastUpdate == 0 || today.get(Calendar.DAY_OF_YEAR) != lastUpdateDate.get(Calendar.DAY_OF_YEAR)) {
            Random random = new Random();
            int index = random.nextInt(Quote.quotes.size());
            Quote quote = Quote.quotes.get(index);

            textQuote.setText(quote.getQuote());
            textQuoteAuthor.setText("-" + quote.getAuthor());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastUpdate", today.getTimeInMillis());
            editor.putInt("lastQuoteId", index);
            editor.apply();
        } else {
            // display last shown quote if the day hasn't changed
            int lastQuoteId = prefs.getInt("lastQuoteId", 0);
            Quote quote = Quote.quotes.get(lastQuoteId);

            textQuote.setText(quote.getQuote());
            textQuoteAuthor.setText("-" + quote.getAuthor());
        }
    }


    public void changescreen(View view) {
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(HomeActivity.this, NotificationActivity.class);
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
    public void gomenu(View view){
        Intent intent = new Intent(HomeActivity.this, menu.class);
        startActivity(intent);
    }
}
