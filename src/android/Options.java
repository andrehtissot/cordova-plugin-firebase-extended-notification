package com.andretissot.firebaseextendednotification;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class Options {
    public Options(String optionsToParse, Context context){
        this.context = context;
        JSONObject options;
        try {
            options = new JSONObject(optionsToParse);
        } catch (JSONException e) {
            return; //invalid json, all will be as default
        }
        this.id = getInt(options, "id");
        this.title = getString(options, "title");
        if(this.title == null){
            Resources resource = context.getResources();
            this.title = resource.getText(resource.getIdentifier("app_name",
                "string", context.getPackageName())).toString();
        }
        this.autoCancel = getBool(options, "autoCancel", true);
        this.summary = getString(options, "summary");
        this.text = getString(options, "text");
        this.textLines = getStringArray(options, "textLines");
        this.setSmallIconResourceId(options);
        this.setLargeIconBitmap(options);
    }

    protected int id;
    protected String title;
    protected String summary;
    protected boolean autoCancel = true;
    protected String[] textLines;
    protected String text;
    protected int smallIconResourceId;
    protected android.graphics.Bitmap largeIconBitmap;
    protected Context context;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public boolean isAutoCancel() {
        return autoCancel;
    }

    public String[] getTextLines() {
        return textLines;
    }

    public String getText() {
        return text;
    }

    public int getSmallIconResourceId() {
        return smallIconResourceId;
    }

    public Bitmap getLargeIconBitmap() {
        return largeIconBitmap;
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
        int iconId = getResourceIdForDrawable(context.getPackageName(), "icon");
        if (iconId == 0)
            iconId = getResourceIdForDrawable("android", "icon");
        if (iconId == 0)
            iconId = android.R.drawable.screen_background_dark_transparent;
        String icon = null;
        if(!options.isNull("largeIcon")){
            try{
                icon = options.getString("largeIcon");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(icon == null){
            this.largeIconBitmap = BitmapFactory.decodeResource(this.context.getResources(), iconId);
            return; //uses app default
        }
        Uri iconUri = null;
        if (icon.startsWith("res:")) {
            iconUri = getUriForResourcePath(icon);
        } else if (icon.startsWith("file:///")) {
            iconUri = getUriFromPath(icon);
        } else if (icon.startsWith("file://")) {
            iconUri = getUriFromAsset(icon);
        } else if (icon.startsWith("http")){
            iconUri = getUriFromRemote(icon);
        }
        if(iconUri == null){
            this.largeIconBitmap = BitmapFactory.decodeResource(this.context.getResources(), iconId);
            return; //uses app default
        }
        try {
            InputStream input = this.context.getContentResolver().openInputStream(iconUri);
            this.largeIconBitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            this.largeIconBitmap = BitmapFactory.decodeResource(this.context.getResources(), iconId);
        }
    }

    protected int getInt(JSONObject options, String attributeName){
        try {
            return options.getInt(attributeName);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return 0;
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

    protected boolean getBool(JSONObject options, String attributeName, boolean defaultValue) {
        if(options.isNull(attributeName))
            return defaultValue;
        try {
            return options.getBoolean(attributeName);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return defaultValue;
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

    protected int getResourceIdForDrawable(String resourcePath) {
        int resId = getResourceIdForDrawable(this.context.getPackageName(), resourcePath);
        if (resId == 0)
            resId = getResourceIdForDrawable("android", resourcePath);
        return resId;
    }

    protected int getResourceIdForDrawable(String className, String resourcePath) {
        String drawable = getBaseName(resourcePath);
        try {
            Class<?> cls  = Class.forName(className + ".R$drawable");
            return (Integer) cls.getDeclaredField(drawable).get(Integer.class);
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
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUriFromPath(String path) {
        String absPath = path.replaceFirst("file://", "");
        File file = new File(absPath);
        if (file.exists())
            return Uri.fromFile(file);
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
            return Uri.fromFile(file);
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

    private Uri getUriFromRemote(String path) {
        File file = getTmpFile();
        if (file == null)
            return null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream input = connection.getInputStream();
            FileOutputStream outStream = new FileOutputStream(file);
            copyFile(input, outStream);
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
