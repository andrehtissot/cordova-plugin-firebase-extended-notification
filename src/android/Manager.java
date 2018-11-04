package com.andretissot.firebaseextendednotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.InboxStyle;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import com.gae.scaffolder.plugin.*;
import java.util.*;
import org.json.*;

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
            this.context.getSystemService(Context.NOTIFICATION_SERVICE);
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
            this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public void showNotification(JSONObject dataToReturnOnClick, JSONObject notificationOptions){
        Options options = new Options(notificationOptions, this.context.getApplicationContext());
        this.createNotificationChannel(options);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this.context, options.getChannelId());
        } else {
            builder = new Notification.Builder(this.context);
        }
        builder.setDefaults(0)
            .setContentTitle(options.getTitle()).setSmallIcon(options.getSmallIconResourceId())
            .setLargeIcon(options.getLargeIconBitmap()).setAutoCancel(options.doesAutoCancel());
        if(options.getBigPictureBitmap() != null)
            builder.setStyle(new BigPictureStyle().bigPicture(options.getBigPictureBitmap()));
        if(options.doesVibrate() && options.getVibratePattern() != null)
            builder.setVibrate(options.getVibratePattern());
        else if (Build.VERSION.SDK_INT >= 21 && options.doesHeadsUp())
            builder.setVibrate(new long[0]);
        if(options.doesHeadsUp()) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        if(options.doesSound() && options.getSoundUri() != null)
            builder.setSound(options.getSoundUri(), android.media.AudioManager.STREAM_NOTIFICATION);
        if (options.doesColor() && Build.VERSION.SDK_INT >= 22)
            builder.setColor(options.getColor());
        this.setContentTextAndMultiline(builder, options);
        this.setOnClick(builder, dataToReturnOnClick);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16) {
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
        NotificationManager notificationManager
            = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(options.getId(), notification);
        if(options.doesOpenApp())
            openApp();
    }

    private void setContentTextAndMultiline(Builder builder, Options options) {
        String bigText = options.getBigText();
        String text = options.getText();
        if(bigText != null && !bigText.isEmpty()){
            builder.setStyle(new BigTextStyle().bigText(bigText).setSummaryText(options.getSummary()));
            builder.setContentText(text).setTicker(options.getTicker());
            return;
        }
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
            String ticker = options.getTicker();
            if(ticker == null) { ticker = textLines[0]; }
            builder.setContentText(textLines[0]).setTicker(ticker);
            return;
        }
        InboxStyle inboxStyle = new InboxStyle();
        for(String line : textLines)
            inboxStyle.addLine(line);
        String summary = options.getSummary();
        builder.setStyle(inboxStyle.setSummaryText(summary));
        String ticker = options.getTicker();
        if(ticker == null) { ticker = summary; }
        builder.setContentText(summary).setTicker(ticker);
    }

    private void setOnClick(Builder builder, JSONObject dataToReturnOnClick) {
        Intent intent = new Intent(this.context, NotificationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Iterator<?> keys = dataToReturnOnClick.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value;
            try {
                value = dataToReturnOnClick.get(key);
            } catch(JSONException e){
                value = null;
            }
            intent.putExtra(key, value == null ? null : value.toString());
        }
        int reqCode = new Random().nextInt();
        PendingIntent contentIntent = PendingIntent.getActivity(
            this.context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
    }

    private void openApp(){
        PackageManager manager = this.context.getPackageManager();
        Context context = this.context.getApplicationContext();
        Intent launchIntent = manager.getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(launchIntent);
    }

    private void createNotificationChannel(Options options) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            if(options.doesHeadsUp()) {
                importance = NotificationManager.IMPORTANCE_HIGH;
            } else if(!options.doesSound()) {
                importance = NotificationManager.IMPORTANCE_LOW;
            }
            NotificationChannel channel = new NotificationChannel(options.getChannelId(), options.getChannelName(), importance);
            if(options.doesHeadsUp()) {
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            }
            channel.setDescription(options.getChannelDescription());
            if(options.doesSound() && options.getSoundUri() != null) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setLegacyStreamType(android.media.AudioManager.STREAM_NOTIFICATION)
                        .build();
                channel.setSound(options.getSoundUri(), audioAttributes);
            }
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
