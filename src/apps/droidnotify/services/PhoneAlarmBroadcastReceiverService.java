package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.receivers.PhoneAlarmReceiver;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille S�vigny
 */
public class PhoneAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public PhoneAlarmBroadcastReceiverService() {
		super("PhoneAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.PhoneAlarmBroadcastReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if missed call notifications are disabled.
		    if(!preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.doWakefulWork() Missed Call Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
			TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotification = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    String blockingAppRuningAction = preferences.getString(Constants.PHONE_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{		    	
		    	notificationIsBlocked = Common.isNotificationBlocked(context, blockingAppRuningAction);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, PhoneService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
			    	//Get the missed call info.
	    			String phoneNumber = null;
	    			String contactName = null;
		    		ArrayList<String> missedCallsArray = PhoneCommon.getMissedCalls(context);
		    		if((missedCallsArray != null) && (missedCallsArray.size() > 0)){
			    		String missedCallArrayItem = missedCallsArray.get(0);
		    			String[] missedCallInfo = missedCallArrayItem.split("\\|");
		    			int arraySize = missedCallInfo.length;
		    			if(arraySize > 0){
			    			if(arraySize >= 2) phoneNumber = missedCallInfo[1];
			    			if(arraySize >= 5) contactName = missedCallInfo[4];
		    			}
		    		}
					//Display Status Bar Notification
	    			Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_PHONE, 0, callStateIdle, contactName, phoneNumber, null, null);
			    }
		    	//Ignore notification based on the users preferences.
		    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		rescheduleNotification = false;
		    		return;
		    	}
		    	if(rescheduleNotification){
			    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
			    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("PhoneAlarmBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
					String intentActionText = "apps.droidnotify.alarm/PhoneAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
					long alarmTime = System.currentTimeMillis() + rescheduleInterval;
					Common.startAlarm(context, PhoneAlarmReceiver.class, null, intentActionText, alarmTime);
		    	}
		    }
	    }catch(Exception ex){
			Log.e("PhoneAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}