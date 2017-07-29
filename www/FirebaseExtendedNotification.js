var exec = require('cordova/exec');
module.exports = {
  getLastNotificationTappedData: function(success, error){
    exec(success, error, 'FirebaseExtendedNotification', 'getLastNotificationTappedData', []);
  },
  closeNotification: function(notificationId, success, error){
    exec(success, error, 'FirebaseExtendedNotification', 'closeNotification', [notificationId]);
  },
  notificationExists: function(notificationId, success, error){
    exec(function(answer){ success(answer === 1); }, error,
      'FirebaseExtendedNotification', 'notificationExists', [notificationId]);
  },
  showNotification: function(dataToReturn, notificationOptions, success, error){
    exec(function(answer){ success(answer === 1); }, error,
      'FirebaseExtendedNotification', 'showNotification', [dataToReturn, notificationOptions]);
  }
};
