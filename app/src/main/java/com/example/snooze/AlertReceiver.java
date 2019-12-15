package com.example.snooze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;



public class AlertReceiver  extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("Title");
        String text = intent.getStringExtra("Text");


        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification(title,text);
        notificationHelper.getmManager().notify(1,nb.build());


    }
}
