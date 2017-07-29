package com.andretissot.firebaseextendednotification;

import android.os.Bundle;
import com.gae.scaffolder.plugin.*;
import java.util.*;

/**
 * Created by André Augusto Tissot on 15/10/16.
 */

public class NotificationActivity extends FCMPluginActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras == null)
            return;
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("wasTapped", true);
        for (String key : extras.keySet()) {
            String value = extras.getString(key);
            data.put(key, value);
        }
        FirebaseExtendedNotification.setLastNotificationTappedData(data);
    }
}
