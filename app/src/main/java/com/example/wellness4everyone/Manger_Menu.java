package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Manger_Menu extends AppCompatActivity {
    ImageButton UA;
    ImageButton GS;
    ImageButton NU;
    FirebaseAuth mAuth;
    TextView numofusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manger_menu);
        UA = (ImageButton) findViewById(R.id.button_user_activities);
        GS =(ImageButton) findViewById(R.id.button_group_stats);
        NU = (ImageButton) findViewById(R.id.button_notify_user);
        numofusers = (TextView) findViewById(R.id.userAmmount);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersinfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int temp = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getId(); // Document ID as email
                            temp++;
                        }
                        numofusers.setText(String.valueOf(temp));
                    }

                });
    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "UserActivities":
                intent = new Intent(Manger_Menu.this, UserActivities.class);
                break;
            case "GroupStats":
                intent = new Intent(Manger_Menu.this, GroupStats.class);
                break;
            case "NotifyUser":
                intent = new Intent(Manger_Menu.this, NotifyUser.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);
    }
    public void logout (View view){
        mAuth.getInstance().signOut();
        // After logout, redirect to Login Activity
        startActivity(new Intent(Manger_Menu.this, LoginSignup.class));
        finish();  // Finish the current activity to p
    }

}