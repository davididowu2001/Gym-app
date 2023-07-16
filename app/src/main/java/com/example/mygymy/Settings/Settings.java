package com.example.mygymy.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import com.example.mygymy.R;
import com.example.mygymy.Signin.Login;
import com.example.mygymy.databinding.ActivityMainBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Settings activity where users can modify various settings related to the application
public class Settings extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    Button userguide;
    FirebaseUser user;
    private ActivityMainBinding binding;
    private Calendar calendar;
    private AlarmManager alarm;
    private PendingIntent pendingIntent;
    SwitchMaterial notificationSwitch;
    private static final int PERMISSIONS_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Check if user has enabled notifications, if not, show a dialog to enable them
        createNotificationChannel();
        if (!checkNotificationPermission()) {
            showEnableNotificationDialog();
        }
        // Initialize Firebase Auth and buttons
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout); //log out button
        userguide = findViewById(R.id.user_guide_button);//user guide button
        user = auth.getCurrentUser();


        // If no user is currently logged in, start Login activity
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        // Logout button's onclick listener
        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
        // User guide button's onclick listener
        userguide.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UserGuide.class);
            startActivity(intent);
        });
        // Find gym button's onclick listener
        Button findGymButton = findViewById(R.id.find_gym_button);
        findGymButton.setOnClickListener(view -> openMapForNearbyGyms());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {    // Check and request permissions
            checkAndRequestPermissions();
        }
        // Time picker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timeSetter.setOnClickListener(view -> setAlarm());
        }
        // Open time picker on click
        binding.timePicker.setOnClickListener(view -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Alarm Time")
                    .build();
            picker.show(getSupportFragmentManager(), "idk");

            picker.addOnPositiveButtonClickListener(view1 -> {
                if (picker.getHour() > 12) {
                    binding.timeSetter.setText(
                            String.format("%02d", (picker.getHour() - 12)) + " : " + String.format
                                    ("%02d", picker.getMinute()) + " PM");
                } else {
                    binding.timeSetter.setText(picker.getHour() + ":" + picker.getMinute() + " AM");
                }
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                // Save alarm time in shared preferences
                saveAlarmTime(picker.getHour(), picker.getMinute());
            });
        });

        // Notification switch's check change listener
        notificationSwitch = findViewById(R.id.notification_switch);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAlarm();
                }
            } else {
                cancelAlarm();
            }
            // Save switch state in shared preferences
            savePreferences("notification_switch", isChecked);
        });
        loadPref(); // Load saved preferences


    }

    // Save the alarm time in shared preferences
    private void saveAlarmTime(int hour, int minute) {
        SharedPreferences sharedPreferences = getSharedPreferences("myGymyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("alarm_hour", hour);
        editor.putInt("alarm_minute", minute);
        editor.apply();
    }

    // Open map and search for nearby gyms
    private void openMapForNearbyGyms() {
        Uri geoLocation = Uri.parse("geo:0,0?q=gym near me");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoLocation);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    // Save boolean value in shared preferences
    public void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences("myGymmyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Load saved preferences and update UI accordingly
    private void loadPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("myGymmyPref", MODE_PRIVATE);
        boolean notificationSwitchOn = sharedPreferences.getBoolean("notification_switch", false);
        notificationSwitch.setChecked(notificationSwitchOn);

        int hour = sharedPreferences.getInt("alarm_hour", 0);
        int minute = sharedPreferences.getInt("alarm_minute", 0);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (hour > 12) {
            binding.timeSetter.setText(
                    String.format("%02d", (hour - 12)) + " : " + String.format("%02d", minute) + " PM");
        } else {
            binding.timeSetter.setText(hour + ":" + minute + " AM");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ServiceCast")
    private void setAlarm() {   // Set the alarm
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set it calling the alarm class
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    // Cancel the alarm
    private void cancelAlarm() {
        if (alarm != null && pendingIntent != null) {
            alarm.cancel(pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "idk";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("idk", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    // Check and request necessary permissions
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Check if ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Check if SCHEDULE_EXACT_ALARM permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
        }

        // Request permissions if not granted
        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionsArray, PERMISSIONS_REQUEST_CODE);
        }
    }

    // Check if notifications are enabled
    private boolean checkNotificationPermission() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        return notificationManager.areNotificationsEnabled();
    }

    //show dialog to ask user to enable notifications
    private void showEnableNotificationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Please enable notifications for this app to receive important alerts.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissions not granted. Some features may not work properly.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}


