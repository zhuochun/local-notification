package com.bicrement.plugins.localNotification;

import java.util.Calendar;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * This plugin utilizes the Android AlarmManager in combination with StatusBar
 * notifications. When a local notification is scheduled the alarm manager takes
 * care of firing the event. When the event is processed, a notification is put
 * in the Android status bar.
 * 
 * @author Daniel van 't Oever
 * @author Wang Zhuochun (https://github.com/zhuochun)
 */
public class LocalNotification extends CordovaPlugin {

	public static final String PLUGIN_NAME = "LocalNotification";

	/**
	 * Delegate object that does the actual alarm registration. Is reused by the
	 * AlarmRestoreOnBoot class.
	 */
	private AlarmHelper alarm = null;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		boolean success = false;

		Log.d(PLUGIN_NAME, "Plugin execute called with action: " + action);
		
		final CordovaInterface cordova = this.cordova;

		alarm = new AlarmHelper(cordova.getActivity().getBaseContext());

		final AlarmOptions alarmOptions = new AlarmOptions();
		/*
		 * Determine which action of the plugin needs to be invoked
		 */
		String alarmId = alarmOptions.getNotificationId();

		alarmOptions.parseOptions(args);

		if (action.equalsIgnoreCase("add")) {
			final boolean daily = alarmOptions.isRepeatDaily();
			final String title = alarmOptions.getAlarmTitle();
			final String subTitle = alarmOptions.getAlarmSubTitle();
			final String ticker = alarmOptions.getAlarmTicker();

			persistAlarm(alarmId, args);

			success = this.add(daily, title, subTitle, ticker, alarmId,
					alarmOptions.getCal());
		} else if (action.equalsIgnoreCase("cancel")) {
			unpersistAlarm(alarmId);

			success = this.cancelNotification(alarmId);
		} else if (action.equalsIgnoreCase("cancelall")) {
			unpersistAlarmAll();

			success = this.cancelAllNotifications();
		}

		if (success) {
			callbackContext.success();
		}

		return success;
	}

	/**
	 * Set an alarm
	 * 
	 * @param repeatDaily
	 *            If true, the alarm interval will be set to one day.
	 * @param alarmTitle
	 *            The title of the alarm as shown in the Android notification
	 *            panel
	 * @param alarmSubTitle
	 *            The subtitle of the alarm
	 * @param alarmId
	 *            The unique ID of the notification
	 * @param cal
	 *            A calendar object that represents the time at which the alarm
	 *            should first be started
	 * @return A pluginresult.
	 */
	public boolean add(boolean repeatDaily, String alarmTitle,
			String alarmSubTitle, String alarmTicker, String alarmId,
			Calendar cal) {
		final long triggerTime = cal.getTimeInMillis();
		final String recurring = repeatDaily ? "daily" : "onetime";

		Log.d(PLUGIN_NAME, "Adding " + recurring + " notification: '"
				+ alarmTitle + alarmSubTitle + "' with id: " + alarmId
				+ " at timestamp: " + triggerTime);

		boolean result = alarm.addAlarm(repeatDaily, alarmTitle, alarmSubTitle,
				alarmTicker, alarmId, cal);
		
		return result;
	}

	/**
	 * Cancel a specific notification that was previously registered.
	 * 
	 * @param notificationId
	 *            The original ID of the notification that was used when it was
	 *            registered using addNotification()
	 */
	public boolean cancelNotification(String notificationId) {
		Log.d(PLUGIN_NAME, "cancelNotification: Canceling event with id: "
				+ notificationId);

		boolean result = alarm.cancelAlarm(notificationId);

		return result;
	}

	/**
	 * Cancel all notifications that were created by this plugin.
	 */
	public boolean cancelAllNotifications() {
		Log.d(PLUGIN_NAME,
				"cancelAllNotifications: cancelling all events for this application");
		/*
		 * Android can only unregister a specific alarm. There is no such thing
		 * as cancelAll. Therefore we rely on the Shared Preferences which holds
		 * all our alarms to loop through these alarms and unregister them one
		 * by one.
		 */
		final CordovaInterface cordova = this.cordova;
		
		final SharedPreferences alarmSettings = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE);
		final boolean result = alarm.cancelAll(alarmSettings);

		return result;
	}

	/**
	 * Persist the information of this alarm to the Android Shared Preferences.
	 * This will allow the application to restore the alarm upon device reboot.
	 * Also this is used by the cancelAllNotifications method.
	 * 
	 * @see #cancelAllNotifications()
	 * 
	 * @param optionsArr
	 *            The assumption is that parseOptions has been called already.
	 * 
	 * @return true when successfull, otherwise false
	 */
	private boolean persistAlarm(String alarmId, JSONArray optionsArr) {
		final CordovaInterface cordova = this.cordova;
		
		final Editor alarmSettingsEditor = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE).edit();

		alarmSettingsEditor.putString(alarmId, optionsArr.toString());

		return alarmSettingsEditor.commit();
	}

	/**
	 * Remove a specific alarm from the Android shared Preferences
	 * 
	 * @param alarmId
	 *            The Id of the notification that must be removed.
	 * 
	 * @return true when successfull, otherwise false
	 */
	private boolean unpersistAlarm(String alarmId) {
		final CordovaInterface cordova = this.cordova;
		
		final Editor alarmSettingsEditor = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE).edit();

		alarmSettingsEditor.remove(alarmId);

		return alarmSettingsEditor.commit();
	}

	/**
	 * Clear all alarms from the Android shared Preferences
	 * 
	 * @return true when successfull, otherwise false
	 */
	private boolean unpersistAlarmAll() {
		final CordovaInterface cordova = this.cordova;
		
		final Editor alarmSettingsEditor = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE).edit();

		alarmSettingsEditor.clear();

		return alarmSettingsEditor.commit();
	}
}
