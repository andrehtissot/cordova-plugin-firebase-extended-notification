# cordova-plugin-firebase-extended-notification
Simple notification message from server as "data" that allows more options as how it will apear to the notified user.

For a practical example, look into https://github.com/andrehtissot/cordova-plugin-firebase-extended-notification-app-example.
Check the wiki before creating an issue.

The default notification from your server to android devices is limited to the options "body", "title" and "icon", like the exemple below:

```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "notification" : {
    "body" : "great match!",
    "title" : "Portugal vs. Denmark",
    "icon" : "myicon"
  }
}
```
References: https://firebase.google.com/docs/cloud-messaging/concept-options#notifications_and_data_messages

This plugin allows a nicer notification without cordova configurations, like the example below:
```javascript
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "id" : 4,
      "title" : "Title test",
      "text" : "Test message",
      "smallIcon" : "drawable/icon",
      "largeIcon" : "https://avatars2.githubusercontent.com/u/1174345?v=3&s=96",
      "autoCancel" : true,
      "vibrate": [200,300,200,300],
      "color": "0000ff",
      "sound": true
    }
  }
}
```

Or if you want more than one line of text when notification is "opened":
```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "id" : 4,
      "title" : "Title test",
      "textLines" : ["Message 1", "Message 2"],
      "summary" : "2 messages",
      "smallIcon" : "drawable/icon",
      "largeIcon" : "https://avatars2.githubusercontent.com/u/1174345?v=3&s=96",
      "autoCancel" : true,
      "vibrate": [200,300,200,300],
      "sound": true
    }
  }
}
```

Or if you want to show a big picture when notification is "opened":
```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "id" : 4,
      "...": "...",
      "bigPicture": "https://cloud.githubusercontent.com/assets/7321362/24875178/1e58d2ec-1ddc-11e7-96ed-ed8bf011146c.png"
    }
  }
}
```

Or if you want to open the app when notification is received:
```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions": {
      "id" : 4,
      "...": "...",
      "openApp": true
    }
  }
}
```


#### Observations:
* At least for now, `bigPicture` and `textLines` are mutually exclusive.
* To get the default vibration, use `true` instead of array.
* To set a background color, use ARGB value as integer like `0x000000ff` (less readable `255`), or as string like `"000000ff"`, with alpha, or `"0000ff"`, without alpha.
* For sound:
  * To play the default sound, use `true`;
  * To play a sound from resource, start with `res://`. For example `res://raw/music_mp3` will play the `R.raw.music_mp3` if available.
  * To play a sound from the web, start with `http://` or `https://`. For example `http://tindeck.com/download/pro/yjuow/Not_That_Guy.mp3` will download the file in cache and play it.

#### Make sure that in Android SDK Manager it is installed:
* Android Support Library version 23 or greater
* Android Support Repository version 20 or greater
* Google Play Services version 27 or greater
* Google Repository version 22 or greater
