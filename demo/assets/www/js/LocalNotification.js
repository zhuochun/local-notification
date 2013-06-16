/*-
 * Phonegap LocalNotification Plugin for Android
 *
 * Support Phonegap 2.x.x
 * 
 * Originally Created by Daniel van 't Oever 2012 MIT Licensed
 * 
 * Edited by Zhuochun (https://github.com/zhuochun)
 * 
 * Usage: 
 *
 * var notification = cordova.require("cordova/plugin/localNotification");
 * 
 * notification.add({ date: new Date(),
 *                    message: 'This is an Android alarm using the statusbar',
 *                    id: 123 });
 * notification.cancel(123); 
 * notification.cancelAll();
 * 
 * This interface is similar to the existing iOS LocalNotification plugin created by Greg Allen
 */

cordova.define('cordova/plugin/localNotification', function(require, exports, module) {    

    var exec = require("cordova/exec");

	/**
	 * Empty constructor
	 */
	var LocalNotification = function() {
	};

	/**
	 * Register a notification message for a specific date / time
	 * 
	 * @param successCB
	 * @param failureCB
	 * @param options
	 *            Array with arguments. Valid arguments are date, message,
	 *            repeatDaily and id
	 */
	LocalNotification.prototype.add = function(options, succeed, failed) {
		var defaults = {
			date : new Date(),
			message : '',
			ticker : '',
			repeatDaily : false,
			id : ""
		};

		if (options.date) {
			options.date = (options.date.getMonth()) + "/" + (options.date.getDate()) + "/"
					+ (options.date.getFullYear()) + "/" + (options.date.getHours()) + "/"
					+ (options.date.getMinutes());
		}

		for ( var key in defaults) {
			if (typeof options[key] !== "undefined")
				defaults[key] = options[key];
		}

		exec(succeed, failed, 'LocalNotification', 'add', new Array(defaults));
	};

	/**
	 * Cancel an existing notification using its original ID.
	 * 
	 * @param id
	 *            The ID that was used when creating the notification using the
	 *            'add' method.
	 */
	LocalNotification.prototype.cancel = function(id, succeed, failed) {
		exec(succeed, failed, 'LocalNotification', 'cancel', new Array({
			id : id
		}));
	};

	/**
	 * Cancel all notifications that were created by your application.
	 */
	LocalNotification.prototype.cancelAll = function(succeed, failed) {
		exec(succeed, failed, 'LocalNotification', 'cancelAll', new Array());
	};

    module.exports = new LocalNotification();

});
