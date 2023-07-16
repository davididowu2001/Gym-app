package com.example.mygymy.Routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.mygymy.R;
import com.example.mygymy.Userprofile.UserProfile;
import com.example.mygymy.model.RoutineModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsWorkouts extends AppCompatActivity {
    // Declaration of RecyclerView to display posted routines
    private RecyclerView PostedRoutinesList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_workouts);
        // Buttons declaration and onClickListener setup for navigation
        ImageButton userprofile = findViewById(R.id.button1);
        ImageButton newsfeed = findViewById(R.id.button2);
        ImageButton shield = findViewById(R.id.button3);
        ImageButton shareButton = findViewById(R.id.share_button);

        // OnClickListeners for navigation buttons
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
        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(intent);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app: https://mygymmy.com");
                startActivity(Intent.createChooser(shareIntent, "Share app via"));

                ShareActionProvider shareActionProvider = new ShareActionProvider(PostsWorkouts.this);
                shareActionProvider.setShareIntent(shareIntent);

            }
        });

        // Setup the RecyclerView
        PostedRoutinesList = findViewById(R.id.postrecycler);
        PostedRoutinesList.setLayoutManager(new LinearLayoutManager(this));
        RoutineAdapter routineAdapter = new RoutineAdapter(this,new ArrayList<>());
        PostWorkoutAdapter postWorkoutAdapter = new PostWorkoutAdapter(this,new ArrayList<>());

        PostedRoutinesList.setAdapter(routineAdapter);
        loadPostedRoutinesFirestore();// Load the routines from Firestore


    }


    // This method is responsible for fetching Routines rom Firestore
    private void loadPostedRoutinesFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query Firestore for public routines that are posted
        db.collection("PublicRoutines")
                .whereEqualTo("posted",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            // List to store fetched routines
                            List<RoutineModel> fetchedRoutines = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                // Convert each document into RoutineModel object
                                RoutineModel routineModel = documentSnapshot.toObject(RoutineModel.class);
                                routineModel.setId(documentSnapshot.getId());
                                // Add routine to list
                                fetchedRoutines.add(routineModel);
                        }
                            // Setting up RecyclerView with fetched routines
                            PostedRoutinesList = findViewById(R.id.postrecycler);
                            PostedRoutinesList.setLayoutManager(new
                                    LinearLayoutManager(PostsWorkouts.this));
                            PostWorkoutAdapter routineAdapter = new PostWorkoutAdapter
                                    (PostsWorkouts.this, fetchedRoutines);
                            PostedRoutinesList.setAdapter(routineAdapter);
                        }

                }
    });
}
}