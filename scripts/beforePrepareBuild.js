var fs = require('fs');

var legacyPrepareBuild = function(legacyFilePath) {
  fs.readFile(legacyFilePath, 'utf8', function(err, sourceText) {
    if (err) return; // On error, fail silently.
    var stringToInsert1 = '\
        <activity android:exported="true" android:launchMode="singleTop" android:name="com.andretissot.firebaseextendednotification.NotificationActivity">\n\
            <intent-filter>\n\
                <category android:name="android.intent.category.DEFAULT" />\n\
            </intent-filter>\n\
        </activity>\n',
      stringToInsert2 = '\
        <service android:name="com.andretissot.firebaseextendednotification.MessagingService">\n\
            <intent-filter>\n\
                <action android:name="com.google.firebase.MESSAGING_EVENT" />\n\
            </intent-filter>\n\
        </service>\n';
    var text = sourceText, textLenght = 0;
    while(text.length != textLenght) {
      textLenght = text.length;
      text = text.replace(stringToInsert1, '');
    }
    textLenght = 0;
    while(text.length != textLenght) {
      textLenght = text.length;
      text = text.replace(stringToInsert2, '');
    }
    var indexFound = text.lastIndexOf('        <activity', text.indexOf('FCMPluginActivity'));
    if(indexFound !== -1)
      text = text.substring(0, indexFound)+stringToInsert1+stringToInsert2+text.substring(indexFound);
    fs.writeFile(legacyFilePath, text, function(err) {
      if (err) return; // On error, fail silently.
    });
  });
}

var prepareBuild = function(filePath) {
  fs.readFile(filePath, 'utf8', function(err, sourceText) {
    if (err) return; // On error, fail silently.
    var stringToInsert1 = '\
        <activity android:exported="true" android:launchMode="singleTop" android:name="com.gae.scaffolder.plugin.FCMPluginActivity">\n\
            <intent-filter>\n\
                <action android:name="FCM_PLUGIN_ACTIVITY" />\n\
                <category android:name="android.intent.category.DEFAULT" />\n\
            </intent-filter>\n\
        </activity>\n';
    var stringToInsert2 = '\
        <service android:name="com.gae.scaffolder.plugin.MyFirebaseMessagingService">\n\
            <intent-filter>\n\
                <action android:name="com.google.firebase.MESSAGING_EVENT" />\n\
            </intent-filter>\n\
        </service>\n';
    var text = sourceText, textLenght = 0;
    while(text.length != textLenght) {
      textLenght = text.length;
      text = text.replace(stringToInsert1, '');
    }
    textLenght = 0;
    while(text.length != textLenght) {
      textLenght = text.length;
      text = text.replace(stringToInsert2, '');
    }
    fs.writeFile(filePath, text, function(err) {
      if (err) return; // On error, fail silently.
    });
  });
}

module.exports = function(context) {
  var legacyFilePath = context.opts.projectRoot+"/platforms/android/AndroidManifest.xml",
    filePath = context.opts.projectRoot+"/platforms/android/app/src/main/AndroidManifest.xml";
  if (fs.existsSync(legacyFilePath)) {
    legacyPrepareBuild(legacyFilePath);
  } else if(fs.existsSync(filePath)) {
    prepareBuild(filePath);
  } else {
    console.error("AndroidManifest.xml file not found!")
  }
}
