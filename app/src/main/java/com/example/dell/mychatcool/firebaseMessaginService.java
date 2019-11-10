package com.example.dell.mychatcool;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by DELL on 4/1/2018.
 */



public class firebaseMessaginService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle=remoteMessage.getNotification().getTitle();
        String notificationBody=remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();
        String ID_from_theSender=remoteMessage.getData().get("from_sender_id");



        NotificationCompat.Builder myBuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.message)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody);
        Intent resultIntent=new Intent(click_action);
        resultIntent.putExtra("selected_user_id",ID_from_theSender);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        myBuilder.setContentIntent(resultPendingIntent);


        int NotificationId=(int) System.currentTimeMillis();
        NotificationManager myNotManger=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myNotManger.notify(NotificationId,myBuilder.build());

    }
}
