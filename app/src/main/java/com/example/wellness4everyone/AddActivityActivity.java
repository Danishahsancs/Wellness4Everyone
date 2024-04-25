package com.example.wellness4everyone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import java.util.Calendar;


public class AddActivityActivity extends Activity {

    ImageButton backButton;
    Button dateButton;
    TextView summary;
    String activityTag;

    EditText enterMins,enterSteps;
    int year, month, day;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addactivity_screen);


        Intent intent = getIntent();
        activityTag = intent.getStringExtra("ACTIVITY_TAG");
        changeanimation(activityTag);
        // sets up back button
        backButton = (ImageButton) findViewById(R.id.button_back);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateButton = (Button) findViewById(R.id.button_selectdate);
        summary = (TextView) findViewById(R.id.activitysummary);
        enterMins =(EditText) findViewById(R.id.enter_mins);
        enterSteps =(EditText) findViewById(R.id.enter_steps);

        setupTextWatchers();
        if(activityTag.equals("walking")|| activityTag.equals("running")){
           enterSteps.setVisibility(View.VISIBLE);
        }else{
            enterSteps.setVisibility(View.GONE);
        }
    }

    private void changeanimation(String activityTag) {
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
        int animationResource;

        switch (activityTag) {
            case "walking":
                lottieAnimationView.setAnimation(R.raw.walking_animation);
                lottieAnimationView.playAnimation();
                break;
            case "running":
                lottieAnimationView.setAnimation(R.raw.running_animation);
                lottieAnimationView.playAnimation();
                break;
            case "weightlifting":
                lottieAnimationView.setAnimation(R.raw.weightlifting_animation);
                lottieAnimationView.playAnimation();
                break;
            case "swimming":
                lottieAnimationView.setAnimation(R.raw.swimming_animation);
                lottieAnimationView.playAnimation();
                break;
            case "biking":
                lottieAnimationView.setAnimation(R.raw.biking_animation);
                lottieAnimationView.playAnimation();
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + activityTag);
        }
    }

    public void goback(View view){
        Intent intent = new Intent(AddActivityActivity.this, Activitieslist.class);
        startActivity(intent);
    }
    public void setdate(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivityActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearx, int monthOfYear, int dayOfMonth) {
                        year = yearx;
                        month = monthOfYear;
                        day = dayOfMonth;
                        updateResults();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement this
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateResults();
            }
        };

        // Attach the text watcher to both EditText fields
        enterMins.addTextChangedListener(textWatcher);
        enterSteps.addTextChangedListener(textWatcher);

    }

    private void updateResults() {
        String mins = enterMins.getText().toString().trim();
        String steps = enterSteps.getText().toString().trim();

        StringBuilder sb = new StringBuilder();
        sb.append("Summary:");
        sb.append("\n\t\t\t\t"+month+"/"+day+"/"+year);
        if(!mins.isEmpty()) {
            sb.append("\n\t\t\t\tNumber of minutes " + activityTag + ": " + mins);
        }
        if((activityTag.equals("walking")|| activityTag.equals("running")) && !steps.isEmpty()){
            sb.append("\n\t\t\t\tNumber of steps " + activityTag+": "+steps);
        }

        summary.setText(sb);
    }
}

