package com.example.mygymy.model;

import java.util.Date;

public class UserProfileData {
    private String email;
    private String name;
    private int workouts;
    private Date dates;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    private int steps;



    public UserProfileData(String email, String name, int workouts, Date dates, int steps) {
        this.email = email;
        this.name = name;
        this.workouts = workouts;
        this.dates = dates;
        this.steps = steps;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkouts() {
        return workouts;
    }


    public UserProfileData() {
    }

}
