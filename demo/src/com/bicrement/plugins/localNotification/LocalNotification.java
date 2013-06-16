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
 * @author Daniel van 't Oever (original author)
 * 
 * @author Wang Zhuochun(https://github.com/zhuochun)
 */
public class LocalNotification extends CordovaPlugin {

	public static final String PLUGIN_NAME = "LocalNotification";
	public static final String PLUGIN_PREFIX = "LocalNotification_";

	/**
	 * Delegate object that does the actual alarm registration. Is reused by the
	 * AlarmRestoreOnBoot class.
	 */
	private AlarmHelper alarm = null;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		boolean success = false;

		final CordovaInterface cordova = this.cordova;

		alarm = new AlarmHelper(cordova.getActivity().getBaseContext());
		
		Log.d(PLUGIN_NAME, "Plugin execute called with action: " + action);
		
		if (action.equalsIgnoreCase("add")) {
			persistAlarm(args.getInt(0), args);
			
			Log.d(PLUGIN_NAME, "Add Notification with Id: " + args.getInt(0));

			success = this.add(args.getInt(0), args.getString(1), args.getString(2),
					args.getString(3), args.getJSONArray(4), args.getString(5));
		} else if (action.equalsIgnoreCase("cancel")) {
			unpersistAlarm(args.getInt(0));
			
			Log.d(PLUGIN_NAME, "Cancel Notification with Id: " + args.getInt(0));

			success = this.cancelNotification(args.getInt(0));
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
	 */
	public boolean add(int id, String title, String subtitle, String ticker, JSONArray date, String repeat) {
		Calendar calendar = Calendar.getInstance();
		
		if (date.length() != 0) {
			try {
				calendar.set(date.getInt(0), date.getInt(1), date.getInt(2),
						date.getInt(3), date.getInt(4), date.getInt(5));
				
				Log.d(PLUGIN_NAME, "Add Alarm at " + calendar.toString());
			} catch (JSONException e) {
				Log.d(PLUGIN_NAME, "JSONException in add " + calendar.toString());
			}
		}
		
		boolean result = alarm.addAlarm(repeat.equalsIgnoreCase("true"),
				title, subtitle, ticker, PLUGIN_PREFIX + id, calendar);
		
		return result;
	}

	/**
	 * Cancel a specific notification that was previously registered.
	 * 
	 * @param notificationId
	 *            The original ID of the notification that was used when it was
	 *            registered using addNotification()
	 */
	public boolean cancelNotification(int id) {
		Log.d(PLUGIN_NAME, "cancel Notification with id: " + id);

		boolean result = alarm.cancelAlarm(id);

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
	private boolean persistAlarm(int id, JSONArray args) {
		final CordovaInterface cordova = this.cordova;
		
		final Editor alarmSettingsEditor = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE).edit();

		alarmSettingsEditor.putString(PLUGIN_PREFIX + id, args.toString());

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
	private boolean unpersistAlarm(int id) {
		final CordovaInterface cordova = this.cordova;
		
		final Editor alarmSettingsEditor = cordova.getActivity().getBaseContext().getSharedPreferences(
				PLUGIN_NAME, Context.MODE_PRIVATE).edit();

		alarmSettingsEditor.remove(PLUGIN_PREFIX + id);

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
