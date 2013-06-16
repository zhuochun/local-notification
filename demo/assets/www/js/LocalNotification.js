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
     * Private Helper
     */
    function _convertDateToArray(date) {
        if (!date) {
            return [];
        }

        var t = [];

        t.push( date.getFullYear() );
        t.push( date.getMonth() );
        t.push( date.getDate() );
        t.push( date.getHours() );
        t.push( date.getMinutes() );
        t.push( date.getSeconds() );
        t.push( date.getMilliseconds() );

        return t;
    }

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
		var params = [];
        
        params.push( options.id || 0 ); // id
        params.push( options.message || "" ); // message
        params.push( options.subtitle || "" ); // subtitle
        params.push( options.ticker || "" ); // ticker
        params.push( _convertDateToArray(options.date) ); // date
        params.push( options.repeatDaily ? "true" : "false" ); // repeat

		exec(succeed, failed, 'LocalNotification', 'add', params);
	};

	/**
	 * Cancel an existing notification using its original ID.
	 * 
	 * @param id
	 *            The ID that was used when creating the notification using the
	 *            'add' method.
	 */
	LocalNotification.prototype.cancel = function(id, succeed, failed) {
		exec(succeed, failed, 'LocalNotification', 'cancel', [id]);
	};

	/**
	 * Cancel all notifications that were created by your application.
	 */
	LocalNotification.prototype.cancelAll = function(succeed, failed) {
		exec(succeed, failed, 'LocalNotification', 'cancelAll', []);
	};

    module.exports = new LocalNotification();

});
