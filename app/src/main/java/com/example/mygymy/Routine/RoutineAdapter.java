package com.example.mygymy.Routine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygymy.R;
import com.example.mygymy.model.Exercise;
import com.example.mygymy.model.RoutineModel;

import java.util.ArrayList;
import java.util.List;

// The RoutineAdapter is a custom RecyclerView.Adapter class used to populate
// the RecyclerView with a list of workout routines.
public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {
    // A list that holds the data for the workout routines
    private static List<RoutineModel> routineList;
    // A LayoutInflater to inflate the layout for each item in the RecyclerView
    private LayoutInflater mInflater;
    // A Context to get access to system services (e.g., LayoutInflater)
    private Context context;

    // Constructor for the adapter. Takes a context and the list of workout routines as input.
    public RoutineAdapter(Context context, List<RoutineModel> routines) {
        this.context = context;
        this.routineList = routines;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    // This method inflates the layout for each item in the RecyclerView and
    // creates a ViewHolder object for that item.
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.routine_item, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the data to the ViewHolder for each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RoutineModel routine = routineList.get(position);
        holder.routineNameTextView.setText(routine.getName());
        holder.postRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoutineModel routineToPost = routineList.get(position);
                routineToPost.setPosted(true);
                ((Routine) context).postRoutine(routineToPost);
            }
        });
    }
    public void delete(int position){
        routineList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,routineList.size());
    }

    // This method returns the number of items in the RecyclerView.
    @Override
    public int getItemCount() {
        return routineList.size();
    }


    // The ViewHolder is a wrapper for the View of each item in the RecyclerView.
    // It contains references to the individual views within the item layout to populate them with data.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView routineNameTextView;
        Button startRoutineButton;
        Button postRoutineButton;
        Routine routine;
        // The ViewHolder constructor. It sets up the references to the views in the item layout.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routineNameTextView = itemView.findViewById(R.id.routine_name);
            postRoutineButton = itemView.findViewById(R.id.post_routine_button);

            startRoutineButton = itemView.findViewById(R.id.start_btn);
            startRoutineButton.setOnClickListener(new View.OnClickListener() {
                // OnClickListener for the start button. When clicked,
                // it starts the workout activity with the selected exercises.
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), workout.class);
                    RoutineModel routine = routineList.get(getAdapterPosition());
                    ArrayList<Exercise> exercises = (ArrayList<Exercise>) routine.getExercises();
                    intent.putExtra("selectedExercises", exercises);
                    view.getContext().startActivity(intent);
                }
            });

        }
    }
}

