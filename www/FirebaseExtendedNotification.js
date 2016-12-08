var exec = require('cordova/exec');
module.exports = FirebaseExtendedNotification = {
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
};
