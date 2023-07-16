package com.example.mygymy.Userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;

import com.example.mygymy.Routine.PostsWorkouts;
import com.example.mygymy.R;
import com.example.mygymy.Routine.Routine;
import com.example.mygymy.Settings.Settings;
import com.example.mygymy.model.UserProfileData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Map;

public class UserProfile extends AppCompatActivity {
    private static final int REQUEST_OAUTH_REQUEST_CODE =1 ;
    // Instantiate Firebase Authentication instance
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button settings;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference workoutsRef = db.collection("UserProfile");
    private GoogleFitHelper googleFitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Get the current logged-in user
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            fetchUserData(userEmail); // Fetch user's data from Firestore

            // Set email and workouts completed text views
            TextView email = findViewById(R.id.useremail);
            email.setText(currentUser.getEmail());
            TextView workoutsCompleted = findViewById(R.id.workouts_completed);
        }
        // Initialize image buttons and set click listeners
        ImageButton userprofile = findViewById(R.id.button1);
        ImageButton newsfeed = findViewById(R.id.button2);
        ImageButton shield = findViewById(R.id.button3);
        ImageButton shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app: https://mygymmy.com");
                startActivity(Intent.createChooser(shareIntent, "Share app via"));

                ShareActionProvider shareActionProvider = new ShareActionProvider(UserProfile.this);
                shareActionProvider.setShareIntent(shareIntent);
            }
        });

        settings = findViewById(R.id.settings_button);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        shield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Routine.class);
                startActivity(intent);
            }
        });
        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostsWorkouts.class);
                startActivity(intent);
            }
        });
        // Query Firestore to fetch workout data and display it in a bar chart
            BarChart barChart = findViewById(R.id.chart);
            workoutsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    // Handle successful document fetch
                    // Create lists to hold data for the chart
                    ArrayList<BarEntry> workouts = new ArrayList<>();
                    ArrayList<String>labels = new ArrayList<>();
                    int i = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        Map<String,Object> workoutData = document.getData();
                        // Create BarDataSet and BarData objects and configure the chart
                        Long NoOfWorkouts = (Long) workoutData.get("workouts");
                        Timestamp timestamp =document.getTimestamp("dates");
                        if (NoOfWorkouts!=null && timestamp != null) {
                            float x = timestamp.toDate().getTime();
                            labels.add(new SimpleDateFormat("MMM").format(timestamp.toDate()));
                            int y = Integer.parseInt(String.valueOf(NoOfWorkouts));
                            workouts.add(new BarEntry(i, y));
                            i++;
                        }
                    }

                    BarDataSet barDataSet = new BarDataSet(workouts,"Workouts");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Workouts per Month");
                    barChart.animateY(2000);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setLabelCount(labels.size());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle error
                }
            });
        }

    private void fetchUserData(String userEmail) {
        // Get a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the "UserProfile" collection where the email field matches the current user's email
        db.collection("UserProfile")
                .whereEqualTo("email", userEmail)
                .get()  // Fetch the data
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // This method will be called once the data has been fetched
                        if (task.isSuccessful()) {
                            // If the fetch was successful...
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Loop over the documents in the result
                                // Get the ID of the current document
                                String userId = document.getId();
                                // Create a reference to the document with the fetched ID in the "UserProfile" collection
                                DocumentReference userRef = db.collection("UserProfile").document(userId);
                                // Attach a snapshot listener to the document reference
                                userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            // If there was an error attaching the listener, log the error and return
                                            Log.w("TAG", "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            // If the snapshot is not null and the document exists
                                            // Convert the snapshot to a UserProfileData object
                                            UserProfileData userProfileData = snapshot.toObject(UserProfileData.class);

                                            // Update the UI with the fetched user data
                                            updateUI(userProfileData);

                                            // Update the graph
                                            updateGraph();
                                        } else {
                                            // If the snapshot is null or the document does not exist, log a message
                                            Log.d("TAG", "Current data: null");
                                        }
                                    }
                                });
                            }
                        } else {
                            // If the fetch was not successful, log the error
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    //update graph after workout has finished
    private void updateGraph() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();// Get the current authenticated user
        CollectionReference workoutsRef = db.collection("UserProfile");
        BarChart barChart = findViewById(R.id.chart);

        // If the current user is not null...
        if (currentUser != null) {
            // Get the current user's email
            String userEmail = currentUser.getEmail();
            workoutsRef.whereEqualTo("email", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    // Create a new ArrayList to hold BarEntry objects for the workouts
                    ArrayList<BarEntry> workouts = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();

                    // Initialize a counter for the x-values of the bar entries
                    int i = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the workout data from the document as a Map object
                        Map<String, Object> workoutData = document.getData();
                        Long NoOfWorkouts = (Long) workoutData.get("workouts");
                        Timestamp timestamp = document.getTimestamp("dates");

                        // If both the number of workouts and the timestamp are not null...
                        if (NoOfWorkouts != null && timestamp != null) {
                            float x = timestamp.toDate().getTime();// Get the time of the timestamp as a float
                            labels.add(new SimpleDateFormat("MMM").format(timestamp.toDate()));
                            int y = Integer.parseInt(String.valueOf(NoOfWorkouts));
                            workouts.add(new BarEntry(i, y));
                            i++;
                        }
                    }

                    BarDataSet barDataSet = new BarDataSet(workouts, "Workouts");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);
                    barChart.setFitBars(true); // Configure the bar chart to fit the bars and set its data
                    barChart.setData(barData);
                    barChart.getDescription().setText("Workouts per Month");
                    barChart.animateY(2000);
                    // Set the value formatter, position, granularity, and label count for the x-axis
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setLabelCount(labels.size());
                    barChart.invalidate(); // Refresh the chart
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle error
                }
            });
        }
    }

    // Function to update the UI with the fetched user data
    private void updateUI(UserProfileData userProfileData) {
        TextView workoutsCompleted = findViewById(R.id.workouts_completed);
        TextView email = findViewById(R.id.useremail);
        workoutsCompleted.setText(String.valueOf(userProfileData.getWorkouts()));
        workoutsCompleted.invalidate();

    }
    // Function to update the bar chart when there's new workout data
    public void updateWorkoutsAndGraph(){

    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser !=null){
        String userEmail = currentUser.getEmail();
        fetchUserData(userEmail);
        TextView email = findViewById(R.id.useremail);
        email.setText(currentUser.getEmail());

    }


    }
    // BroadcastReceiver to listen for when a workout is completed
    private BroadcastReceiver workoutCompletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.example.mygymy.WORKOUT_COMPLETED")){
                updateWorkoutsAndGraph();
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        // Register the BroadcastReceiver when the activity is resumed
        IntentFilter filter = new IntentFilter("com.example.mygymy.WORKOUT_COMPLETED");
        registerReceiver(workoutCompletedReceiver,filter);
        updateWorkoutsAndGraph();


    }
    protected void onPause() {
        // Unregister the BroadcastReceiver when the activity is paused
        super.onPause();
        unregisterReceiver(workoutCompletedReceiver);
    }

}
