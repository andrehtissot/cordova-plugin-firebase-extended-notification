module.exports = function(context) {
  var fs = require('fs'), filename = context.opts.projectRoot+"/platforms/android/AndroidManifest.xml";
  fs.readFile(filename, 'utf8', function(err, sourceText) {
    if (err) return; // On error, fail silently.
    var duplicatedString =
'        <activity android:exported="true" android:launchMode="singleTop" android:name="com.andretissot.firebaseextendednotification.NotificationActivity" android:screenOrientation="portrait">\n'+
'            <intent-filter>\n'+
'                <category android:name="android.intent.category.DEFAULT" />\n'+
'            </intent-filter>\n'+
'        </activity>\n';
    var text = sourceText.split(duplicatedString);
    if(text.length === 1) {
      return; // Duplicate not found, nothing to do here.
    }
    text[0] += duplicatedString;
    fs.writeFile(filename, text.join(''), function(err) {
      if (err) return; // On error, fail silently.
    });
  });
}
