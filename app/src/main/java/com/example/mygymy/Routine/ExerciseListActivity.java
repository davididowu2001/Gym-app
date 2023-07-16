package com.example.mygymy.Routine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.Userprofile.UserProfile;
import com.example.mygymy.model.Exercise;

import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private sqlExerciseAdapter exerciseAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recomrecycler);

        //creating buttons

        recyclerView = findViewById(R.id.suggrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageButton userprofile = findViewById(R.id.button1);
        ImageButton newsfeed= findViewById(R.id.button2);
        ImageButton shield = findViewById(R.id.button3);
        ImageButton shareButton = findViewById(R.id.share_button);

        //this fixes each exercise in a recycler view
        Exercisedb exercisedb = new Exercisedb(this); //local db
        List<Exercise> exercises = exercisedb.getExercises();
        exerciseAdapter = new sqlExerciseAdapter(this, exercises);
        recyclerView.setAdapter(exerciseAdapter);



        //share content provider, so details of the exercises
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri exercisesUri = Uri.parse("content://com.example.mygymy.exercisecontentprovider/exercises");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, exercisesUri.toString());
                startActivity(Intent.createChooser(shareIntent, "Share exercise content provider URI"));

            }
        });


        //open user profile
        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });
        //open post page

        newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this, PostsWorkouts.class);
                startActivity(intent);

            }
        });

        //open ROutine page
        shield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this, Routine.class);
                startActivity(intent);

            }
        });


    }
}
