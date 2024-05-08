package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserActivities extends AppCompatActivity {

    FirebaseFirestore db;
    LinearLayout usrbuttonlayout;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_activities);
        db = FirebaseFirestore.getInstance();
        usrbuttonlayout = (LinearLayout) findViewById(R.id.userlistlayout);

        db.collection("usersinfo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getId();
                                String username = document.getString("name");

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 0, 0, 16);

                                Button usrbtn = new Button(UserActivities.this);
                                usrbtn.setLayoutParams(params);
                                usrbtn.setText(username + "\n("+email+") ");
                                usrbtn.setTextColor(ContextCompat.getColorStateList(UserActivities.this, R.color.primarycolor));
                                usrbtn.setBackground(ContextCompat.getDrawable(UserActivities.this, R.drawable.roundedcorner));

                                usrbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(UserActivities.this, Statspage_manager.class);
                                        intent.putExtra("EMAIL", email);
                                        startActivity(intent);
                                    }
                                });
                                usrbuttonlayout.addView(usrbtn);
                            }
                        }
                    }
                });
    }

    public void goback(View view){
        ImageButton btn = (ImageButton) view;
        Intent intent = new Intent(UserActivities.this, Manger_Menu.class);
        startActivity(intent);
    }
}

