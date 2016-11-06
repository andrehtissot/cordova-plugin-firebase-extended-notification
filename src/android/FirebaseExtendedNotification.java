package com.andretissot.firebaseextendednotification;

import org.json.JSONArray;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class FirebaseExtendedNotification extends CordovaPlugin {
    static private Map<String, String> lastNotificationData;
    static private CordovaInterface cordovaInterface;
    public static void setLastNotificationData(Map<String, String> notificationData){
        lastNotificationData = notificationData;
    }
    public static Map<String, String> getLastNotificationData(){
        return lastNotificationData;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordovaInterface = cordova, webView);
    }

    public boolean execute(final String action, final JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {
        if (action.equals("getLastNotificationData")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Map<String, String> lastNotificationData
                            = FirebaseExtendedNotification.getLastNotificationData();
                        if(lastNotificationData == null){
                            callbackContext.error("null value");
                        } else {
                            callbackContext.success(new JSONObject(lastNotificationData));
                        }
                    } catch (Exception e){
                        callbackContext.error(e.getMessage());
                    }
                }
            });
        } else return false;
        return true;
    }
}
