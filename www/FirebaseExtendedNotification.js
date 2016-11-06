var exec = require('cordova/exec');

FirebaseExtendedNotification = {};
FirebaseExtendedNotification.getLastNotificationData = function(success, error){
	exec(success, error, 'FirebaseExtendedNotification', 'getLastNotificationData', []);
}

module.exports = FirebaseExtendedNotification;
