package com.example.mygymy.Routine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.Userprofile.UserProfile;
import com.example.mygymy.model.Exercise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class workout extends AppCompatActivity {

    private TextView countdownTextView;
    private long startTime;
    private Handler handler;
    private RecyclerView rvExerciseList;
    private exerciseAdapter mAdapter;
    private Button finishBtn;
    private List<Exercise> selectedExercises;

    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);

        // Initialize the countdownTextView
        countdownTextView = findViewById(R.id.countdown_text);

        // Get the current system time
        startTime = SystemClock.elapsedRealtime();

        // Initialize Handler
        handler = new Handler();
        handler.postDelayed(updateTimer, 0); // Start the timer
        // Initialize RecyclerView and set its layout manager
        rvExerciseList = findViewById(R.id.exerciseList);
        rvExerciseList.setLayoutManager(new LinearLayoutManager(this));
        //iniitialize exerciseAdapter and set it to RecyclerView
        mAdapter = new exerciseAdapter(this, new ArrayList<>());
        rvExerciseList.setAdapter(mAdapter);

        // Get selected exercises from intent extras
        selectedExercises = (List<Exercise>) getIntent().getSerializableExtra("selectedExercises");
        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            // If there are selected exercises, update the adapter
            mAdapter.updateExercises(selectedExercises);
        }


        // Set click listener for finish workout button
        finishBtn = findViewById(R.id.finishworkout);
        finishBtn.setOnClickListener( view -> {
            // On click, increment workout count, navigate to UserProfile activity,
            // send workout completed broadcast and start notification service
            incrementWorkoutCount();
            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
            finish();
            sendWorkoutCompletedBc();
            Intent serviceIntent = new Intent(this, NotificationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(serviceIntent);

            }else {

                this.startService(serviceIntent);
            }

        });
    }
    // This method increments the workout count of the current user
    private void incrementWorkoutCount() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("UserProfile")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // If the task is successful, increment the workout count in the document
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    Long currentWorkoutCount = document.getLong("workouts");
                                    if (currentWorkoutCount != null) {
                                        db.collection("UserProfile")
                                                .document(documentId)
                                                .update("workouts", currentWorkoutCount + 1);
                                    }
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    private final Runnable updateTimer = new Runnable() {
        // This method is called every second to update the timer
        @SuppressLint("DefaultLocale")
        public void run() {
            // Calculate elapsed time in minutes and seconds
            long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
            int minutes = (int) (elapsedMillis / 1000) / 60;
            int seconds = (int) (elapsedMillis / 1000) % 60;
            // Format the elapsed time as "MM:SS"
            String timeFormatted = String.format("%02d:%02d", minutes, seconds);
            countdownTextView.setText(timeFormatted);
            // Update the countdownTextView with the formatted time
            handler.postDelayed(this, 1000);
            // Schedule the next update after 1 second
        }
    };
    public void sendWorkoutCompletedBc(){
        // This method sends a broadcast indicating that a workout has been completed
        Intent intent = new Intent("com.example.mygymy.WORKOUT_COMPLETED");
        sendBroadcast(intent);
    }
}
