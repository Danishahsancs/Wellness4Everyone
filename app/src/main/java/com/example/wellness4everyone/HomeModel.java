package com.example.wellness4everyone;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SharedPreferences prefs;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    public HomeModel(SharedPreferences prefs) {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        this.prefs = prefs;
    }

    // returns current users
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // returns firestore instance
    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    // returns predefined time periods
    public List<TimePeriod> getTimePeriods() {
        return Arrays.asList (
                new TimePeriod("03-17-2024", "03-31-2024", "WEEKS 1&2"),
                new TimePeriod("03-31-2024", "04-14-2024", "WEEKS 3&4"),
                new TimePeriod("04-14-2024", "04-28-2024", "WEEKS 5&6"),
                new TimePeriod("04-28-2024", "05-12-2024", "WEEKS 7&8"),
                new TimePeriod("05-12-2024", "05-26-2024", "WEEKS 9&10"),
                new TimePeriod("05-26-2024", "06-09-2024", "WEEKS 11&12"),
                new TimePeriod("06-09-2024", "06-23-2024", "WEEKS 13&14")
        );
    }

    // returns quote at the generated index
    public Quote getRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(Quote.quotes.size());
        return Quote.quotes.get(index);
    }

    // saves the index of the last displayed quote
    public void saveQuoteIndex(int index) {
        Calendar today = Calendar.getInstance();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastUpdate", today.getTimeInMillis());
        editor.putInt("lastQuoteId", index);
        editor.apply();
    }

    // returns the index of the last displayed quote
    public int getLastQuoteIndex() {
        return prefs.getInt("lastQuoteId", 0);
    }

    // returns the timestamp of the last quote update
    public long getLastUpdate() {
        return prefs.getLong("lastUpdate", 0);
    }

    // inner class that represents a time period
    public class TimePeriod {
        private String startDate;
        private String endDate;
        private String label;

        public TimePeriod(String start, String end, String label) {
            this.startDate = start;
            this.endDate = end;
            this.label = label;
        }

        // checks if a given date is within the time period
        public boolean isWithinRange(Date current) {
            try {
                Date start = dateFormatter.parse(this.startDate);
                Date end = dateFormatter.parse(this.endDate);
                return !current.before(start) && !current.after(end);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        // getters for start date, end date, and challenge label
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

}
