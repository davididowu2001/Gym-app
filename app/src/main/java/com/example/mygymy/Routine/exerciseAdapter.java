package com.example.mygymy.Routine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class exerciseAdapter extends RecyclerView.Adapter<exerciseAdapter.MyViewHolder> {

    Context context;
    ArrayList<Exercise> exercisesArrayList;

    public exerciseAdapter(Context context, ArrayList<Exercise> exercisesArrayList) {
        this.context = context;
        // Initialize the list of exercises
        // If the exercisesArrayList passed in is null, create a new empty ArrayList
        this.exercisesArrayList = exercisesArrayList != null ? exercisesArrayList : new ArrayList<>();
        this.exercisesArrayList = exercisesArrayList != null ? exercisesArrayList : new ArrayList<>();
    }

    @NonNull
    @Override
    public exerciseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the exercise item view layout
        View v = LayoutInflater.from(context).inflate(R.layout.item_exercise,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull exerciseAdapter.MyViewHolder holder, int position) {
        // Bind the data to the view
        Exercise exercise = exercisesArrayList.get(position);
        holder.Name.setText(exercise.getName());
        holder.Type.setText(exercise.getType());
        holder.muscleGroup.setText(exercise.getMuscleGroup());

    }

    // Return the number of items in the list of exercises
    @Override
    public int getItemCount() {
        return exercisesArrayList.size();
    }


    public List<Exercise> getSelectedExercises() {
        // Return the number of items in the list of exercises
        List<Exercise> selectedExercises = new ArrayList<>();
        for (Exercise exercise : exercisesArrayList) {
            if (exercise.isSelected()) {
                selectedExercises.add(exercise);
            }
        }

        return selectedExercises;

    }

    public void updateExercises(List<Exercise> selectedExercises) {
        // Update the list of exercises with a new list of selected exercises
        exercisesArrayList.clear();
        if (selectedExercises != null) {
            exercisesArrayList.addAll(selectedExercises);
        }
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder class to hold the views for an individual exercise item
        TextView Name, muscleGroup, Type;
        CheckBox checkBoxExercise;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.exercise_name);
            Type = itemView.findViewById(R.id.exercise_type);
            muscleGroup = itemView.findViewById(R.id.muscle_group);
            checkBoxExercise = itemView.findViewById(R.id.checkBoxExercise);
            // Set a listener to handle checkbox clicks
            checkBoxExercise.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Update the isSelected property of the exercise object in the list
                    exerciseAdapter.this.exercisesArrayList.get(position).setSelected(isChecked);
                }
            });
        }


    }

}
