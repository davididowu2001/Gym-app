package com.example.mygymy.Userprofile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class GoogleFitHelper {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1001;
    private final GoogleSignInClient mGoogleSignInClient;
    private final Activity mActivity;
    static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private StepCountListener stepCountListener;

    public GoogleFitHelper(Activity activity) {
        mActivity = activity;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void connect() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(mActivity, fitnessOptions);
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    mActivity,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Google Fit permissions granted.");
                accessGoogleFit();

            }
            else {
                Log.e(TAG, "Google Fit permissions not granted. Result code: " + resultCode);
            }
        }
    }

    void accessGoogleFit() {
        // Access and use Google Fit data here.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account == null) {
            OnDataPointListener listener = new OnDataPointListener() {
                @Override
                public void onDataPoint(@NonNull DataPoint dataPoint) {
                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value val = dataPoint.getValue(field);
                        if (field.getName().equals("steps")) {
                            int steps = val.asInt();

                            if (stepCountListener != null) {
                                stepCountListener.onStepCountUpdated(steps);
                            }
                        }
                    }
                }

            };
            // Handle the case when the user is not signed in
            return;
        }

        Fitness.getHistoryClient(mActivity, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    int totalSteps = dataSet.isEmpty() ? 0 : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    // Display the step count in app's user interface

//                    ((UserProfile) mActivity).onStepCountUpdated(totalSteps);
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "There was a problem getting steps.", e);
                });
        // Subscribe to step count updates
        OnDataPointListener listener = dataPoint -> {
            for (Field field : dataPoint.getDataType().getFields()) {
                Value val = dataPoint.getValue(field);
                if (field.getName().equals("steps")) {
                    int steps = val.asInt();
                    Log.d(TAG, "Steps: " + steps);
                    if (stepCountListener != null) {
                        stepCountListener.onStepCountUpdated(steps);
                    }
                }
            }
        };
        Fitness.getSensorsClient(mActivity, account)
                .add(
                        new SensorRequest.Builder()
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                .setSamplingRate(1, TimeUnit.SECONDS)
                                .build(),
                        listener)
                .addOnSuccessListener(aVoid -> {
                    // Successfully subscribed to step count updates
                })
                .addOnFailureListener(e -> {
                    Log.e("GoogleFitHelper", "Error subscribing to step count updates", e);
                });

    }
    public void disconnect() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Handle disconnection
                    }
                });
    }
    public void requestFitnessPermissions() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(mActivity), fitnessOptions)) {
//            System.out.println("Pls work");
            GoogleSignIn.requestPermissions(
                    mActivity,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(mActivity),
                    fitnessOptions);
            requestFitnessPermissions();

        } else {

            accessGoogleFit();
        }
    }
    public boolean isConnected() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        return account != null;

    }
    public boolean shouldRequestFitnessPermissions() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        return !GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(mActivity), fitnessOptions);
    }
    public interface StepCountListener {
        void onStepCountUpdated(int totalSteps);
    }
    public void setStepCountListener(StepCountListener listener) {
        this.stepCountListener = listener;
    }

}
