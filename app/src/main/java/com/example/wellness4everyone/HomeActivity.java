package com.example.wellness4everyone;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private HomeController homeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // initializes model, view, and controller
        HomeModel homeModel = new HomeModel(PreferenceManager.getDefaultSharedPreferences(this));
        HomeView homeView = new HomeView(this);
        homeController = new HomeController(homeModel, homeView);

        homeController.initialize();
    }

    // changes screen when button is clicked
    public void changescreen(View view) {
        ScreenNavigator.navigate(this, view);
    }

    // opens menu when button is clicked
    public void gomenu(View view) {
        homeController.goToMenu(view);
    }
}

