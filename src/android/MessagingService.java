package com.andretissot.firebaseextendednotification;

import com.google.firebase.messaging.RemoteMessage;
import com.gae.scaffolder.plugin.*;
import org.json.*;
import java.util.*;
import android.util.Log;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class MessagingService extends MyFirebaseMessagingService {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null
            || remoteMessage.getData() == null
            || remoteMessage.getData().get("notificationOptions") == null){
            return; //does nothing
        }
        Map<String, String> originalData = remoteMessage.getData();
        JSONObject options;
        try {
            options = new JSONObject(originalData.get("notificationOptions"));
        } catch (JSONException e) {
            return; //invalid json, all will be as default
        }
        new Manager(this).showNotification(new JSONObject(originalData), options);
    }
}
