package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifyUser extends AppCompatActivity {


    FirebaseFirestore db;
    String email;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText title;
    EditText notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notify_user);

        db = FirebaseFirestore.getInstance();
        title = (EditText) findViewById(R.id.title);
        notification = (EditText) findViewById(R.id.notification);

    }




    public void goback(View view){
        ImageButton btn = (ImageButton) view;
        Intent intent = new Intent(NotifyUser.this, Manger_Menu.class);
        startActivity(intent);
    }

    public void sendnoti(View view){
        String titleinput = title.getText().toString().trim();
        String mes = notification.getText().toString().trim();
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(now);

        if(titleinput.isEmpty() || mes.isEmpty()) {
            Toast.makeText(NotifyUser.this, "Title and message cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> noti = new HashMap<>();
        noti.put("Title",titleinput);
        noti.put("Message",mes);
        noti.put("Time",formattedDate);

        db.collection("usersinfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getId(); // Document ID as email

                            db.collection("notifications")
                                    .document(email)
                                    .collection("notification")
                                    .document(formattedDate)
                                    .set(noti);
                        }
                    }
                });
        Toast.makeText(NotifyUser.this, "Notification sent to all users ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NotifyUser.this, Manger_Menu.class);
        startActivity(intent);
    }


}