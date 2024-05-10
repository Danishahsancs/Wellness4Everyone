package com.example.wellness4everyone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends Activity {

    ListView lvItems;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    String email;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);
        lvItems = findViewById(R.id.lvItems);
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();


        setupListViewListener();
        loadItemsFromFirebase();

    }

    private void loadItemsFromFirebase() {
        db.collection("notifications")
                .document(email)
                .collection("notification")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {


                        items.clear();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            String time = document.getString("Time");
                            if (time.length() > 3) {
                                time = time.substring(0, time.length() - 3);
                            }
                            items.add(time + "\n\n" + document.getString("Message"));
                        }
                        itemsAdapter.notifyDataSetChanged();



                    }
                });
    }


    public void changescreen(View view) {
        ImageButton btn = (ImageButton) view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(NotificationActivity.this, Statspage.class);
                break;
            case "Activitiespage":
                intent = new Intent(NotificationActivity.this, Activitieslist.class);
                break;
            case "Homepage":
                intent = new Intent(NotificationActivity.this, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener((parent, view, position, id) -> {
            String itemToDelete = itemsAdapter.getItem(position); // Get the item to delete
            String[] parts = itemToDelete.split("\n\n");
            String messageToDelete = parts.length > 1 ? parts[1] : parts[0];


            new AlertDialog.Builder(NotificationActivity.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this notification?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // User clicked "Delete" button, delete the notification
                        deleteNotification(messageToDelete);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User clicked "Cancel" button, dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();

            return true; // Indicate that the click was handled
        });
    }

    private void deleteNotification(String messageToDelete) {
        db.collection("notifications")
                .document(email)
                .collection("notification")
                .whereEqualTo("Message", messageToDelete)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(NotificationActivity.this, "Notification deleted successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(NotificationActivity.this, "Error deleting notification", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(NotificationActivity.this, "Error finding notification to delete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}


