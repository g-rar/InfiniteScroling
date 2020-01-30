package com.example.infinitescroling.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.infinitescroling.ISFirebaseManager;
import com.google.firebase.messaging.FirebaseMessagingService;

public class InfScrollMessagingService extends FirebaseMessagingService {

    private String TAG = "InfScrollMessagingService";
    private ISFirebaseManager appManager = ISFirebaseManager.getInstance();

    public InfScrollMessagingService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "refreshingToken");
        appManager.updateDeviceTokenInDB(s);
    }

    //    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage){
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
}
