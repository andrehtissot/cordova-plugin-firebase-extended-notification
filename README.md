# cordova-plugin-firebase-extended-notification

[![npm](https://img.shields.io/npm/dt/cordova-plugin-firebase-extended-notification.svg)](https://www.npmjs.com/package/cordova-plugin-firebase-extended-notification)
[![npm](https://img.shields.io/npm/v/cordova-plugin-firebase-extended-notification.svg)](https://www.npmjs.com/package/cordova-plugin-firebase-extended-notification)
[![GitHub license](https://img.shields.io/github/license/andrehtissot/cordova-plugin-firebase-extended-notification.svg)](https://github.com/andrehtissot/cordova-plugin-firebase-extended-notification/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/andrehtissot/cordova-plugin-firebase-extended-notification.svg)](https://github.com/andrehtissot/cordova-plugin-firebase-extended-notification/issues)
[![GitHub forks](https://img.shields.io/github/forks/andrehtissot/cordova-plugin-firebase-extended-notification.svg)](https://github.com/andrehtissot/cordova-plugin-firebase-extended-notification/network)
[![GitHub stars](https://img.shields.io/github/stars/andrehtissot/cordova-plugin-firebase-extended-notification.svg)](https://github.com/andrehtissot/cordova-plugin-firebase-extended-notification/stargazers)


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
      "headsUp": true,
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

Or if you want to show a big text when notification is "opened":
```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "id" : 4,
      "...": "...",
      "bigText": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris mollis urna sed nisl venenatis, a tincidunt orci iaculis. In hac habitasse platea dictumst. Nulla quis hendrerit risus. Morbi neque lectus, laoreet quis dui quis, luctus blandit mauris. Sed ullamcorper risus et lorem facilisis, sit amet tristique nulla rutrum. Vivamus auctor pulvinar ligula, tempor lacinia arcu commodo in. Ut condimentum dolor ac felis venenatis, sit amet cursus erat accumsan. Aliquam a justo elit. Maecenas dignissim suscipit ipsum, nec laoreet velit."
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

#### Channels for Android 8+
From the version 1.9.0 on, it's possible to define in which notification channel the notification will be linked to:
```json
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions": {
      "id" : 4,
      "...": "...",
      "channelId": "music",
      "channelName": "Music",
      "channelDescription": "Notifications for new musics available"
    }
  }
}
```

#### Creating notifications locally
From the version 1.7.0 on, it's possible to "simulate" the extended Firebase support using the same options, called like:
```javascript
FirebaseExtendedNotification.showNotification({
  "dataValuesToGetWhenClickedOn" : 111
}, {
  "title" : "Title test",
  "textLines" : ["Message 1", "Message 2"],
  "summary" : "2 messages",
  "..." : "..."
});
```

#### JSON.stringified notificationOptions
From the version 1.10.0 on, it's possible to send as string instead of object, called like:
```javascript
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : JSON.stringify({
      "id" : 4,
      "title" : "Title test",
      "text" : "Test message",
      "smallIcon" : "drawable/icon",
      "largeIcon" : "https://avatars2.githubusercontent.com/u/1174345?v=3&s=96",
      "autoCancel" : true,
      "vibrate": [200,300,200,300],
      "color": "0000ff",
      "headsUp": true,
      "sound": true
    })
  }
}
```

#### Observations:
* At least for now, `bigPicture`, `bigText` and `textLines` are mutually exclusive.
* To get the default vibration, use `true` instead of array.
* To set a background color, use ARGB value as integer like `0x000000ff` (less readable `255`), or as string like `"000000ff"`, with alpha, or `"0000ff"`, without alpha, or for convenience `"#0000ff"`.
* For sound:
  * To play the default sound, use `true`;
  * To play a sound from resource, start with `res://`. For example `res://raw/music_mp3` will play the `R.raw.music_mp3` if available.
  * To play a sound from the web, start with `http://` or `https://`. For example `http://tindeck.com/download/pro/yjuow/Not_That_Guy.mp3` will download the file in cache and play it.

#### Make sure that in Android SDK Manager it is installed:
* Android Support Library version 23 or greater
* Android Support Repository version 20 or greater
* Google Play Services version 27 or greater
* Google Repository version 22 or greater
