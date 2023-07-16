package com.example.mygymy.Routine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.model.RoutineModel;

import java.util.List;



public class PostWorkoutAdapter extends RecyclerView.Adapter<PostWorkoutAdapter.ViewHolder> {
    private List<RoutineModel> postedWorkouts;
    // A list that holds the data for the posted workouts
    private LayoutInflater inflater;
    private Context context;

    // Constructor for the adapter. Takes a context and the list of posted workouts as input.
    public PostWorkoutAdapter(Context context, List<RoutineModel> postedWorkouts) {
        this.context = context;
        this.postedWorkouts = postedWorkouts;
        this.inflater = LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.routine_post_item, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the data to the ViewHolder for each item in the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoutineModel postedWorkout = postedWorkouts.get(position);
        holder.userNameTextView.setText(postedWorkout.getUserEmail()); // Replace with the actual user name
        holder.workoutRoutineTextView.setText(postedWorkout.getName());

    }
    // This method returns the number of items in the RecyclerView.
    @Override
    public int getItemCount() {
        return postedWorkouts.size();
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView workoutRoutineTextView;
        ImageButton likeButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name); //username textview
            workoutRoutineTextView = itemView.findViewById(R.id.workout_routine); //workoutine routine textview
            likeButton = itemView.findViewById(R.id.like_button); //likebutton textview
            likeButton.setOnClickListener(new View.OnClickListener() {
                // When the like button is clicked, its selected state is toggled
                @Override
                public void onClick(View view) {
                    view.setSelected(!view.isSelected());
                }
            });
        }
    }
}
