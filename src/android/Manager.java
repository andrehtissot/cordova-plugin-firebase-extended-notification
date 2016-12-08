package com.andretissot.firebaseextendednotification;

import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.StatusBarNotification;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class Manager {
    protected Context context;

    public Manager(Context context) {
        this.context = context;
    }

    public boolean notificationExists(int notificationId) {
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            StatusBarNotification[] notifications = notificationManager
                .getActiveNotifications();
            for (StatusBarNotification notification : notifications)
                if (notification.getId() == notificationId)
                    return true;
            return false;
        }
        //If lacks support
        return true;
    }

    public void cancelNotification(int notificationId){
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}
