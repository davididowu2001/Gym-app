package com.example.mygymy.Userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.mygymy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class charts extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference workoutsRef = db.collection("UserProfile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        BarChart barChart = findViewById(R.id.chart);
        // Retrieve workout data from Firestore
        workoutsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<BarEntry> workouts = new ArrayList<>();
                ArrayList<String>labels = new ArrayList<>();
                int i = 0;
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Map<String,Object>  workoutData = document.getData();
                    Long NoOfWorkouts = (Long) workoutData.get("workouts");
                    Timestamp timestamp =document.getTimestamp("dates");
                    // Retrieve workout data from Firestore
                    if (NoOfWorkouts!=null && timestamp != null) {
                        float x = timestamp.toDate().getTime();
                        labels.add(new SimpleDateFormat("MMM").format(timestamp.toDate()));
                        int y = Integer.parseInt(String.valueOf(NoOfWorkouts));
                        workouts.add(new BarEntry(i, y));
                        i++;
                    }
                }
                // Set up chart data and properties
                BarDataSet barDataSet = new BarDataSet(workouts,"Workouts");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Workouts per Month");
                barChart.animateY(2000);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setLabelCount(labels.size());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle error
            }
        });
    }
}