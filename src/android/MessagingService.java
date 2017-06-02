package com.andretissot.firebaseextendednotification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.content.pm.PackageManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import java.util.Map;
import java.util.Random;
import com.gae.scaffolder.plugin.*;

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
        Options options = new Options(remoteMessage.getData().get("notificationOptions"),
            this.getApplicationContext());
        Builder builder = new Builder(this).setDefaults(0)
            .setContentTitle(options.getTitle()).setSmallIcon(options.getSmallIconResourceId())
            .setLargeIcon(options.getLargeIconBitmap()).setAutoCancel(options.doesAutoCancel());
        if(options.getBigPictureBitmap() != null)
            builder.setStyle(new BigPictureStyle().bigPicture(options.getBigPictureBitmap()));
        if(options.doesVibrate() && options.getVibratePattern() != null)
            builder.setVibrate(options.getVibratePattern());
        if(options.doesSound() && options.getSoundUri() != null)
            builder.setSound(options.getSoundUri(), android.media.AudioManager.STREAM_NOTIFICATION);
        if (options.doesColor() && Build.VERSION.SDK_INT >= 22)
            builder.setColor(options.getColor());
        Map<String, String> data = remoteMessage.getData();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setContentTextAndMultiline(builder, options);
        setOnClick(builder, data);
        Notification notification;
        if (Build.VERSION.SDK_INT <= 15) {
            notification = builder.getNotification(); // Notification for HoneyComb to ICS
        } else {
            notification = builder.build(); // Notification for Jellybean and above
        }
        if(options.doesAutoCancel())
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if(options.doesVibrate() && options.getVibratePattern() == null)
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        if(options.doesSound() && options.getSoundUri() == null)
            notification.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(options.getId(), notification);
        if(options.doesOpenApp())
            openApp();
    }

    private void setContentTextAndMultiline(Builder builder, Options options) {
        String text = options.getText();
        if(text != null && !text.isEmpty()){
            String ticker = options.getTicker();
            if(ticker == null) { ticker = text; }
            builder.setContentText(text).setTicker(ticker);
            return;
        }
        String[] textLines = options.getTextLines();
        if(textLines == null || textLines.length == 0) {
            return;
        }
        if(textLines.length == 1) {
            builder.setContentText(textLines[0]).setTicker(textLines[0]);
            return;
        }
        InboxStyle inboxStyle = new InboxStyle(builder);
        for(String line : textLines)
            inboxStyle.addLine(line);
        String summary = options.getSummary();
        inboxStyle.setSummaryText(summary);
        String ticker = options.getTicker();
        if(ticker == null) { ticker = summary; }
        builder.setContentText(summary).setTicker(ticker);
    }

    private void setOnClick(Builder builder, Map<String, String> data) {
        Intent intent = new Intent(this, NotificationActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        for (Map.Entry<String, String> entry : data.entrySet())
            intent.putExtra(entry.getKey(), entry.getValue());
        int reqCode = new Random().nextInt();
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
    }

    private void openApp(){
        PackageManager manager = getPackageManager();
        Context context = getApplicationContext();
        Intent launchIntent = manager.getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(launchIntent);
    }
}
