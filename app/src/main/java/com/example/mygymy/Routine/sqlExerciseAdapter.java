package com.example.mygymy.Routine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.model.Exercise;

import java.util.List;


// Adapter class for RecyclerView to display exercises
public class sqlExerciseAdapter extends RecyclerView.Adapter<sqlExerciseAdapter.ExerciseViewHolder> {
    private Context context;
    private List<Exercise> exercises;

    // Constructor for sqlExerciseAdapter
    public sqlExerciseAdapter(Context context, List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    // This method creates a new ViewHolder that can represent  exercises.
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.localexercise, parent, false);
        return new ExerciseViewHolder(itemView);
    }

    // This method takes the ViewHolder and sets the proper list data on it.
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.nameTextView.setText(exercise.getName());
        holder.descriptionTextView.setText(exercise.getDescription());
        holder.muscleGroupTextView.setText(exercise.getMuscleGroup());
        holder.equipmentTextView.setText(exercise.getEquipment());

    }

    @Override
    // This method returns the size of the collection
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView; // TextView for the exercise name
        TextView descriptionTextView; // TextView for the exercise description
        TextView muscleGroupTextView; // TextView for the exercise muscle group
        TextView equipmentTextView; // TextView for the exercise equipment
        ImageView imageView; // ImageView for the exercise image

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_textview);
            descriptionTextView = itemView.findViewById(R.id.description_text);
            muscleGroupTextView = itemView.findViewById(R.id.muscle_group_textview);
            equipmentTextView = itemView.findViewById(R.id.equipment_textview);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
