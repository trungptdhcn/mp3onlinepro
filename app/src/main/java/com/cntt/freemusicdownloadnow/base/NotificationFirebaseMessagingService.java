package com.cntt.freemusicdownloadnow.base;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by TRUNGPT on 8/23/16.
 */
public class NotificationFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        if (remoteMessage.getData().size() > 0)
        {
            Log.d("trung dai ca", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null)
        {
            Log.d("trung dai ca", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

}