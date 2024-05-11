package com.example.wellness4everyone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class menu extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
    }

    public void logout (View view){
        mAuth.getInstance().signOut();
        // After logout, redirect to Login Activity
        startActivity(new Intent(menu.this, LoginSignup.class));
        finish();  // Finish the current activity to p
    }
    public void goback(View view){
        ImageButton btn = (ImageButton) view;
        Intent intent = new Intent(menu.this, HomeActivity.class);
        startActivity(intent);
    }

    public void sendPasswordReset(View view) {
        String email = mAuth.getCurrentUser().getEmail();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(menu.this, "Reset link sent to "+email, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(menu.this, "Failed to send reset to "+email, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    public void changeName(String name){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("usersinfo")
                .document(mAuth.getCurrentUser().getEmail())
                .update("name",name);
        Toast.makeText(menu.this, "Change name to "+name, Toast.LENGTH_SHORT).show();
    }

    public void showChangeNameDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Name");

        final EditText input = new EditText(this);
        input.setHint("Enter new name");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (!newName.isEmpty()) {
                    changeName(newName);
                } else {
                    Toast.makeText(menu.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}