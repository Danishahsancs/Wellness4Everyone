package com.example.wellness4everyone;

import android.content.Intent;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class LoginSignup extends AppCompatActivity {
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
                        setupdatabase(email, usryear, usrname);
                        Toast.makeText(LoginSignup.this, "Account created.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginSignup.this, HomeActivity.class);
                        intent.putExtra("USERNAME_KEY", usrname); // enables username to be passed through different pages
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
                        if(email.equalsIgnoreCase("danish28436@gmail.com")){
                            Toast.makeText(LoginSignup.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginSignup.this, Manger_Menu.class);
                            startActivity(intent);
                        }else {
                        Toast.makeText(LoginSignup.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginSignup.this, HomeActivity.class);
                        // intent.putExtra("USERNAME_KEY", usrname); (how to get username from login)
                        startActivity(intent);
                        }
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginSignup.this, "Authentication failed: \n", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
    public void setupdatabase(String email,String year,String name){
        Map<String, Object> emptyData = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email",email);
        userInfo.put("name", name);
        userInfo.put("participation year", year);


        db.collection("users").document(email).collection("walking").document("total").set(emptyData);
        db.collection("users").document(email).collection("running").document("total").set(emptyData);
        db.collection("users").document(email).collection("swimming").document("total").set(emptyData);
        db.collection("users").document(email).collection("weightlifting").document("total").set(emptyData);
        db.collection("users").document(email).collection("biking").document("total").set(emptyData);
        db.collection("usersinfo").document(email).set(userInfo);

    }

}