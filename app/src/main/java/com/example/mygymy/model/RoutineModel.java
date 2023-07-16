package com.example.mygymy.model;

import java.util.List;

public class RoutineModel {
    private String id;
    private String name;
    private List<Exercise> exercises;
    private boolean posted;



    public String getUserEmail() {
        return userEmail;
    }


    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private String userEmail;

    public RoutineModel() {
        // Required empty constructor
    }

    public RoutineModel(String id, String name, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setPosted (boolean isPosted){
        this.posted = isPosted;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
