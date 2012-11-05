package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.sms.SMSCommon;

public class MMSAlarmBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSAlarmBroadcastReceiverService() {
		super("MMSAlarmBroadcastReceiverService");
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
		Context context = getApplicationContext();
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				Log.e(context, "MMSAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Check for a blacklist entry before doing anything else.
		    Bundle mmsNotificationBundle = SMSCommon.getMMSMessagesFromDisk(context); 
    		Bundle mmsNotificationBundleSingle = null;
    		if(mmsNotificationBundle != null){
    			mmsNotificationBundleSingle = mmsNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
    		}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, MMSService.class));
		    }else{	    		 	
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
	    			if(mmsNotificationBundleSingle != null){
						//Display Status Bar Notification
					    Common.setStatusBarNotification(context, 1, Constants.NOTIFICATION_TYPE_MMS, 0, callStateIdle, mmsNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), mmsNotificationBundleSingle.getLong(Constants.BUNDLE_CONTACT_ID, -1), mmsNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), mmsNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, null, mmsNotificationBundleSingle.getLong(Constants.BUNDLE_THREAD_ID, -1), false, Common.getStatusBarNotificationBundle(context, Constants.NOTIFICATION_TYPE_MMS));
	    			}
			    }					
		    	if(mmsNotificationBundle != null) Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, Constants.NOTIFICATION_TYPE_MMS, mmsNotificationBundle);
		    }
		}catch(Exception ex){
			Log.e(context, "MMSAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}