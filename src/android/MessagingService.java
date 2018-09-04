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
            // If as object
            options = new JSONObject(originalData.get("notificationOptions"));
        } catch (JSONException e1) {
            try {
                // If stringified
                String notificationOptions = originalData.get("notificationOptions");
                if(notificationOptions == null || notificationOptions.length() < 2) {
                    // Should not substring on small string
                    return;
                }
                options = new JSONObject(notificationOptions.substring(1, notificationOptions.length() - 1));
            } catch (JSONException e2) {
                return; //invalid json, all will be as default
            }
        }
        new Manager(this).showNotification(new JSONObject(originalData), options);
    }
}
