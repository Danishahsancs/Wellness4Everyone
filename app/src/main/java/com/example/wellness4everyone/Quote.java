package com.example.wellness4everyone;

import java.util.ArrayList;
import java.util.List;

public class Quote {
    private int id;
    private String quote;
    private String author;

    public Quote(int id, String quote, String author) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public static final List<Quote> quotes = new ArrayList<>();

    static {
        quotes.add(new Quote(1,
                "I hated every minute of training, but I said, ‘Don’t quit. Suffer now and live the rest of your life as a champion.",
                "Muhammad Ali"));
        quotes.add(new Quote(2,
                "We are what we repeatedly do. Excellence then is not an act but a habit.",
                "Aristotele"));
        quotes.add(new Quote(3,
                "The body achieves what the mind believes.",
                "Napoleon Hill"));
        quotes.add(new Quote(4,
                "The hard days are the best because that’s when champions are made, so if you push through, you can push through anything.",
                "Dana Vollmer"));
        quotes.add(new Quote(5,
                "If you don’t find the time, if you don’t do the work, you don’t get the results.",
                "Arnold Schwarzenegger"));
        quotes.add(new Quote(6,
                "Dead last finish is greater than did not finish, which trumps did not start.",
                "Anonymous"));
        quotes.add(new Quote(7,
                "Push harder than yesterday if you want a different tomorrow.",
                "Vincent Williams Sr."));
        quotes.add(new Quote(8,
                "The real workout starts when you want to stop.",
                "Ronnie Coleman"));
        quotes.add(new Quote(9,
                "Take care of your body. It’s the only place you have to live.",
                "Jim Rohn"));
        quotes.add(new Quote(10,
                "I’ve failed over and over again in my life and that is why I succeed.",
                "Michael Jordan"));
    }
}
