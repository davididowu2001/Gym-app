package com.example.mygymy.Routine;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.mygymy.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class Exercisedb extends SQLiteOpenHelper {
    private static final String Exercise_Library = "exercise_library.db";
    private static final int DATABASE_VERSION = 4;

    public Exercisedb(Context context) {
        super(context, Exercise_Library, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + "exercises" + "("
                + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name" + " TEXT NOT NULL,"
                + "description" + " TEXT,"
                + "muscle_group" + " TEXT,"
                + "equipment" + " TEXT,"
                + "image_path" + " TEXT"
                + ")";

        db.execSQL(CREATE_EXERCISES_TABLE);

        // Insert sample data
        insertExercise(db, "Push-ups", "Place your hands on the floor shoulder-width " +
                        "apart, with your legs straight behind you. Lower your body to the floor by bending your arms at the elbows, " +
                        "then push back up to the starting position.", "Chest, Triceps",
                "Bodyweight", "path/to/image");
        insertExercise(db, "Bench Press", "Lie on a flat bench with your feet on the floor. " +
                        "Grip the barbell with your hands slightly wider than shoulder-width apart. Lower the bar to your chest, " +
                        "then push it back up to the starting position.", "Chest, Triceps",
                "Barbell, Bench", "path/to/image");

        insertExercise(db, "Dumbbell Fly", "Lie on a flat bench with a dumbbell in each hand. " +
                        "Extend your arms out to the sides, then lower the weights until they are level with your chest. " +
                        "Raise the weights back up to the starting position.", "Chest",
                "Dumbbells, Bench", "path/to/image");

        insertExercise(db, "Triceps Dip", "Sit on the edge of a bench or chair with your hands " +
                        "grasping the edge. Lower your body until your arms form a 90-degree angle, then push back up to the starting position.", "Triceps",
                "Bodyweight, Bench", "path/to/image");

        insertExercise(db, "Triceps Extension", "Hold a dumbbell in each hand and stand with your feet shoulder-width apart. " +
                        "Raise the weights above your head with your arms straight, then lower the weights behind your head. " +
                        "Raise the weights back up to the starting position.", "Triceps",
                "Dumbbells", "path/to/image");

        insertExercise(db, "Squats", "Stand with your feet shoulder-width apart and your toes pointing forward. " +
                        "Lower your body by bending your knees, keeping your back straight. " +
                        "Push back up to the starting position.", "Legs",
                "Barbell, Bodyweight", "path/to/image");

        insertExercise(db, "Lunges", "Stand with your feet hip-width apart. " +
                        "Take a large step forward with your left foot and lower your body until your left knee is at a 90-degree angle. " +
                        "Push back up to the starting position and repeat with your right leg.", "Legs",
                "Bodyweight, Dumbbells", "path/to/image");

        insertExercise(db, "Calf Raises", "Stand with your feet shoulder-width apart and hold a dumbbell in each hand. " +
                        "Raise your heels off the floor as high as possible, then lower them back down to the starting position.", "Legs",
                "Dumbbells", "path/to/image");
        insertExercise(db, "Dumbbell Curls", "Stand with a dumbbell in each hand, " +
                        "curl them up towards your shoulders, then lower them back down.", "Biceps",
                "Dumbbell", "path/to/image");

        insertExercise(db, "Barbell Curls", "Stand with a barbell, " +
                        "curl it up towards your shoulders, then lower it back down.", "Biceps",
                "Barbell", "path/to/image");

        insertExercise(db, "Pull-ups", "Hang from a bar with your hands shoulder-width " +
                        "apart, pull your body up towards the bar, then lower it back down.", "Back, Biceps",
                "Bodyweight", "path/to/image");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + "exercises");
            onCreate(db);
        }
    }

    //inserts exercises into database based on the fields provided

    public void insertExercise(SQLiteDatabase db, String name, String description, String muscleGroup, String equipment, String imagePath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("muscle_group", muscleGroup);
        contentValues.put("equipment", equipment);
        contentValues.put("image_path", imagePath);

        db.insert("exercises", null, contentValues);
    }

        public List<Exercise> getExercises() {
            List<Exercise> exercises = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {"_id", "name", "description", "muscle_group", "equipment", "image_path"};

            Cursor cursor = db.query("exercises", projection, null, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                    @SuppressLint("Range") String muscleGroup = cursor.getString(cursor.getColumnIndex("muscle_group"));
                    @SuppressLint("Range") String equipment = cursor.getString(cursor.getColumnIndex("equipment"));
                    @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex("image_path"));

                    Exercise exercise = new Exercise();
                    exercise.setName(name);
                    exercise.setDescription(description);
                    exercise.setMuscleGroup(muscleGroup);
                    exercise.setEquipment(equipment);
                    // Add a new field in the Exercise class for the image path and set it here.
                     exercise.setImagePath(imagePath);

                    exercises.add(exercise);
                }
                cursor.close();
            }
            db.close();
            Log.d("Exercisedb", "Fetched exercises count: " + exercises.size());
            return exercises;
        }

    }

