package com.example.wellness4everyone;

import java.util.ArrayList;
import java.util.List;

// class to store quotes, quote id, and quote author
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
        quotes.add(new Quote(11,
                "Once you are exercising regularly, the hardest thing is to stop it.",
                "Erin Gray"));
        quotes.add(new Quote(12,
                "The secret of getting ahead is getting started.",
                "Mark Twain"));
        quotes.add(new Quote(13,
                "Exercise should be regarded as tribute to the heart.",
                "Gene Tunney"));
        quotes.add(new Quote(14,
                "Most people fail, not because of lack of desire, but, because of lack of commitment.",
                "Vince Lombardi"));
        quotes.add(new Quote(15,
                "You’re going to have to let it hurt. Let it suck. The harder you work, the better you will look. Your appearance isn’t parallel to how heavy you lift, it’s parallel to how hard you work.",
                "Joe Mangianello"));
        quotes.add(new Quote(16,
                "You miss one hundred percent of the shots you don’t take.",
                "Wayne Gretzky"));
        quotes.add(new Quote(17,
                "If something stands between you and your success, move it. Never be denied.",
                "Dwayne (The Rock) Johnson"));
        quotes.add(new Quote(18,
                "All progress takes place outside the comfort zone.",
                "Michael John Bobak"));
        quotes.add(new Quote(19,
                "Just believe in yourself. Even if you don’t, just pretend that you do and at some point, you will",
                "Venus Williams"));
        quotes.add(new Quote(20,
                "The harder you work and the more prepared you are for something, you’re going to be able to persevere through anything.",
                "Carli Lloyd"));
    }
}
