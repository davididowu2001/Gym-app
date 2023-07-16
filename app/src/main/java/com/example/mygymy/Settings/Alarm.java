package com.example.mygymy.Settings;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.os.Build;

import com.example.mygymy.R;
import com.example.mygymy.Signin.Login;


public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the alarm goes off
        // Create an intent to launch the Login activity when the notification is clicked
        Intent intent1 = new Intent(context, Login.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
        // Create a notification channel for the notification
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("GymTime", "Gym Tine", NotificationManager.IMPORTANCE_DEFAULT);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "idk")
                .setSmallIcon(R.drawable.baseline_newspaper_24)
                .setContentTitle("Gym Time")
                .setContentText("Time to Gymmy!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.createNotificationChannel(channel);
        notificationManagerCompat.notify(123, builder.build());
    }

}
