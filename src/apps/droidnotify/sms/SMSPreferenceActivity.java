package apps.droidnotify.sms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "SMS Notifications" applications preference Activity.
 * 
 * @author Camille S�vigny
 */
public class SMSPreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.sms_preferences);
	    this.setContentView(R.layout.sms_preferences);
	    setupCustomPreferences();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Status Bar Notification Settings Preference/Button
		Preference statusBarNotificationSettingsPref = (Preference)findPreference(Constants.SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
		statusBarNotificationSettingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, SMSStatusBarNotificationsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "SMSPreferenceActivity() Status Bar Notifications Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Preference/Button
		Preference customizePref = (Preference)findPreference(Constants.SETTINGS_CUSTOMIZE_PREFERENCE);
		customizePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, SMSCustomizePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "SMSPreferenceActivity() Customize Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
}