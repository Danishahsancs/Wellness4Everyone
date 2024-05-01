package com.example.wellness4everyone;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<String>items;
    ArrayAdapter<String>itemsAdapter;
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


        //setupListViewListener();
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
                            items.add(document.getString("Time") + "\n\n" + document.getString("Title") + "\n\t\t\t" + document.getString("Message"));
                        }
                        itemsAdapter.notifyDataSetChanged();

                        // Check if there are new notifications and update UI accordingly
                        if (!snapshots.isEmpty()) {
                            Toast.makeText(NotificationActivity.this, " Notification received from manager", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(NotificationActivity.this,NotificationActivity.class);
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
}

