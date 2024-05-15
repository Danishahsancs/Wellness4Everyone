package com.example.wellness4everyone;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeController {
    private HomeModel homeModel;
    private HomeView homeView;
    private int totalSteps = 0;
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    public HomeController(HomeModel homeModel, HomeView homeView) {
        this.homeModel = homeModel;
        this.homeView = homeView;
    }

    // initializes controller
    public void initialize() {
        homeView.setProgBarMax(100);
        updateWeeksDisplay();
        updateQuote();

        String userEmail = homeModel.getCurrentUser().getEmail();
        // retrieves user info from firestore
        homeModel.getFirestore().collection("usersinfo").document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("name");
                            if (username == null) username = userEmail;
                            homeView.setWelcomeUser(username);
                        } else {
                            Log.d("Document", "No such document");
                        }
                    } else {
                        Log.d("Database", "get failed with ", task.getException());
                    }
                });
    }

    // updates the challenge weeks display for the current 2 week cycle
    private void updateWeeksDisplay() {
        Date currentDate = new Date();
        // loops through defined time periods to find the current date range
        for (HomeModel.TimePeriod period : homeModel.getTimePeriods()) {
            if (period.isWithinRange(currentDate)) {
                homeView.setWeeksLabel(period.getLabel());
                fetchWalkingSteps(period.getStartDate(), period.getEndDate());
                break;
            }
        }
    }

    // gets walking steps from firestore for the given date range from updateWeeksDisplay()
    private void fetchWalkingSteps(String startDateStr, String endDateStr) {
        String email = homeModel.getCurrentUser().getEmail();

        homeModel.getFirestore().collection("users").document(email).collection("walking")
                .whereGreaterThanOrEqualTo("Date", startDateStr)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String dateStr = document.getString("Date");
                            try {
                                Date date = inputDateFormat.parse(dateStr); // parses firestore date
                                String formattedDate = outputDateFormat.format(date); // reformats date from firestore to match MM-dd-yyyy format
                                Date parsedDate = outputDateFormat.parse(formattedDate); // parses updated date format
                                // checks if date is within the range
                                if (parsedDate != null && isWithinRange(parsedDate, outputDateFormat.parse(startDateStr), outputDateFormat.parse(endDateStr))) {
                                    String stepsStr = document.getString("Steps");
                                    totalSteps += Integer.parseInt(stepsStr != null ? stepsStr : "0");
                                }
                            } catch (ParseException e) {
                                Log.e("DateConversionError", "Error parsing date: " + dateStr, e);
                            }
                        }
                        homeView.setTextSteps(String.valueOf(totalSteps)); // Set total steps
                        updateProgressDisplay();
                    } else {
                        Log.w("Firebase", "Error getting documents", task.getException());
                    }
                });
    }

    // checks if the date is within the specified range
    private boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    // updates the progress bar display
    private void updateProgressDisplay() {
        int progressPercentage = (int) ((totalSteps / 100000.0) * 100);
        progressPercentage = Math.min(progressPercentage, 100);

        homeView.setProgBarPercent(progressPercentage + "%"); // sets progress percentage text

        // animates the progress bar to fill to its current progress %
        ObjectAnimator animation = ObjectAnimator.ofInt(homeView.getProgBar(), "progress", 0, progressPercentage);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        if (totalSteps < 100000) {
            homeView.setStatusText("of 100k steps.");
        } else {
            homeView.setStatusText("challenge complete! Stay tuned for next challenge.");
        }
    }

    // updates quote view daily
    private void updateQuote() {
        Calendar today = Calendar.getInstance();
        long lastUpdate = homeModel.getLastUpdate();
        Calendar lastUpdateDate = Calendar.getInstance();
        lastUpdateDate.setTimeInMillis(lastUpdate);

        // checks if quote needs to be updated
        if (lastUpdate == 0 || today.get(Calendar.DAY_OF_YEAR) != lastUpdateDate.get(Calendar.DAY_OF_YEAR)) {
            Quote quote = homeModel.getRandomQuote();
            homeView.setQuote(quote.getQuote(), quote.getAuthor());
            homeModel.saveQuoteIndex(quote.getId());
        } else {
            // display last shown quote if the day hasn't changed
            int lastQuoteId = homeModel.getLastQuoteIndex();
            Quote quote = Quote.quotes.get(lastQuoteId);
            homeView.setQuote(quote.getQuote(), quote.getAuthor());
        }
    }

    // handles navigation to the menu screen
    public void goToMenu(View view){
        Intent intent = new Intent(view.getContext(), menu.class);
        view.getContext().startActivity(intent);
    }
}
