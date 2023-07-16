package com.example.mygymy.Routine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.mygymy.R;
import com.example.mygymy.Userprofile.UserProfile;

public class NotificationService extends Service {
    private static final int NOTIFICATION_ID = 1;// Constant for notification ID
    private NotificationManager mNotificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Call the function to display the notification when the service starts
        showNotification();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used because this is a started service, not a bound service
        return null;
    }

    private void showNotification() {
        // Get the NotificationManager system service
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id",
                    "MyGymy Notifications",
                    // High importance so the user is interrupted and a heads-up notification is shown
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                "channel_id")
                .setSmallIcon(R.drawable.baseline_10k_24)  // Icon for the notification
                .setContentTitle("Congratulations!") // Title of the notification
                .setContentText("You completed a workout!") // Text of the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)// Default priority
                .setAutoCancel(true);// Notification is automatically canceled when the user taps it
        // Create an Intent that will be fired when the user taps the notification
        Intent notificationIntent = new Intent(this, UserProfile.class);
        // Wrap it in a PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        // Set the PendingIntent to be fired when the user taps the notification
        builder.setContentIntent(contentIntent);

        // Use the current time in milliseconds as a unique identifier for the notification
        int notificationId = (int) System.currentTimeMillis();
        // Build the notification and start the service in the foreground
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
        // Notify the user with the built notification
        mNotificationManager.notify(notificationId, notification);
        Log.d("NotificationService", "showNotification called");
        stopForeground(true);
          stopSelf();
    }
}
