package com.example.wellness4everyone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;



public class LoginSignup extends AppCompatActivity {
    private static final int NOTIFICATION_REQUEST_CODE = 100; // You can use any integer here

    Switch toggle;

    EditText name;
    TextView year;
    Button lSbutton;
    EditText emailInput;
    EditText passwordInput;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_signup);
        toggle = (Switch) findViewById(R.id.Signupswitch);
        name = (EditText) findViewById(R.id.name);
        year = (TextView) findViewById(R.id.Yearjoin);
        emailInput = (EditText) findViewById(R.id.EmailAddress);
        passwordInput = (EditText) findViewById(R.id.Password);
        lSbutton = (Button) findViewById(R.id.LSButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        year.setText("Year of Participation: "+ Calendar.getInstance().get(Calendar.YEAR));

    }

    public void signuptoggle(View view) {
        boolean isChecked = toggle.isChecked();
        if (isChecked) {
            year.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            toggle.setText("Toggle to Login");
            lSbutton.setText("Sign Up");
        } else {
            year.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            toggle.setText("Toggle to Signup");
            lSbutton.setText("Login");
        }
    }

    public void loginsignup(View view) {
        String email, password, usrname, usryear;
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        usrname = name.getText().toString();
        usryear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        boolean isChecked = toggle.isChecked();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginSignup.this, "enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginSignup.this, "enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isChecked) {
            if (TextUtils.isEmpty(usrname)) {
                Toast.makeText(LoginSignup.this, "enter name", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        requestNotificationPermission();
                        setupDatabase(email, usryear, usrname);
                        Toast.makeText(LoginSignup.this, "Account created.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginSignup.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginSignup.this, "Authentication failed: \n", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> tokenTask) {
                                if (tokenTask.isSuccessful()) {
                                    String newToken = tokenTask.getResult();

                                    db.collection("usersinfo").document(email).update("fcmToken", newToken);
                                }
                                if(email.equalsIgnoreCase("danish28436@gmail.com")){
                                    Toast.makeText(LoginSignup.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginSignup.this, Manger_Menu.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginSignup.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginSignup.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginSignup.this, "Authentication failed: \n", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers");
        if(email.equalsIgnoreCase("Danish28436@gmail.com")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic("allUsers");
        }
    }

    public void setupDatabase(String email, String year, String name) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Successfully retrieved the token
                    String token = task.getResult();

                    // Preparing user info with token
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("email", email);
                    userInfo.put("name", name);
                    userInfo.put("participation year", year);
                    userInfo.put("fcmToken", token);  // Adding the FCM token

                    // Storing user info in Firestore
                    db.collection("usersinfo").document(email).set(userInfo);

                    // Set empty data for activity collections
                    Map<String, Object> emptyData = new HashMap<>();
                    db.collection("users").document(email).collection("walking").document("total").set(emptyData);
                    db.collection("users").document(email).collection("running").document("total").set(emptyData);
                    db.collection("users").document(email).collection("swimming").document("total").set(emptyData);
                    db.collection("users").document(email).collection("weightlifting").document("total").set(emptyData);
                    db.collection("users").document(email).collection("biking").document("total").set(emptyData);


                } else {
                    // Handle the exception
                    Toast.makeText(LoginSignup.this, "Failed to fetch FCM token: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void requestNotificationPermission() {
        Toast.makeText(this, "request noti.", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "sdk is valid", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);
        }
        // On older Android versions, notifications are allowed by default.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }



}