var fs = require("fs");

var removeText = function(sourceText, stringToRemove) {
  var text = sourceText;
  var textLenght = 0;
  while (text.length != textLenght) {
    textLenght = text.length;
    text = text.replace(stringToRemove, "");
  }
  return text;
};

var prepareBuild = function(filePath) {
  fs.readFile(filePath, "utf8", function(err, sourceText) {
    if (err) return; // On error, fail silently.
    var text = removeText(
      sourceText,
      '\
        <activity android:exported="true" android:launchMode="singleTop" android:name="com.gae.scaffolder.plugin.FCMPluginActivity">\n\
            <intent-filter>\n\
                <action android:name="FCM_PLUGIN_ACTIVITY" />\n\
                <category android:name="android.intent.category.DEFAULT" />\n\
            </intent-filter>\n\
        </activity>\n'
    );
    text = removeText(
      text,
      '\
        <service android:name="com.gae.scaffolder.plugin.MyFirebaseMessagingService" android:stopWithTask="false">\n\
            <intent-filter>\n\
                <action android:name="com.google.firebase.MESSAGING_EVENT" />\n\
            </intent-filter>\n\
        </service>\n'
    );
    fs.writeFile(filePath, text, function(err) {
      if (err) return; // On error, fail silently.
    });
  });
};

module.exports = function(context) {
  var filePath =
    context.opts.projectRoot +
    "/platforms/android/app/src/main/AndroidManifest.xml";
  if (fs.existsSync(filePath)) {
    prepareBuild(filePath);
  } else {
    throw new Error("AndroidManifest.xml file not found!");
  }
};
