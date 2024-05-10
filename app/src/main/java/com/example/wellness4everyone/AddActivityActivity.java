package com.example.wellness4everyone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddActivityActivity extends Activity {

    ImageButton backButton;
    Button dateButton;
    TextView summary;
    String activityTag, email;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();
    }

    private void changeanimation(String activityTag) {
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);


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
        DatePicker datePicker = datePickerDialog.getDatePicker();


        datePicker.setMaxDate(System.currentTimeMillis());


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
        sb.append("\n\t\t\t\t"+(month+1)+"/"+day+"/"+year);
        if(!mins.isEmpty()) {
            sb.append("\n\t\t\t\tNumber of minutes " + activityTag + ": " + mins);
        }
        if((activityTag.equals("walking")|| activityTag.equals("running")) && !steps.isEmpty()){
            sb.append("\n\t\t\t\tNumber of steps " + activityTag+": "+steps);
        }

        summary.setText(sb);
    }

    public void saveworkout(View view){
        Map<String, Object> workout = new HashMap<>();
        String date = ((month+1) + "-" + day + "-" + year); // Use for document ID
        workout.put("Date", date); // Use the formatted date as the value
        workout.put("Minutes", enterMins.getText().toString().trim());

        if(activityTag.equals("walking")|| activityTag.equals("running")){
            workout.put("Steps", enterSteps.getText().toString().trim());
        }


        db.collection("users")
                .document(email)
                .collection(activityTag)
                .document(date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int minutes = Integer.parseInt(document.getString("Minutes"));
                        int tempMins = minutes+ Integer.parseInt(enterMins.getText().toString().trim());
                        workout.put("Minutes",String.valueOf(tempMins));

                        if(activityTag.equals("walking")|| activityTag.equals("running")){
                            int steps = Integer.parseInt(document.getString("Steps"));
                            int tempSteps =steps + Integer.parseInt(enterSteps.getText().toString().trim());
                            workout.put("Steps",String.valueOf(tempSteps));
                        }

                    }
                    db.collection("users")
                            .document(email)
                            .collection(activityTag)
                            .document(date).set(workout);
                    Toast.makeText(AddActivityActivity.this, activityTag + " activity added\n", Toast.LENGTH_SHORT).show();
                    updatetotal(date,activityTag,workout);
                    Intent intent = new Intent(AddActivityActivity.this, Activitieslist.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void updatetotal(String date, String activityTag, Map<String, Object> workout) {

        String formattedDate = reformatDateForComparison(date);

        db.collection(activityTag)
                .document(date)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                int minutes = Integer.parseInt(document.getString("Minutes"));
                                int tempMins = minutes+ Integer.parseInt(enterMins.getText().toString().trim());
                                workout.put("Minutes",String.valueOf(tempMins));

                                if(activityTag.equals("walking")|| activityTag.equals("running")){
                                    int steps = Integer.parseInt(document.getString("Steps"));
                                    int tempSteps =steps + Integer.parseInt(enterSteps.getText().toString().trim());
                                    workout.put("Steps",String.valueOf(tempSteps));
                                }

                            }
                            workout.put("Date",formattedDate);

                            db.collection(activityTag)
                                    .document(date)
                                    .set(workout);
                        }
                    }
                });
    }

    private String reformatDateForComparison(String originalDate) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = originalFormat.parse(originalDate);
            assert date != null;
            return targetFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateError", "Error parsing date: " + originalDate, e);
            return null;
        }
    }

}

