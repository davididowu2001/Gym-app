package com.example.mygymy.model;

import java.io.Serializable;

public class Exercise implements Serializable {
     String Name;
     String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    String equipment;
     String muscleGroup;
     String Type;
     private boolean selected;



    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String imagePath;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getType() {
        return Type;
    }



    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Empty constructor for Firestore
    public Exercise() {}

    public Exercise(String Name, String muscleGroup, String Type, String description,String equipment) {
        this.Name = Name;
        this.muscleGroup = muscleGroup;
        this.Type = Type;
        this.description = description;
        this.equipment = equipment;
    }


}

