# cordova-plugin-firebase-extended-notification
Simple notification message from server as "data" that allows more options as how it will apear to the notified user.

The default notification from your server to android devices is limited to the options "body", "title" and "icon", like the exemple below:

```
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
```
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "title" : "Title test",
      "id" : 4,
      "text" : "Test message",
      "smallIcon" : "drawable/icon",
      "largeIcon" : "https://avatars2.githubusercontent.com/u/1174345?v=3&s=96",
      "autoCancel" : true
    }
  }
}
```

Or if you want more than one line of text when notification is "opened":
```
{
  "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
  "data" : {
    "dataValuesToGetWhenClickedOn" : 111,
    "notificationOptions" : {
      "title" : "Title test",
      "id" : 4,
      "textLines" : ["Message 1", "Message 2"],
      "summary" : "2 messages",
      "smallIcon" : "drawable/icon",
      "largeIcon" : "https://avatars2.githubusercontent.com/u/1174345?v=3&s=96",
      "autoCancel" : true
    }
  }
}
```
