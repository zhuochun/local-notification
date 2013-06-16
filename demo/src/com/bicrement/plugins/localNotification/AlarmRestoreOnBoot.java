package com.bicrement.plugins.localNotification;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class is triggered upon reboot of the device. It needs to re-register
 * the alarms with the AlarmManager since these alarms are lost in case of
 * reboot.
 * 
 * @author dvtoever
 */
public class AlarmRestoreOnBoot extends BroadcastReceiver {
	
	private AlarmHelper alarm = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		alarm = new AlarmHelper(context);

		// Obtain alarm details form Shared Preferences
		final SharedPreferences alarmSettings = context.getSharedPreferences(
				LocalNotification.PLUGIN_NAME, Context.MODE_PRIVATE);
		final Map<String, ?> allAlarms = alarmSettings.getAll();
		final Set<String> alarmIds = allAlarms.keySet();

		/*
		 * For each alarm, parse its alarm options and register is again with
		 * the Alarm Manager
		 */
		for (String alarmId : alarmIds) {
			try {
				this.processAlarm(new JSONArray(alarmSettings.getString(alarmId, "")));
			} catch (JSONException e) {
				Log.d(LocalNotification.PLUGIN_NAME,
						"AlarmRestoreOnBoot: Error while restoring alarm details after reboot: "
								+ e.toString());
			}
		}
		
		Log.d(LocalNotification.PLUGIN_NAME,
				"AlarmRestoreOnBoot: Successfully restored alarms upon reboot");
	}
	
	public boolean processAlarm(JSONArray args) throws JSONException {
		return this.add(args.getInt(0), args.getString(1), args.getString(2),
				args.getString(3), args.getJSONArray(4), args.getString(5));
	}
	
	public boolean add(int id, String title, String subtitle, String ticker, JSONArray date, String repeat) {
		Calendar calendar = Calendar.getInstance();
		
		if (date.length() != 0) {
			try {
				calendar.set(date.getInt(0), date.getInt(1), date.getInt(2),
						date.getInt(3), date.getInt(4), date.getInt(5));
			} catch (JSONException e) {
				
			}
		}
		
		boolean result = alarm.addAlarm(repeat.equalsIgnoreCase("true"),
				title, subtitle, ticker, LocalNotification.PLUGIN_PREFIX + id, calendar);
		
		return result;
	}
}
