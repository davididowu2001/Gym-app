package com.example.mygymy.Routine;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mygymy.R;
import com.example.mygymy.Userprofile.UserProfile;
import com.example.mygymy.model.Exercise;
import com.example.mygymy.model.RoutineModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Routine extends AppCompatActivity {
    // List of Exercise objects to hold the exercises for the routines
    private List<Exercise> exercises;
    private RecyclerView RoutineList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);


        @SuppressLint("WrongViewCast") MaterialButton createRoutineBtn =
                findViewById(R.id.add_routine_button);
        @SuppressLint({"WrongViewCast", "MissingInflatedId", "LocalSuppress"}) Toolbar toolbar =
                findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        ImageButton userprofile = findViewById(R.id.button1);
        ImageButton newsfeed= findViewById(R.id.button2);
        ImageButton shield = findViewById(R.id.button3);
        ImageButton shareButton = findViewById(R.id.share_button);
        Button recommendation = findViewById(R.id.suggestions_button);

        // Setting up RecyclerView
        RoutineList = findViewById(R.id.rv_routine_list);
        RoutineList.setLayoutManager(new LinearLayoutManager(this));
        // Creating a new RoutineAdapter with an empty list
        RoutineAdapter routineAdapter = new RoutineAdapter(this, new ArrayList<>());
        RoutineList.setAdapter(routineAdapter);
        // Loading routines from Firebase Firestore
        loadRoutinesFromFirestore();


        // opens the Exercise page
        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Routine.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });

        //opens the createRoutine pahe
        createRoutineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Routine.this, createRoutine.class);
                startActivity(intent);
                onResume();
            }
        });
        //share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app: https://mygymmy.com");
                startActivity(Intent.createChooser(shareIntent, "Share app via"));

                ShareActionProvider shareActionProvider = new ShareActionProvider(Routine.this);
                shareActionProvider.setShareIntent(shareIntent);

            }
        });
        //opens the user profile page
        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Routine.this, UserProfile.class);
                startActivity(intent);
            }
        });
        // opens the posting page

        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Routine.this, PostsWorkouts.class);
                startActivity(intent);

            }
        });

    }
    // This method is called when the activity will start interacting with the user
    public void onResume(){
        super.onResume();
        loadRoutinesFromFirestore();
    }

    // This method retrieves routines from Firebase Firestore
    public void loadRoutinesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Routines")
                .whereEqualTo("userEmail",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<RoutineModel> fetchedRoutines = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RoutineModel routine = document.toObject(RoutineModel.class);
                                routine.setId(document.getId()); // Set the document ID as the routine ID
                                fetchedRoutines.add(routine);
                                Log.d("com/example/mygymy/Routine", "Routine loaded: " + routine.getName());
                            }
                            // Set the adapter to the RecyclerView and pass the data to the adapter
                            RoutineList = findViewById(R.id.rv_routine_list);
                            RoutineList.setLayoutManager(new LinearLayoutManager(Routine.this));
                            RoutineAdapter routineAdapter = new RoutineAdapter(Routine.this, fetchedRoutines);
                            RoutineList.setAdapter(routineAdapter);
                        } else {
                            Log.e("com/example/mygymy/Routine", "Error getting routines: ", task.getException());
                        }
                    }
                });


    }


    // This method returns the name of the routine
    public String getName() {
        return getName();
    }

    // This method posts a routine to Firebase Firestore
    public void postRoutine(RoutineModel routineToPost) {
        // Update Firestore document with new information
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = Long.toString(System.currentTimeMillis());
        String documentId = userId + "-" + timestamp;
        DocumentReference routineRef = db.collection("PublicRoutines").document(documentId);
        routineToPost.setPosted(true);
        routineToPost.setId(documentId);
        routineRef.set(routineToPost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Routine.this, "Routine posted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(Routine.this, "Error posting routine", Toast.LENGTH_SHORT).show();
                    }
                });
    }









}