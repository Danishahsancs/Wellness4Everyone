package com.example.wellness4everyone;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends Activity {

    Button welcomeWidget;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private StepsAdapter adapter;
    private ArrayList<StepEntry> stepEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // initializes firestone
        db = FirebaseFirestore.getInstance();
        initializeRecycler();

        fetchStepData();

        welcomeWidget = (Button) findViewById(R.id.text_welcome);
        String welcomeText = "<big><b>Welcome back!</b></big><br/><br/>" +
                "<small>Kick off your day by starting a new activity.</small>";
        welcomeWidget.setText(Html.fromHtml(welcomeText, Html.FROM_HTML_MODE_LEGACY));

        welcomeWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddActivityActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initializeRecycler() {
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_m4wcrank);
        stepEntries = new ArrayList<>();
        adapter = new StepsAdapter(stepEntries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchStepData() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userEmail = doc.getId(); // Get the user email as document ID
                        db.collection("users")
                                .document(userEmail)
                                .collection("walking")
                                .document("total")
                                .get()
                                .addOnSuccessListener(docInner -> {
                                    long steps = docInner.contains("Steps") ? docInner.getLong("Steps") : 0;
                                    stepEntries.add(new StepEntry(userEmail, steps));
                                    stepEntries.sort((o1, o2) -> Long.compare(o2.getSteps(), o1.getSteps()));
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Error fetching steps for " + userEmail, e);
                                    stepEntries.add(new StepEntry(userEmail, 0)); // Handle the case where data couldn't be fetched
                                    adapter.notifyDataSetChanged();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user documents", e);
                });
    }

    public class StepEntry {
        private String userEmail;
        private long steps;

        public StepEntry(String userEmail, long steps) {
            this.userEmail = userEmail;
            this.steps = steps;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public long getSteps() {
            return steps;
        }
    }

    public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
        private ArrayList<StepEntry> stepEntries;

        public StepsAdapter(ArrayList<StepEntry> stepEntries) {
            this.stepEntries = stepEntries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StepEntry entry = stepEntries.get(position);
            holder.userEmail.setText(entry.getUserEmail());
            holder.steps.setText(String.valueOf(entry.getSteps()));
            holder.rank.setText(String.valueOf(position + 1));
        }

        @Override
        public int getItemCount() {
            return stepEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView userEmail, steps, rank;

            public ViewHolder(View itemView) {
                super(itemView);
                userEmail = itemView.findViewById(R.id.email);
                steps = itemView.findViewById(R.id.steps);
                rank = itemView.findViewById(R.id.rank);
            }
        }
    }

    public void changescreen(View view){
        ImageButton btn = (ImageButton)  view;
        String tag = btn.getTag().toString();

        Intent intent;
        switch (tag) {
            case "Notificationpage":
                intent = new Intent(HomeActivity.this,NotificationActivity.class);
                break;
            case "Statspage":
                intent = new Intent(HomeActivity.this, Statspage.class);
                break;
            case "Activitiespage":
                intent = new Intent(HomeActivity.this, Activitieslist.class);
                break;
            case "Homepage":
                intent = new Intent(HomeActivity.this, HomeActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unexpected tag: " + tag);
        }
        startActivity(intent);

    }



}
