var exec = require('cordova/exec');

FirebaseExtendedNotification = {};
FirebaseExtendedNotification.getLastNotificationTappedData = function(success, error){
	exec(success, error, 'FirebaseExtendedNotification', 'getLastNotificationTappedData', []);
}

module.exports = FirebaseExtendedNotification;
