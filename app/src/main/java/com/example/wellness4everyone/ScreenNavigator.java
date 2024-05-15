package com.example.wellness4everyone;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

// uses facade pattern for navigating between screens
public class ScreenNavigator {
    // navigates to different screens based on ImageButton tag
    public static void navigate(Activity activity, View view) {
        ImageButton btn = (ImageButton) view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(activity, NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(activity, Statspage.class);
                break;
            case "Activitiespage":
                intent = new Intent(activity, Activitieslist.class);
                break;
            case "Homepage":
                intent = new Intent(activity, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        activity.startActivity(intent);
    }
}
