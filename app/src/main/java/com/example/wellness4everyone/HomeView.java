package com.example.wellness4everyone;

import android.app.Activity;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class HomeView {
    private TextView textSteps, progBarPercent, statusText, weeksLabel, textQuote, textQuoteAuthor, welcomeUser;
    CircularProgressIndicator progBar;

    public HomeView(Activity activity) {
        // initializes UI elements
        welcomeUser = activity.findViewById(R.id.text_username);
        textSteps = activity.findViewById(R.id.text_steps);
        progBarPercent = activity.findViewById(R.id.text_progbar_percent);
        statusText = activity.findViewById(R.id.text_m4wc2);
        weeksLabel = activity.findViewById(R.id.text_m4wc_weeks);
        progBar = activity.findViewById(R.id.progbar);
        textQuote = activity.findViewById(R.id.text_quote);
        textQuoteAuthor = activity.findViewById(R.id.text_quote_author);
    }


    // setters and getters for UI
    public CircularProgressIndicator getProgBar() {
        return progBar;
    }

    public void setWelcomeUser(String username) {
        welcomeUser.setText(username + "!");
    }

    public void setTextSteps(String steps) {
        textSteps.setText(steps);
    }

    public void setProgBarPercent(String percent) {
        progBarPercent.setText(percent);
    }

    public void setStatusText(String status) {
        statusText.setText(status);
    }

    public void setWeeksLabel(String label) {
        weeksLabel.setText(label);
    }

    public void setProgBarMax(int max) {
        progBar.setMax(max);
    }

    public void setQuote(String quote, String author) {
        textQuote.setText(quote);
        textQuoteAuthor.setText("-" + author);
    }
}
