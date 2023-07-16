package com.example.mygymy.Routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.model.Exercise;
import com.example.mygymy.model.RoutineModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class createRoutine extends AppCompatActivity {
    //Declare UI elements
    private EditText RoutineName;
    private RecyclerView ExerciseList;
    private Button btnCancel, btnSave;
    private com.example.mygymy.Routine.exerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createroutine);
        //initialise UI elements
        RoutineName = findViewById(R.id.etRoutineName);
        ExerciseList = findViewById(R.id.rvExerciseList);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        // Initialize the RecyclerView and adapter
        ExerciseList.setLayoutManager(new LinearLayoutManager(this));
        exerciseAdapter = new exerciseAdapter(this, new ArrayList<>()); // Initialize with an empty list
        ExerciseList.setAdapter(exerciseAdapter);

        // Load exercises from Firestore
        loadExercisesFromFirestore();
        // Close the activity or dialog when the user cancels
        btnCancel.setOnClickListener(view -> {
            finish();
        });
        btnSave.setOnClickListener(view -> {
            // Save the new routine to Firestore
            saveRoutineToFirestore();
        });
    }
    // Load exercises from Firestore
    private void loadExercisesFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("Exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a list of Exercise objects from the Firestore data
                        ArrayList<Exercise> exercisesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Exercise exercise = document.toObject(Exercise.class);
                            exercisesList.add(exercise);
                        }
                        // Update the adapter with the new data
                        exerciseAdapter.exercisesArrayList = exercisesList;
                        exerciseAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error getting exercises: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Save the new routine to Firestore
        private void saveRoutineToFirestore() {
            String routineName = RoutineName.getText().toString().trim();
            //if there is no routine name,then make them enter username
            if (routineName.isEmpty()) {
                Toast.makeText(this, "Please enter a routine name.", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Exercise> selectedExercises = exerciseAdapter.getSelectedExercises();
            //Select at least one exercise
            if (selectedExercises.isEmpty()) {
                Toast.makeText(this, "Please select at least one exercise.", Toast.LENGTH_SHORT).show();
                return;
            }

            //  implement user authentication and have the user's ID
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            // Create a new routine object
            RoutineModel newRoutine = new RoutineModel();
            newRoutine.setName(routineName);
            newRoutine.setExercises(selectedExercises);
            newRoutine.setId(userId);
            newRoutine.setUserEmail(userEmail);
            newRoutine.setPosted(false);

            // Save the new routine to the "routines" collection in Firestore
            FirebaseFirestore.getInstance()
                    .collection("Routines")
                    .add(newRoutine)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Routine saved successfully!", Toast.LENGTH_SHORT).show();

                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }

