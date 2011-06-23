package apps.droidnotify.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import apps.droidnotify.Log;

/**
 * 
 * @author xqs230cs
 *
 */
public class AboutDialogPreference extends DialogPreference {

	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Class Constructor.
	 * 
	 * @param context - Context
	 * @param attrs - AttributeSet
	 */
	public AboutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Log.getDebug()) Log.v("AboutDialogPreference.AboutDialogPreference(Context context, AttributeSet attrs)");
	}

	/**
	 * Class Constructor.
	 * 
	 * @param context - Context
	 * @param attrs - AttributeSet
	 * @param defStyle - int
	 */
	public AboutDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (Log.getDebug()) Log.v("AboutDialogPreference.AboutDialogPreference(Context context, AttributeSet attrs, int defStyle)");
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}