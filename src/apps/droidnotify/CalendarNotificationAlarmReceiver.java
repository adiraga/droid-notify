package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class listens for scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille S�vigny
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATIONS_ENABLED = "reschedule_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	private static final String USER_IN_MESSAGING_APP = "user_in_messaging_app";	
	private static final String MESSAGING_APP_RUNNING_ACTION_CALENDAR = "messaging_app_running_action_calendar";
	
	private static final String MESSAGING_APP_RUNNING_ACTION_RESCHEDULE = "0";
	private static final String MESSAGING_APP_RUNNING_ACTION_IGNORE = "1";
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that the Calendar should be checked.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarNotificationAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (_debug) Log.v("CalendarNotificationAlarmReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!preferences.getBoolean(CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("CalendarNotificationAlarmReceiver.onReceive() Calendar Notifications Disabled. Exiting... ");
			return;
		}
	    //Check the state of the users phone.
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean rescheduleNotification = false;
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    boolean inMessagingApp = preferences.getBoolean(USER_IN_MESSAGING_APP, false);
	    boolean messagingAppRunning = Common.isMessagingAppRunning(context);
	    String messagingAppRuningAction = preferences.getString(MESSAGING_APP_RUNNING_ACTION_CALENDAR, "0");
	    if(!callStateIdle || inMessagingApp){
	    	rescheduleNotification = true;
	    }else{
	    	//Messaging App is running.
	    	if(messagingAppRunning){
	    		//Reschedule notification based on the users preferences.
			    if(messagingAppRuningAction.equals(MESSAGING_APP_RUNNING_ACTION_RESCHEDULE)){
					rescheduleNotification = true;
			    }
			    //Ignore notification based on the users preferences.
			    if(messagingAppRuningAction.equals(MESSAGING_APP_RUNNING_ACTION_IGNORE)){
			    	return;
			    }
	    	}
	    }
	    if(!rescheduleNotification){
			WakefulIntentService.acquireStaticLock(context);
			Intent calendarIntent = new Intent(context, CalendarNotificationAlarmReceiverService.class);
			calendarIntent.putExtras(intent.getExtras());
			context.startService(calendarIntent);
	    }else{
	    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
	    	long rescheduleInterval = Long.parseLong(preferences.getString(RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
	    	if(preferences.getBoolean(RESCHEDULE_NOTIFICATIONS_ENABLED, true)){
	    		if(rescheduleInterval == 0){
	    			SharedPreferences.Editor editor = preferences.edit();
	    			editor.putBoolean(RESCHEDULE_NOTIFICATIONS_ENABLED, false);
	    			editor.commit();
	    			return;
	    		}
	    		if (_debug) Log.v("CalendarNotificationAlarmReceiver.onReceive() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent calendarIntent = new Intent(context, CalendarNotificationAlarmReceiver.class);
				calendarIntent.putExtras(intent.getExtras());
				calendarIntent.setAction("apps.droidnotify.VIEW/CalendarReschedule/" + System.currentTimeMillis());
				PendingIntent calendarPendingIntent = PendingIntent.getBroadcast(context, 0, calendarIntent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, calendarPendingIntent);
	    	}
	    }
	}
	
}