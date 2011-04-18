package apps.droidnotify;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;
import android.view.Window;

/**
 * 
 * @author csevigny
 *
 */
public class NotificationInfo {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private int NOTIFICATION_TYPE_PHONE = 0;
	private int NOTIFICATION_TYPE_SMS = 1;
	private int NOTIFICATION_TYPE_MMS = 2;
	private int NOTIFICATION_TYPE_CALENDAR = 3;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private String _fromAddress;
	private String _messageBody;
	private long _timeStamp;
	private long _threadID;
	private long _contactID;
	private String _contactLookupKey;
	private String _contactName;
	private long _photoID;
	private int _messageType;
	private long _messageID;
	private boolean _fromEmailGateway;
	private MessageClass _messageClass;
  
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * Construct SmsMmsMessage given a raw message (created from pdu).
	 */
	public NotificationInfo(Context context, Bundle bundle, int notificationType) {
		if (Log.getDebug()) Log.v("NotificationInfo.NotificationInfo()");
		setContext(context);
		SmsMessage[] msgs = null;
        String messageBody = "";            
        if (bundle != null){
    	    if(notificationType == NOTIFICATION_TYPE_PHONE){
    	    	//TODO - Missed Call
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_SMS){
	            // Retrieve SMS message from bundle.
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	            }
	            SmsMessage sms = msgs[0];
	            setTimeStamp(new java.util.Date().getTime());
	    		setFromAddress(sms.getDisplayOriginatingAddress());
	    		setFromEmailGateway(sms.isEmail());
	    		setMessageClass(sms.getMessageClass());
	    		setMessageType(NOTIFICATION_TYPE_SMS);
	            //Get the entire message body from the new message.
	            for (int i=0; i<msgs.length; i++){                
	                messageBody += msgs[i].getMessageBody().toString();
	            }
	            setMessageBody(messageBody);
	    		loadThreadID(getContext(), getFromAddress());  
	    		loadContactsInfo(getContext(), getFromAddress());
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_MMS){
    	    	//TODO - MMS Message
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
    	    	//TODO - Calendar Reminder
    	    }
        }
	}

	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("NotificationInfo.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("NotificationInfo.getContext()");
	    return _context;
	}

	/**
	 * Set the fromAddress property.
	 */
	public void setFromAddress(String fromAddress) {
		if (Log.getDebug()) Log.v("NotificationInfo.setFromAddress()");
		_fromAddress = fromAddress;
	}
	
	/**
	 * Get the fromAddress property.
	 */
	public String getFromAddress() {
		if (Log.getDebug()) Log.v("NotificationInfo.getFromAddress()");
		return _fromAddress;
	}
	
	/**
	 * Set the messageBody property.
	 */
	public void setMessageBody(String messageBody) {
		if (Log.getDebug()) Log.v("NotificationInfo.setMessageBody()");
		_messageBody = messageBody;
	}
	
	/**
	 * Get the messageBody property.
	 */
	public String getMessageBody() {
		if (Log.getDebug()) Log.v("NotificationInfo.getMessageBody()");
		if (_messageBody == null) {
			_messageBody = "";
	    }
	    return _messageBody;
	}

	/**
	 * Set the timeStamp property.
	 */
	public void setTimeStamp(long timeStamp) {
		if (Log.getDebug()) Log.v("NotificationInfo.setTimeStamp()");
	    _timeStamp = timeStamp;
	}
	
	/**
	 * Get the timeStamp property.
	 */
	public long getTimeStamp() {
		if (Log.getDebug()) Log.v("NotificationInfo.getTimeStamp()");
	    return _timeStamp;
	}
	
	/**
	 * Set the threadID property.
	 */
	public void setThreadID(long threadID) {
		if (Log.getDebug()) Log.v("NotificationInfo.setThreadID()");
	    _threadID = threadID;
	}
	
	/**
	 * Get the threadID property.
	 */
	public long getThreadID() {
		if (Log.getDebug()) Log.v("NotificationInfo.getThreadID()");
	    return _threadID;
	}	
	
	/**
	 * Set the contactID property.
	 */
	public void setContactID(long contactID) {
		if (Log.getDebug()) Log.v("NotificationInfo.setContactID()");
	    _contactID = contactID;
	}
	
	/**
	 * Get the contactID property.
	 */
	public long getContactID() {
		if (Log.getDebug()) Log.v("NotificationInfo.getContactID()");
	    return _contactID;
	}
	
	/**
	 * Set the contactLookupKey property.
	 */
	public void setContactLookupKey(String contactLookupKey) {
		if (Log.getDebug()) Log.v("NotificationInfo.setContactLookupKey()");
		_contactLookupKey = contactLookupKey;
	}
	
	/**
	 * Get the contactLookupKey property.
	 */
	public String getContactLookupKey() {
		if (Log.getDebug()) Log.v("NotificationInfo.getContactLookupKey()");
	    return _contactLookupKey;
	}	

	/**
	 * Set the contactName property.
	 */
	public void setContactName(String contactName) {
		if (Log.getDebug()) Log.v("NotificationInfo.setContactName()");
		_contactName = contactName;
	}
	
	/**
	 * Get the contactName property.
	 */
	public String getContactName() {
		if (Log.getDebug()) Log.v("NotificationInfo.getContactName()");
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
	}

	/**
	 * Set the photoID property.
	 */
	public void setPhotoID(long photoID) {
		if (Log.getDebug()) Log.v("NotificationInfo.setPhotoID()");
		_photoID = photoID;
	}
	
	/**
	 * Get the photoID property.
	 */
	public long getPhotoID() {
		if (Log.getDebug()) Log.v("NotificationInfo.getPhotoID()");
		return _photoID;
	}
	
	/**
	 * Get the messageClass property.
	 */
	public MessageClass getMessageClass() {
		if (Log.getDebug()) Log.v("NotificationInfo.getMessageClass()");
		return _messageClass;
	}

	/**
	 * Set the messageType property.
	 */
	public void setMessageType(int messageType) {
		if (Log.getDebug()) Log.v("NotificationInfo.setMessageType()");
		_messageType = messageType;
	}
	
	/**
	 * Get the messageType property.
	 */
	public int getMessageType() {
		if (Log.getDebug()) Log.v("NotificationInfo.getMessageType()");
		return _messageType;
	}

	/**
	 * Set the messageID property.
	 */
	public void setMessageId(long messageID) {
		if (Log.getDebug()) Log.v("NotificationInfo.setMessageId()");
  		_messageID = messageID;
	}
	
	/**
	 * Get the messageID property.
	 */
	public long getMessageId() {
		if (Log.getDebug()) Log.v("NotificationInfo.getMessageId()");
  		return _messageID;
	}

	/**
	 * Set the fromEmailGateway property.
	 */
	public void setFromEmailGateway(boolean fromEmailGateway) {
		if (Log.getDebug()) Log.v("NotificationInfo.setFromEmailGateway()");
  		_fromEmailGateway = fromEmailGateway;
	}
	
	/**
	 * Get the fromEmailGateway property.
	 */
	public boolean setFromEmailGateway() {
		if (Log.getDebug()) Log.v("NotificationInfo.getFromEmailGateway()");
  		return _fromEmailGateway;
	}	

	/**
	 * Set the messageClass property.
	 */
	public void setMessageClass(MessageClass messageClass) {
		if (Log.getDebug()) Log.v("NotificationInfo.setMessageClass()");
		_messageClass = messageClass;
	}
	
	/**
	 * Get the messageClass property.
	 */
	public MessageClass setMessageClass() {
		if (Log.getDebug()) Log.v("NotificationInfo.getMessageClass()");
  		return _messageClass;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Get the THREAD_ID from the SMS Contract content provider using the SMS message address.
	 */
	private void loadThreadID(Context context, String address){
		if (Log.getDebug()) Log.v("NotificationInfo.getThreadIdByAddress()");
		if (address == null) return;
		final String[] projection = new String[] { "_ID", "THREAD_ID" };
		final String selection = "ADDRESS = " + address;
		final String[] selectionArgs = null;
		final String sortOrder = null;
	    long threadID = 0;
	    Cursor cursor = context.getContentResolver().query(
	    		Uri.parse("content://sms/inbox"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	if(cursor.getCount() > 0){
		    	try {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("THREAD_ID"));
		    			if (Log.getDebug()) Log.v("NotificationInfo.getThreadIdByAddress() Thread_ID Found: " + threadID);
		    		}
		    	} finally {
		    		cursor.close();
		    	}
	    	}else{
	        	cursor.close();
	    	}
	    }
	    setThreadID(threadID);
	}

	/**
	 * Load contact info from the ContactsContract content provider using the SMS message address.
	 */ 
	private void loadContactsInfo(Context context, String smsPhoneNumber){
		PhoneNumber incomingNumber = new PhoneNumber(smsPhoneNumber);
		if (Log.getDebug()) Log.v("NotificationInfo.loadContactsInfo()");
		if (smsPhoneNumber == null) return;
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				projection, 
				selection, 
				selectionArgs, 
				sortOrder);
		while (cursor.moveToNext()) { 
		   String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
		   String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
		   String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		   String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		   String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
		   if (Log.getDebug()) Log.v("NotificationInfo.loadContactsInfo() photoID: " + photoID);
		   if (Integer.parseInt(hasPhone) > 0) { 
				final String[] phoneProjection = null;
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
				final String[] phoneSelectionArgs = null;
				final String phoneSortOrder = null;
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
		      while (phoneCursor.moveToNext()) { 
		    	  String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    	  PhoneNumber contactNumber = new PhoneNumber(phoneNumber);
		    	  if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
		    		  setContactID(Long.parseLong(contactID));
		    		  setContactLookupKey(contactLookupKey);
		    		  setContactName(contactName);
		    		  //setPhotoID(Long.parseLong(photoID));
		    	  }
		      } 
		      phoneCursor.close(); 
		   }
		}
		cursor.close();
	}
	
	
	
	
	
}