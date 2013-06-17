package com.bicrement.plugins.localNotification;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bicrement.localNotification.LocalNotification;
import com.bicrement.localNotification.R;

/**
 * The alarm receiver is triggered when a scheduled alarm is fired. This class
 * reads the information in the intent and displays this information in the
 * Android notification bar. The notification uses the default notification
 * sound and it vibrates the phone.
 * 
 * @author dvtoever (original author)
 * 
 * @author Wang Zhuochun(https://github.com/zhuochun)
 */
public class AlarmReceiver extends BroadcastReceiver {

	public static final String TITLE = "ALARM_TITLE";
	public static final String SUBTITLE = "ALARM_SUBTITLE";
	public static final String TICKER_TEXT = "ALARM_TICKER";
	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

	/* Contains time in 24hour format 'HH:mm' e.g. '04:30' or '18:23' */
	public static final String HOUR_OF_DAY = "HOUR_OF_DAY";
	public static final String MINUTE = "MINUTES";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("AlarmReceiver", "AlarmReceiver invoked!");

		final Bundle bundle = intent.getExtras();

		// Retrieve notification details from the intent
		final String tickerText = bundle.getString(TICKER_TEXT);
		final String notificationTitle = bundle.getString(TITLE);
		final String notificationSubText = bundle.getString(SUBTITLE);
		final String notificationId = bundle.getString(NOTIFICATION_ID);

		Log.d("AlarmReceiver", "Process alarm with id: " + notificationId);

		Calendar currentCal = Calendar.getInstance();
		int alarmHour = bundle.getInt(HOUR_OF_DAY);
		int alarmMin = bundle.getInt(MINUTE);
		int currentHour = currentCal.get(Calendar.HOUR_OF_DAY);
		int currentMin = currentCal.get(Calendar.MINUTE);

		if (currentHour != alarmHour && currentMin != alarmMin) {
			/*
			 * If you set a repeating alarm at 11:00 in the morning and it
			 * should trigger every morning at 08:00 o'clock, it will
			 * immediately fire. E.g. Android tries to make up for the
			 * 'forgotten' reminder for that day. Therefore we ignore the event
			 * if Android tries to 'catch up'.
			 */
			Log.d("LocalNotification AlarmReceiver",
					"AlarmReceiver, ignoring alarm since it is due");
			return;
		}

		// Construct the notification and notificationManager objects
		final NotificationManager notificationMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Define the Intent
		final Intent notifyIntent = new Intent(context, LocalNotification.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Notification Builder
		// You will need Support Library to use NotificationCompat.Builder
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
				    .setSmallIcon(R.drawable.ic_launcher)
				    .setContentTitle(notificationTitle)
				    .setContentText(notificationSubText)
				    .setTicker(tickerText)
				    .setDefaults(Notification.DEFAULT_ALL)
				    .setContentIntent(contentIntent)
				    .setAutoCancel(true);

		/*
		 * If you want all reminders to stay in the notification bar, you should
		 * generate a random ID. If you want do replace an existing
		 * notification, make sure the ID below matches the ID that you store in
		 * the alarm intent.
		 */
		final int id = Integer.parseInt(notificationId.substring(
				com.bicrement.plugins.localNotification.LocalNotification.PLUGIN_PREFIX.length()));
		notificationMgr.notify(id, mBuilder.build());
	}
}
