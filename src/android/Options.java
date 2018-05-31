package com.andretissot.firebaseextendednotification;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.json.*;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class Options {
    public Options(JSONObject options, Context context){
        this.context = context;
        this.id = getInt(options, "id", 0);
        this.title = getString(options, "title");
        if(this.title == null){
            Resources resource = context.getResources();
            this.title = resource.getText(resource.getIdentifier("app_name",
                "string", context.getPackageName())).toString();
        }
        this.ticker = getString(options, "ticker");
        this.autoCancel = getBoolean(options, "autoCancel", true);
        this.openApp = getBoolean(options, "openApp", false);
        this.headsUp = getBoolean(options, "headsUp", false);
        this.summary = getString(options, "summary");
        this.text = getString(options, "text");
        this.bigText = getString(options, "bigText");
        Integer color = getARGB(options, "color");
        if(this.doesColor = (color != null))
            this.color = color;
        this.textLines = getStringArray(options, "textLines");
        this.vibratePattern = getLongArray(options, "vibrate", null, true);
        this.doesVibrate = this.vibratePattern!=null || getBoolean(options, "vibrate", true);
        this.soundUri = getUriOption(options, "sound");
        this.doesSound = this.soundUri!=null || getBoolean(options, "sound", true);
        this.setSmallIconResourceId(options);
        this.setLargeIconBitmap(options);
        this.bigPictureBitmap = getBitmapOption(options, "bigPicture");
        this.channelId = getString(options, "channelId", "notification");
        this.channelName = getString(options, "channelName", "Notification");
        this.channelDescription = getString(options, "channelDescription", "");
    }

    protected int id;
    protected String title;
    protected String ticker;
    protected String summary;
    protected boolean autoCancel;
    protected boolean openApp;
    protected boolean headsUp;
    protected String[] textLines;
    protected String text;
    protected String bigText;
    protected int smallIconResourceId;
    protected boolean doesVibrate;
    protected long[] vibratePattern;
    protected Uri soundUri;
    protected boolean doesSound;
    protected int color;
    protected boolean doesColor;
    protected Bitmap largeIconBitmap;
    protected Bitmap bigPictureBitmap;
    protected Context context;
    protected String channelName;
    protected String channelId;
    protected String channelDescription;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public boolean doesAutoCancel() {
        return autoCancel;
    }

    public boolean doesOpenApp() {
        return openApp;
    }

    public boolean doesHeadsUp() {
        return headsUp;
    }

    public String[] getTextLines() {
        return textLines;
    }

    public String getText() {
        return text;
    }

    public String getBigText() {
        return bigText;
    }

    public String getTicker() {
        return ticker;
    }

    public int getSmallIconResourceId() {
        return smallIconResourceId;
    }

    public boolean doesVibrate() {
        return doesVibrate;
    }

    public long[] getVibratePattern() {
        return vibratePattern;
    }

    public boolean doesSound() {
        return doesSound;
    }

    public Uri getSoundUri() {
        return soundUri;
    }

    public boolean doesColor() {
        return doesColor;
    }

    public int getColor() {
        return color;
    }

    public Bitmap getLargeIconBitmap() {
        return largeIconBitmap;
    }

    public Bitmap getBigPictureBitmap() {
        return bigPictureBitmap;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    protected void setSmallIconResourceId(JSONObject options){
        String icon = null;
        if(!options.isNull("smallIcon")){
            try {
                icon = options.getString("smallIcon");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(icon != null)
            this.smallIconResourceId = getResourceIdForDrawable(icon);
        if(this.smallIconResourceId == 0)
            this.smallIconResourceId = getResourceIdForDrawable("icon");
        if(this.smallIconResourceId == 0)
            this.smallIconResourceId = getResourceIdForDrawable("ic_launcher");
        if(this.smallIconResourceId == 0)
            this.smallIconResourceId = android.R.drawable.ic_popup_reminder;
    }

    protected void setLargeIconBitmap(JSONObject options){
        this.largeIconBitmap = this.getBitmapOption(options, "largeIcon");
        if(this.largeIconBitmap == null){
            int iconId = getResourceIdForDrawable(context.getPackageName(), "icon");
            if (iconId == 0)
                iconId = getResourceIdForDrawable("android", "icon");
            if (iconId == 0)
                iconId = android.R.drawable.screen_background_dark_transparent;
            //uses app default
            this.largeIconBitmap=BitmapFactory.decodeResource(this.context.getResources(), iconId);
        }
    }

    protected Uri getUriOption(JSONObject options, String optionName){
        String uriSource = null;
        if(!options.isNull(optionName)){
            try {
                uriSource = options.getString(optionName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(uriSource == null)
            return null;
        if (uriSource.startsWith("res:"))
            return getUriForResourcePath(uriSource);
        if (uriSource.startsWith("file:///"))
            return getUriFromPath(uriSource);
        if (uriSource.startsWith("file://"))
            return getUriFromAsset(uriSource);
        if (uriSource.startsWith("http"))
            return getUriFromRemote(uriSource);
        return null;
    }

    protected Bitmap getBitmapOption(JSONObject options, String optionName){
        Uri iconUri = getUriOption(options, optionName);
        if(iconUri == null)
            return null;
        try {
            InputStream input = this.context.getContentResolver().openInputStream(iconUri);
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Integer getInt(JSONObject options, String attributeName, Integer defaultValue){
        try {
            return options.getInt(attributeName);
        } catch (JSONException e){
            return defaultValue;
        }
    }

    protected boolean getBoolean(JSONObject options, String attributeName, boolean defaultValue){
        if(options.has(attributeName)){
            try {
                return options.getBoolean(attributeName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    protected String getString(JSONObject options, String attributeName){
        return getString(options, attributeName, null);
    }

    protected String getString(JSONObject options, String attributeName, String defaultValue){
        if(options.isNull(attributeName))
            return defaultValue;
        try {
            String string = options.getString(attributeName);
            if(string.isEmpty())
                return defaultValue;
            return string;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return defaultValue;
    }

    protected Integer getARGB(JSONObject options, String attributeName){
        Integer colorInt = getInt(options, attributeName, null);
        if(colorInt != null)
            return colorInt;
        String colorString = getString(options, attributeName);
        if(colorString == null)
            return null;
        if(colorString.charAt(0) == '#')
            colorString = colorString.substring(1);
        if(colorString.matches("^[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$")){
            try {
                return Integer.parseInt(colorString, 16);
            } catch(NumberFormatException e) {}
        }
        return null; // other "formats" not yet supported
    }

    protected String[] getStringArray(JSONObject options, String attributeName) {
        if(options.isNull(attributeName))
            return null;
        JSONArray lines;
        try {
            lines = options.getJSONArray(attributeName);
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        String[] stringArray = new String[lines.length()];
        for (int i=0;i<lines.length();i++){
            if(lines.isNull(i)){
                stringArray[i] = null;
                continue;
            }
            try {
                stringArray[i] = lines.getString(i);
            } catch (JSONException e) {
                stringArray[i] = null;
            }
        }
        return stringArray;
    }

    protected long[] getLongArray(JSONObject options, String attributeName, long[] defaultValue, boolean supressExceptions){
        if(!options.has(attributeName))
            return defaultValue;
        JSONArray lines;
        try {
            lines = options.getJSONArray(attributeName);
        } catch (JSONException e){
            if(!supressExceptions) {
                e.printStackTrace();
            }
            return defaultValue;
        }
        long[] longArray = new long[lines.length()];
        for (int i=0;i<lines.length();i++){
            try {
                longArray[i] = lines.getLong(i);
            } catch (JSONException e) {
                if(!supressExceptions) {
                    e.printStackTrace();
                }
            }
        }
        return longArray;
    }

    protected int getResourceIdForDrawable(String resourcePath) {
        int resId = getResourceIdForDrawable(this.context.getPackageName(), resourcePath);
        if (resId == 0)
            resId = getResourceIdForDrawable("android", resourcePath);
        return resId;
    }

    protected int getResourceIdForDrawable(String className, String resourcePath) {
        try {
            return this.context.getResources().getIdentifier(getBaseName(resourcePath),
                getResourceTypeName(resourcePath), className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected String getBaseName(String resPath) {
        if(resPath == null) { return null; }
        String drawable = resPath;
        if (drawable.contains("/"))
            drawable = drawable.substring(drawable.lastIndexOf('/') + 1);
        if (resPath.contains("."))
            drawable = drawable.substring(0, drawable.lastIndexOf('.'));
        return drawable;
    }

    protected String getResourceTypeName(String resPath) {
        if(resPath == null) { return null; }
        if(resPath.contains("/"))
            return resPath.substring(0, resPath.lastIndexOf('/'));
        return "drawable";
    }

    private Uri getUriForResourcePath(String path) {
        String resPath = path.replaceFirst("res://", "");
        int resId = getResourceIdForDrawable(resPath);
        File file = getTmpFile();
        if (resId == 0)
            return null;
        try {
            Resources res = this.context.getResources();
            FileOutputStream outStream = new FileOutputStream(file);
            InputStream inputStream = res.openRawResource(resId);
            copyFile(inputStream, outStream);
            return getProvidedFileUri(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUriFromPath(String path) {
        String absPath = path.replaceFirst("file://", "");
        File file = new File(absPath);
        if (file.exists())
            return getProvidedFileUri(file);
        return null;
    }

    private Uri getUriFromAsset(String path) {
        String resPath = path.replaceFirst("file:/", "www");
        String fileName = resPath.substring(resPath.lastIndexOf('/') + 1);
        File file = getTmpFile(fileName);
        if (file == null)
            return null;
        try {
            AssetManager assets = this.context.getAssets();
            FileOutputStream outStream = new FileOutputStream(file);
            InputStream inputStream = assets.open(resPath);
            copyFile(inputStream, outStream);
            return getProvidedFileUri(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getTmpFile(String name) {
        File dir = context.getExternalCacheDir();
        if (dir == null)
            return null;
        String storage  = dir.toString() + "/extendednotification";
        new File(storage).mkdir();
        return new File(storage, name);
    }

    private File getTmpFile () {
        return getTmpFile(UUID.randomUUID().toString());
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
            out.write(buffer, 0, read);
        out.flush();
        out.close();
    }

    private Uri getProvidedFileUri(File ouputFile){
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uriProvided = FileProvider.getUriForFile(this.context,
                this.context.getPackageName()+".fbenfileprovider", ouputFile);
            this.context.grantUriPermission("com.android.systemui", uriProvided, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return uriProvided;
        }
        return Uri.fromFile(ouputFile);
    }

    private Uri getUriFromRemote(String path) {
        File file = getTmpFile();
        if (file == null)
            return null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream input = connection.getInputStream();
            FileOutputStream outStream = new FileOutputStream(file);
            copyFile(input, outStream);
            return getProvidedFileUri(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
