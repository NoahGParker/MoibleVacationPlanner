package com.example.myapplication.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_MESSAGE = "message";
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    "vacation_channel",
                    "Vacation Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for vacation notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "vacation_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Vacation Alert")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notificationManager.notify(notificationId, notification);
        }
    }
}
